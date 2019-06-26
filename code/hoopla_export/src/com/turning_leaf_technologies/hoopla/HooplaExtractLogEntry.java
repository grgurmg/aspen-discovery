package com.turning_leaf_technologies.hoopla;

import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class HooplaExtractLogEntry {
    private Long logEntryId = null;
    private Date startTime;
    private Date endTime;
    private ArrayList<String> notes = new ArrayList<>();
    private int numProducts = 0;
    private int numErrors = 0;
    private int numAdded = 0;
    private int numDeleted = 0;
    private int numUpdated = 0;
    private Logger logger;

    HooplaExtractLogEntry(Connection dbConn, Logger logger){
        this.logger = logger;
        this.startTime = new Date();
        try {
            insertLogEntry = dbConn.prepareStatement("INSERT into hoopla_export_log (startTime) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
            updateLogEntry = dbConn.prepareStatement("UPDATE hoopla_export_log SET lastUpdate = ?, endTime = ?, notes = ?, numProducts = ?, numErrors = ?, numAdded = ?, numUpdated = ?, numDeleted = ? WHERE id = ?", PreparedStatement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            logger.error("Error creating prepared statements to update log", e);
        }
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    void addNote(String note) {
        Date date = new Date();
        this.notes.add(dateFormat.format(date) + " - " + note);
        saveResults();
    }

    private String getNotesHtml() {
        StringBuilder notesText = new StringBuilder("<ol class='cronNotes'>");
        for (String curNote : notes){
            String cleanedNote = curNote;
            cleanedNote = cleanedNote.replaceAll("<pre>", "<code>");
            cleanedNote = cleanedNote.replaceAll("</pre>", "</code>");
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
    void saveResults() {
        try {
            if (logEntryId == null){
                insertLogEntry.setLong(1, startTime.getTime() / 1000);
                insertLogEntry.executeUpdate();
                ResultSet generatedKeys = insertLogEntry.getGeneratedKeys();
                if (generatedKeys.next()){
                    logEntryId = generatedKeys.getLong(1);
                }
            }else{
                int curCol = 0;
                updateLogEntry.setLong(++curCol, new Date().getTime() / 1000);
                if (endTime == null){
                    updateLogEntry.setNull(++curCol, java.sql.Types.INTEGER);
                }else{
                    updateLogEntry.setLong(++curCol, endTime.getTime() / 1000);
                }
                updateLogEntry.setString(++curCol, getNotesHtml());
                updateLogEntry.setInt(++curCol, numProducts);
                updateLogEntry.setInt(++curCol, numErrors);
                updateLogEntry.setInt(++curCol, numAdded);
                updateLogEntry.setInt(++curCol, numUpdated);
                updateLogEntry.setInt(++curCol, numDeleted);
                updateLogEntry.setLong(++curCol, logEntryId);
                updateLogEntry.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error creating updating log", e);
        }
    }
    void setFinished() {
        this.endTime = new Date();
        this.addNote("Finished Hoopla extraction");
        this.saveResults();
    }
    void incErrors(){
        numErrors++;
    }
    void incAdded(){
        numAdded++;
    }
    void incDeleted(){
        numDeleted++;
    }
    void incUpdated(){
        numUpdated++;
    }
    void incNumProducts(int size) {
        numProducts += size;
    }

    boolean hasErrors() {
        return numErrors > 0;
    }
}
