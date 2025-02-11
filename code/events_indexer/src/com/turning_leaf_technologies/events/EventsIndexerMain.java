package com.turning_leaf_technologies.events;

import com.turning_leaf_technologies.config.ConfigUtil;
import com.turning_leaf_technologies.file.JarUtil;
import com.turning_leaf_technologies.logging.LoggingUtil;
import com.turning_leaf_technologies.strings.AspenStringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.ini4j.Ini;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EventsIndexerMain {
	private static Logger logger;
	public static void main(String[] args) {
		String serverName;
		if (args.length == 0) {
			serverName = AspenStringUtils.getInputFromCommandLine("Please enter the server name");
			if (serverName.length() == 0) {
				System.out.println("You must provide the server name as the first argument.");
				System.exit(1);
			}
		} else {
			serverName = args[0];
		}

		String processName = "events_indexer";
		logger = LoggingUtil.setupLogging(serverName, processName);

		//Get the checksum of the JAR when it was started so we can stop if it has changed.
		long myChecksumAtStart = JarUtil.getChecksumForJar(logger, processName, "./" + processName + ".jar");
		long timeAtStart = new Date().getTime();

		while (true) {
			Date startTime = new Date();
			Long startTimeForLogging = startTime.getTime() / 1000;
			logger.info("Starting " + processName + ": " + startTime.toString());

			// Read the base INI file to get information about the server (current directory/cron/config.ini)
			Ini configIni = ConfigUtil.loadConfigFile("config.ini", serverName, logger);

			//Connect to the aspen database
			Connection aspenConn = connectToDatabase(configIni);

			try {
				String solrPort = configIni.get("Reindex", "solrPort");
				ConcurrentUpdateSolrClient solrUpdateServer = setupSolrClient(solrPort);

				// LibraryMarket LibraryCalendar
				PreparedStatement getEventsSitesToIndexStmt = aspenConn.prepareStatement("SELECT * from lm_library_calendar_settings");
				ResultSet eventsSitesRS = getEventsSitesToIndexStmt.executeQuery();
				while (eventsSitesRS.next()) {
					LibraryMarketLibraryCalendarIndexer indexer = new LibraryMarketLibraryCalendarIndexer(
							eventsSitesRS.getLong("id"),
							eventsSitesRS.getString("name"),
							eventsSitesRS.getString("baseUrl"),
							eventsSitesRS.getString("clientId"),
							eventsSitesRS.getString("clientSecret"),
							eventsSitesRS.getString("username"),
							eventsSitesRS.getString("password"),
							solrUpdateServer, aspenConn, logger);
					indexer.indexEvents();
				}

				// Springshare LibCal
				getEventsSitesToIndexStmt = aspenConn.prepareStatement("SELECT * from springshare_libcal_settings");
				eventsSitesRS = getEventsSitesToIndexStmt.executeQuery();
				while (eventsSitesRS.next()) {
					SpringshareLibCalIndexer indexer = new SpringshareLibCalIndexer(
							eventsSitesRS.getLong("id"),
							eventsSitesRS.getString("name"),
							eventsSitesRS.getString("baseUrl"),
							eventsSitesRS.getString("calId"),
							eventsSitesRS.getString("clientId"),
							eventsSitesRS.getString("clientSecret"),
							solrUpdateServer, aspenConn, logger);
					indexer.indexEvents();
				}

					//Index events from other source here
			} catch (SQLException e) {
				logger.error("Error indexing events", e);
			}

			//Check to see if the jar has changes, and if so quit
			if (myChecksumAtStart != JarUtil.getChecksumForJar(logger, processName, "./" + processName + ".jar")){
				break;
			}
			//Check to see if it's between midnight and 1 am and the jar has been running more than 15 hours.  If so, restart just to clean up memory.
			GregorianCalendar nowAsCalendar = new GregorianCalendar();
			Date now = new Date();
			nowAsCalendar.setTime(now);
			if (nowAsCalendar.get(Calendar.HOUR_OF_DAY) <=1 && (now.getTime() - timeAtStart) > 15 * 60 * 60 * 1000 ){
				logger.info("Ending because we have been running for more than 15 hours and it's between midnight and one AM");
				break;
			}

			//Pause 15 minutes before running the next export
			try {
				Thread.sleep(1000 * 60 * 15);
			} catch (InterruptedException e) {
				logger.info("Thread was interrupted");
			}
		}
	}

	private static ConcurrentUpdateSolrClient setupSolrClient(String solrPort) {
		ConcurrentUpdateSolrClient.Builder solrBuilder = new ConcurrentUpdateSolrClient.Builder("http://localhost:" + solrPort + "/solr/events");
		solrBuilder.withThreadCount(1);
		solrBuilder.withQueueSize(25);
		ConcurrentUpdateSolrClient updateServer = solrBuilder.build();
		updateServer.setRequestWriter(new BinaryRequestWriter());

		return updateServer;
	}

	private static Connection connectToDatabase(Ini configIni) {
		Connection aspenConn = null;
		try {
			String databaseConnectionInfo = ConfigUtil.cleanIniValue(configIni.get("Database", "database_aspen_jdbc"));
			if (databaseConnectionInfo != null) {
				aspenConn = DriverManager.getConnection(databaseConnectionInfo);
			} else {
				logger.error("Aspen database connection information was not provided");
				System.exit(1);
			}

		} catch (Exception e) {
			logger.error("Error connecting to aspen database", e);
			System.exit(1);
		}
		return aspenConn;
	}
}
