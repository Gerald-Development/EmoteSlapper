package main.java.de.voidtech.emotecleaner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
	private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

	private final Properties config = new Properties();

	public Configuration() {
		File configFile = new File("config.properties");
		try (FileInputStream fis = new FileInputStream(configFile)){
			config.load(fis);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "an error has occurred while reading the config: " + e.getMessage());
		}	
	}

	public String getToken() {
		return config.getProperty("Discord.token");
	}
	
	public String getChannel() {
		return config.getProperty("Discord.channel");
	}
	
	public String getDBUser()
	{
		String user = config.getProperty("Database.User");
		return user != null ? user : "postgres";
	}
	
	public String getDBPassword()
	{
		String pass = config.getProperty("Database.Password");
		return pass != null ? pass : "root";
	}
	
	public String getConnectionURL()
	{
		String dbURL = config.getProperty("Database.ConnectionURL");
		return dbURL != null ? dbURL : "jdbc:postgresql://localhost/BaristaDB";
	}
}