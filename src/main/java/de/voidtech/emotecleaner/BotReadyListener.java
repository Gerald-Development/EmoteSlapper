package main.java.de.voidtech.emotecleaner;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class BotReadyListener implements EventListener{
	
	private static final Logger LOGGER = Logger.getLogger(BotReadyListener.class.getName());
	
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent)
            LOGGER.log(Level.INFO, "Logged into Discord");
    }
}
