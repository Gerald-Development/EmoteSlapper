package main.java.de.voidtech.emotecleaner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;

public class EmoteValidator {
	
	private static final Logger LOGGER = Logger.getLogger(EmoteValidator.class.getName());
	
	private static final int INCREMENT_AMOUNT = 30;
	private static final int MAXIMUM_DELAY = 2200;
	private static final int MINUMUM_DELAY = 1700;
	
	private Connection databaseConnection;
	private JDA jda;
	private Configuration config;
	private Webhook webhook;
	private Random random = new Random();
	
	private int offset = 0;
	
	public EmoteValidator(Connection connection, JDA jda, Configuration config) {
		this.databaseConnection = connection;
		this.jda = jda;
		this.config = config;
	}
	
	public void beginValidation() {
		try {
			jda.awaitReady();
			webhook = WebhookManager.getOrCreateWebhook(jda.getTextChannelById(config.getChannel()), "Emote Smacker", jda.getSelfUser().getId());
			slapEmotes();
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Error occurred during AwaitReady: " + e.getMessage());
		}
	}
	
	private String formatEmoteString(boolean isAnimated, String emoteName, String emoteID) {
		return String.format("<%s%s:%s>", isAnimated ? "a:" : ":", emoteName, emoteID);
	}
	
	private void slapEmotes() {
		List<String> emotesList = new ArrayList<String>();
		int resultCount = 0;
		try {
			ResultSet emotesResultSet = DatabaseInterface.queryDatabase(databaseConnection,
					String.format("SELECT * FROM NitroliteEmote LIMIT %s OFFSET %s", INCREMENT_AMOUNT, offset));
			while (emotesResultSet.next()) {
				resultCount++;
				boolean emoteIsAnimated = emotesResultSet.getBoolean("isanimated");
				String emoteName = emotesResultSet.getString("name");
				String emoteID = emotesResultSet.getString("emoteid");
				String emoteString = formatEmoteString(emoteIsAnimated, emoteName, emoteID);
				emotesList.add(emoteString + "," + emoteID);
			}
			WebhookManager.sendWebhookMessage(webhook, String.join(" ", emotesList));
			LOGGER.log(Level.INFO, String.format("Result Count: %s Offset: %s", resultCount, offset));
			Thread.sleep(random.nextInt(MAXIMUM_DELAY - MINUMUM_DELAY) + MINUMUM_DELAY);
			
			if (resultCount < INCREMENT_AMOUNT) selfDestruct();
			else {
				offset = offset + INCREMENT_AMOUNT;
				slapEmotes();
			}
			
		} catch (SQLException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, "An exception has occurred: " + e.getMessage());
		}
	}

	private void selfDestruct() {
		jda.shutdown();
		DatabaseInterface.shutdownConnection(databaseConnection);
	}
}