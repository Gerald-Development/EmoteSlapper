package main.java.de.voidtech.emotecleaner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInterface {
	
	private static final Logger LOGGER = Logger.getLogger(DatabaseInterface.class.getName());

	public static ResultSet queryDatabase(Connection databaseConnection, String query) {
		Statement statement;
		try {
			statement = databaseConnection.createStatement();
			return statement.executeQuery(query);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "A SQL Exception has occurred: " + e.getMessage());
		}
		return null;
	}
	
	public static void executeUpdate(Connection databaseConnection, String query) {
		Statement statement;
		try {
			statement = databaseConnection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "A SQL Exception has occurred: " + e.getMessage());
		}
	}
	
	public static void shutdownConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "An error occurred during connection closing: " + e.getMessage());
		}
	}
	
}
