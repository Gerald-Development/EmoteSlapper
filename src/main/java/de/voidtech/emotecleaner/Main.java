package main.java.de.voidtech.emotecleaner;

public class Main{
	
    public static void main(String[] args) {
        Configuration config = new Configuration();
        new MessageSender(
        		new DatabaseConnection().connect(config.getConnectionURL(), config.getDBUser(), config.getDBPassword()),
        		new DiscordBot().buildDiscordClient(config.getToken()),
        		config).begin();
    }
}
