package main.java.de.voidtech.emotecleaner;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class BotEventListener implements EventListener {

	private Connection dbConnection = null;
	private Configuration config = new Configuration();
	private static final Logger LOGGER = Logger.getLogger(BotEventListener.class.getName());
	
	public BotEventListener() {
		dbConnection = new DatabaseConnection().connect(config.getConnectionURL(), config.getDBUser(), config.getDBPassword());
	}
	
	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent) {
			Message message = ((MessageReceivedEvent) event).getMessage();
			List<String> messageTokens = Arrays.asList(message.getContentRaw().split(" "));
			checkMessageTokens(messageTokens);
			message.delete().complete();
		}
		else if (event instanceof ShutdownEvent) DatabaseInterface.shutdownConnection(dbConnection);
		else if (event instanceof ReadyEvent) LOGGER.log(Level.INFO, "Logged into Discord");
	}

	private void checkMessageTokens(List<String> messageTokens) {
		List<String> invalidEmotes = new ArrayList<String>();
		for (String token : messageTokens) {
			if (!token.contains("<")) invalidEmotes.add(token);
		}
		
		for (String emote : invalidEmotes) {
			String ID = Arrays.asList(emote.split(",")).get(1);
			DatabaseInterface.executeUpdate(dbConnection, "DELETE FROM NitroliteEmote WHERE emoteid = '" + ID + "'");
		}
		
		if (!invalidEmotes.isEmpty()) LOGGER.log(Level.INFO, String.format("Deleted %s emotes", invalidEmotes.size()));
	}
}