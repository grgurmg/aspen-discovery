package com.turning_leaf_technologies.website_indexer;

import com.turning_leaf_technologies.logging.BaseLogEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class WebsiteIndexLogEntry implements BaseLogEntry {
	private Long logEntryId = null;
	private String websiteName;
	private Date startTime;
	private Date endTime;
	private ArrayList<String> notes = new ArrayList<>();
	private int numPages = 0;
	private int numAdded = 0;
	private int numDeleted = 0;
	private int numUpdated = 0;
	private int numErrors = 0;
	private Logger logger;

	WebsiteIndexLogEntry(String websiteName, Connection dbConn, Logger logger){
		this.logger = logger;
		this.startTime = new Date();
		this.websiteName = websiteName;
		try {
			insertLogEntry = dbConn.prepareStatement("INSERT into website_index_log (startTime, websiteName) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			updateLogEntry = dbConn.prepareStatement("UPDATE website_index_log SET lastUpdate = ?, endTime = ?, notes = ?, numPages = ?, numAdded = ?, numUpdated = ?, numDeleted = ?, numErrors = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			logger.error("Error creating prepared statements to update log", e);
		}
		saveResults();
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//Synchronized to prevent concurrent modification of the notes ArrayList
	public synchronized void addNote(String note) {
		Date date = new Date();
		this.notes.add(dateFormat.format(date) + " - " + note);
		saveResults();
	}

	private String getNotesHtml() {
		StringBuilder notesText = new StringBuilder("<ol class='cronNotes'>");
		for (String curNote : notes){
			String cleanedNote = curNote;
			cleanedNote =  StringUtils.replace(cleanedNote, "<pre>", "<code>");
			cleanedNote = StringUtils.replace(cleanedNote,"</pre>", "</code>");
			//Replace multiple line breaks
			cleanedNote = cleanedNote.replaceAll("(?:<br?>\\s*)+", "<br/>");
			cleanedNote = cleanedNote.replaceAll("<meta.*?>", "");
			cleanedNote = cleanedNote.replaceAll("<title>.*?</title>", "");
			notesText.append("<li>").append(cleanedNote).append("</li>");
		}
		notesText.append("</ol>");
		String returnText = notesText.toString();
		if (returnText.length() > 25000){
			returnText = returnText.substring(0, 25000) + " more data was truncated";
		}
		return returnText;
	}

	private static PreparedStatement insertLogEntry;
	private static PreparedStatement updateLogEntry;
	public boolean saveResults() {
		try {
			if (logEntryId == null){
				insertLogEntry.setLong(1, startTime.getTime() / 1000);
				insertLogEntry.setString(2, websiteName);
				insertLogEntry.executeUpdate();
				ResultSet generatedKeys = insertLogEntry.getGeneratedKeys();
				if (generatedKeys.next()){
					logEntryId = generatedKeys.getLong(1);
				}
			}else{
				int curCol = 0;
				updateLogEntry.setLong(++curCol, new Date().getTime() / 1000);
				if (endTime == null){
					updateLogEntry.setNull(++curCol, Types.INTEGER);
				}else{
					updateLogEntry.setLong(++curCol, endTime.getTime() / 1000);
				}
				updateLogEntry.setString(++curCol, getNotesHtml());
				updateLogEntry.setInt(++curCol, numPages);
				updateLogEntry.setInt(++curCol, numAdded);
				updateLogEntry.setInt(++curCol, numUpdated);
				updateLogEntry.setInt(++curCol, numDeleted);
				updateLogEntry.setInt(++curCol, numErrors);
				updateLogEntry.setLong(++curCol, logEntryId);
				updateLogEntry.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			logger.error("Error creating updating log", e);
			return false;
		}
	}
	public void setFinished() {
		this.endTime = new Date();
		this.addNote("Finished Website extraction for " + websiteName);
		this.saveResults();
	}
	void incAdded(){
		numAdded++;
		if ((numAdded + numUpdated) % 100 == 0){
			this.saveResults();
		}
	}
	void incDeleted(){
		numDeleted++;
		if ((numDeleted) % 50 == 0){
			this.saveResults();
		}
	}
	void incUpdated(){
		numUpdated++;
		if ((numAdded + numUpdated) % 100 == 0){
			this.saveResults();
		}
	}
	void incNumPages() {
		numPages++;
	}

	boolean hasErrors() {
		return numErrors > 0;
	}

	public void incErrors(String note) {
		this.addNote("ERROR: " + note);
		numErrors++;
		this.saveResults();
		logger.error(note);
	}

	public void incErrors(String note, Exception e){
		this.addNote("ERROR: " + note + " " + e.toString());
		numErrors++;
		this.saveResults();
		logger.error(note, e);
	}
}
