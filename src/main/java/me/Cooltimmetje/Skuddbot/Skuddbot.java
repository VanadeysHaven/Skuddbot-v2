package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Commands.CommandManager;
import me.Cooltimmetje.Skuddbot.Commands.PingCommand;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the bot, and is used to register commands and listeners.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class Skuddbot {

    private static Logger logger = LoggerFactory.getLogger(Skuddbot.class);

    private DiscordApi api;
    private String token;
    private CommandManager commandManager;

    public Skuddbot(String token){
        logger.info("Received token.");
        this.token = token;
        logger.info("Creating command manager...");
        this.commandManager = new CommandManager();
    }

    public void buildAndLogin(){
        logger.info("Building client and logging in...");
        this.api = new DiscordApiBuilder().setToken(token).login().join();
    }

    public void registerCommands() {
        logger.info("Registering global commands...");
        this.commandManager.registerCommand(new PingCommand());
    }

    public void registerListeners() {
        logger.info("Registering MessageCreateListener...");
        api.addMessageCreateListener(event -> this.commandManager.process(event.getMessage()));
    }
}
