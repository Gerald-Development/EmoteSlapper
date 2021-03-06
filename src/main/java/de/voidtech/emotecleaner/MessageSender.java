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

public class MessageSender {
	
	private static final Logger LOGGER = Logger.getLogger(MessageSender.class.getName());
	
	private static final int INCREMENT_AMOUNT = 30;
	
	private Connection databaseConnection;
	private JDA jda;
	private Configuration config;
	private Webhook webhook;
	private Random random = new Random();
	
	private int offset = 0;
	private boolean keepSlapping = true;
	
	public MessageSender(Connection connection, JDA jda, Configuration config) {
		this.databaseConnection = connection;
		this.jda = jda;
		this.config = config;
	}
	
	public void begin() {
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
			while (keepSlapping) {
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
				Thread.sleep(random.nextInt(config.getMaximumDelay() - config.getMinimumDelay()) + config.getMinimumDelay());
				
				if (resultCount < INCREMENT_AMOUNT) keepSlapping = false;
				else offset = offset + INCREMENT_AMOUNT;	
			}
			selfDestruct();
		} catch (SQLException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, "An exception has occurred: " + e.getMessage());
		}
	}

	private void selfDestruct() {
		jda.shutdown();
		DatabaseInterface.shutdownConnection(databaseConnection);
	}
}