package me.Cooltimmetje.Skuddbot;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Commands.*;
import me.Cooltimmetje.Skuddbot.Commands.Donator.GameCommand;
import me.Cooltimmetje.Skuddbot.Commands.Donator.ManageMessageCommand;
import me.Cooltimmetje.Skuddbot.Commands.ImageCommands.BaconCommand;
import me.Cooltimmetje.Skuddbot.Commands.ImageCommands.CakeCommand;
import me.Cooltimmetje.Skuddbot.Commands.ImageCommands.KittyCommand;
import me.Cooltimmetje.Skuddbot.Commands.ImageCommands.PuppyCommand;
import me.Cooltimmetje.Skuddbot.Commands.SuperAdmin.ManageAdminsCommand;
import me.Cooltimmetje.Skuddbot.Commands.SuperAdmin.ManageDonatorsCommand;
import me.Cooltimmetje.Skuddbot.Listeners.MessageListener;
import me.Cooltimmetje.Skuddbot.Listeners.ReactionAddListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//In memory of Ray's Nan
//RIP 22-12-2019

/**
 * This class represents the bot, and is used to register commands and listeners.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class Skuddbot {

    private static Logger logger = LoggerFactory.getLogger(Skuddbot.class);

    @Getter private DiscordApi api;
    private String token;
    @Getter private CommandManager commandManager; //todo this getter is not desirable

    public Skuddbot(String token){
        logger.info("Received token.");
        this.token = token;
        logger.info("Creating command manager...");
        commandManager = new CommandManager();
        logger.info("Creating donator manager and loading data...");
    }

    void buildAndLogin(){
        logger.info("Building client and logging in...");
        this.api = new DiscordApiBuilder().setToken(token).login().join();
    }

    void registerCommands() {
        logger.info("Registering global commands...");
        commandManager.registerCommand(new PingCommand(), new ServerSettingsCommand(), new UserSettingsCommand(), new StatsCommand(), new PuppyCommand(), new KittyCommand(), new CakeCommand(),
                new BaconCommand(), new PermissionCheckCommand(), new ManageAdminsCommand(), new GameCommand(), new ManageMessageCommand(), new ManageDonatorsCommand(), new HelpCommand(),
                new ExperienceCommand());
    }

    void registerListeners() {
        logger.info("Registering MessageCreateListener...");
        api.addMessageCreateListener(event -> this.commandManager.process(event.getMessage()));
        api.addMessageCreateListener(event -> MessageListener.run(event.getMessage()));
        logger.info("Registering ReactionAddListener...");
        api.addReactionAddListener(ReactionAddListener::run);

    }

}
