package main.java.de.voidtech.emotecleaner;

import java.sql.Connection;

import net.dv8tion.jda.api.JDA;

public class Main{
	
    public static void main(String[] args) {
        Configuration config = new Configuration();
        Connection dbConnection = new DatabaseConnection().connect(config.getConnectionURL(), config.getDBUser(), config.getDBPassword());
        JDA jda = new DiscordBot().buildDiscordClient(config.getToken());
        new EmoteValidator(dbConnection, jda, config).beginValidation();
    }
}
