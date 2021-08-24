package main.java.de.voidtech.emotecleaner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
	
	private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
	
	public Connection databaseConnection = null;
	
	public Connection connect(String url, String user, String password) {
        try {
        	databaseConnection = DriverManager.getConnection(url, user, password);
            LOGGER.log(Level.INFO, "Connected to the database successfully.");
        } catch (SQLException e) {
        	LOGGER.log(Level.SEVERE, "Failed to connect to database: " + e.getMessage());
        }

        return databaseConnection;
    }
	
	public Connection getDatabaseConnection() {
		return this.databaseConnection;
	}
	
}
