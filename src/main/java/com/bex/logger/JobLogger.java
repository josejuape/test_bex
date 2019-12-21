package com.bex.logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	
	private static final String NAME_FILE_LOG = "/log_bex.log";
    private static boolean logToFile;
    private static boolean logToConsole;
    private static boolean logMessage;
    private static boolean logWarning;
    private static boolean logError;
    private static boolean logToDatabase;
    private static Map<String,String> dbParams; 
    private static Logger logger;

    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
                     boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map<String,String> dbParamsMap) {
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;
    }

    public void logMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception { 

        if(validate(messageText,message,warning,error)){
        	String messageLog = createMessageLog(messageText.trim(),message,warning,error);
        	
        	if(logToFile) {
        		printMessageToFile(messageLog);
        	}
        	
        	if(logToConsole) {
        		printMessageToConsole(messageLog);
            }
        	
        	if(logToDatabase) {
            	int typePrint = 0; 
                if (message && logMessage) 
                	typePrint = 1;
                
                if (error && logError) 
                	typePrint = 2;
                
                if (warning && logWarning) 
                	typePrint = 3;
                
                printMessageToDataBase(messageLog, typePrint);
            }
        }
    }
    
    private Boolean validate(String messageText,boolean message, boolean warning,boolean error) throws Exception {
    	if (messageText == null || messageText.trim().length() == 0) {
            return Boolean.FALSE;
        }
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration");
        }
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or Message must be specified");
        }
        
        return Boolean.TRUE;
    }
    
    
    private String createMessageLog(String messageText, boolean message, boolean warning,boolean error){
    	String date = DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
    	if (error && logError) {
            return "error " +  date +" " +messageText;
        }

        if (warning && logWarning) {
            return "warning " +date +" " + messageText;
        }

        if (message && logMessage) {
            return "message " +date +" " + messageText;
        }
        return "";
    }
    
    private void printMessageToFile(String messageText) throws IOException{
    	File logFile = new File(dbParams.get("logFileFolder") + NAME_FILE_LOG);
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + NAME_FILE_LOG);
    	
        logger.addHandler(fh);
        logger.log(Level.INFO, messageText);
    }
    
    private void printMessageToConsole(String messageText) {
    	ConsoleHandler ch = new ConsoleHandler();
        logger.addHandler(ch);
        logger.log(Level.INFO, messageText);
    }
    
    private void printMessageToDataBase(String messageText, int typePrint) throws SQLException, ClassNotFoundException {
    	Connection connection = null;
        Class.forName( "com.mongodb.jdbc.MongoDriver" );
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbParams.get("userName"));
        connectionProps.put("password", dbParams.get("password"));
        connection = DriverManager.getConnection("jdbc:mongo://" + dbParams.get("serverName") + ":" + dbParams.get("portNumber")
              + "/" + dbParams.get("dbms"), connectionProps);
        Statement stmt = connection.createStatement();
        
        stmt.executeUpdate("insert into Log_Values('" + messageText + "', " + String.valueOf(typePrint) + ")");
    }
    
}
