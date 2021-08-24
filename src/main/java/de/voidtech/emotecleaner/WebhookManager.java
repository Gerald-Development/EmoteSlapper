package main.java.de.voidtech.emotecleaner;

import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;

public class WebhookManager {
	
	private static final Logger LOGGER = Logger.getLogger(WebhookManager.class.getName());

	public static Webhook getOrCreateWebhook(TextChannel targetChannel, String webhookName, String selfID) {
		
		List<Webhook> webhooks = targetChannel.retrieveWebhooks().complete();
		for (Webhook webhook : webhooks) {
			if (webhook.getName().equals(webhookName) && webhook.getOwnerAsUser().getId().equals(selfID)) {
				return webhook;
			}
		}
		Webhook newWebhook = targetChannel.createWebhook(webhookName).complete();
		return newWebhook;		
	}
	
	public static void sendWebhookMessage(Webhook webhook, String content) {
		JSONObject webhookPayload = new JSONObject();
        webhookPayload.put("content", content);
        webhookPayload.put("username", "Emote Smacker");
        webhookPayload.put("tts", false);
        
        try {              			
            URL url = new URL(webhook.getUrl());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Emote Slapper");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            
            OutputStream stream = connection.getOutputStream();
            stream.write(webhookPayload.toString().getBytes());
            stream.flush();
            stream.close();

            connection.getInputStream().close();
            connection.disconnect();
        	
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during Webhook Execution: " + e.getMessage());
        }
	}
	
}
