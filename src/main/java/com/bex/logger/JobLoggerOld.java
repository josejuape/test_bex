package com.bex.logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLoggerOld {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	private boolean initialized; // Propiedad no utilizada, deberia eliminarse
	private static Map dbParams; // Se debe esecificar los tipos de dato de los Clave - Valor
	private static Logger logger;

	public JobLoggerOld(boolean logToFileParam, boolean logToConsoleParam,
			boolean logToDatabaseParam, boolean logMessageParam,
			boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
		logger = Logger.getLogger("MyLog"); // Se deberia manejar una constante para el nombre del log
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
	}

	// El metodo no deberia ser static ya que manejamos un constructor para inicializar las propiedades de la clase.
	// Considerar como regla para el nombrado de variables y metodos el formato CamelCase
	public static void LogMessage(String messageText, boolean message,
			boolean warning, boolean error) throws Exception {
		messageText.trim(); // Considerar que el string es inmutable, esta linea no afecta e la variable original
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning)
				|| (!message && !warning && !error)) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		Connection connection = null;
		Class.forName( "com.mongodb.jdbc.MongoDriver" ); // Es necesario referirse al driver para la conexión a la base de datos
		Properties connectionProps = new Properties();
		connectionProps.put("user", dbParams.get("userName"));
		connectionProps.put("password", dbParams.get("password"));

		// Existe un error en el orden del hostname y la base de datos
		connection = DriverManager.getConnection(
				"jdbc:" + dbParams.get("dbms") + "://"
						+ dbParams.get("serverName") + ":"
						+ dbParams.get("portNumber") + "/", connectionProps);
        
		
		
		int t = 0;// Siempre el nombre de variable debe ser descripctivo
		if (message && logMessage) {
			t = 1;
		}

		if (error && logError) {
			t = 2;
		}

		if (warning && logWarning) {
			t = 3;
		}

		Statement stmt = connection.createStatement();

		String l = null; // Siempre el nombre de variable debe ser descripctivo
		File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt"); // utilizar una constante para guardar el nombre del archivo
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileHandler fh = new FileHandler(dbParams.get("logFileFolder")
				+ "/logFile.txt"); // utilizar una constante para guardar el nombre del archivo
		ConsoleHandler ch = new ConsoleHandler();
		if (error && logError) {
			l = l
					+ "error "
					+ DateFormat.getDateInstance(DateFormat.LONG).format(
							new Date()) + messageText;
		}

		if (warning && logWarning) {
			l = l
					+ "warning "
					+ DateFormat.getDateInstance(DateFormat.LONG).format(
							new Date()) + messageText;
		}

		if (message && logMessage) {
			l = l
					+ "message "
					+ DateFormat.getDateInstance(DateFormat.LONG).format(
							new Date()) + messageText;
		}
		if (logToFile) {
			logger.addHandler(fh);
			logger.log(Level.INFO, messageText); // Se deberia enviar al log el mensaje completo: variable l
		}
		if (logToConsole) {
			logger.addHandler(ch);
			logger.log(Level.INFO, messageText); // Se deberia enviar al log el mensaje completo: variable l
		}
		if (logToDatabase) {
			stmt.executeUpdate("insert into Log_Values('" + message + "', "
					+ String.valueOf(t) + ")"); // Se deberia enviar al log el mensaje completo: variable l
		}
	}
}

/**
 Recomendaciones:
 - Aplicar programación procedural para organizar la logica de toda la funcionalidad de la clase.
 - Aplicar CheckStyle y Spotbugs para mejorar y garantizar la calidad de codigo estatico.
 - Utilizar patron builder para creacion del objeto, se recomienda utilizar la libreria lombok.
*/