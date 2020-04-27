package me.Cooltimmetje.Skuddbot;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Commands.*;
import me.Cooltimmetje.Skuddbot.Commands.Donator.GameCommand;
import me.Cooltimmetje.Skuddbot.Commands.Donator.ManageMessageCommand;
import me.Cooltimmetje.Skuddbot.Commands.Donator.SetPingCommand;
import me.Cooltimmetje.Skuddbot.Commands.HelpCommand.HelpCommand;
import me.Cooltimmetje.Skuddbot.Commands.HelpCommand.HelpGenerator;
import me.Cooltimmetje.Skuddbot.Commands.Managers.CommandManager;
import me.Cooltimmetje.Skuddbot.Commands.SuperAdmin.*;
import me.Cooltimmetje.Skuddbot.Commands.Useless.ActionCommands.HugCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.ActionCommands.PunchCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.FlipCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands.BaconCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands.CakeCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands.KittyCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands.PuppyCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.PanicCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.RiotCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.SaluteCommand;
import me.Cooltimmetje.Skuddbot.Listeners.JoinQuitServerListener;
import me.Cooltimmetje.Skuddbot.Listeners.MessageListener;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackCommand;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeCommand;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings.GlobalSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

//In loving memory of Ray's Nan
//RIP 23-12-2019

//In loving memory of Ray's Grandad
//RIP 23-01-2020

/**
 * This class represents the bot, and is used to register commands and listeners.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.1.1
 * @version ALPHA-2.0
 */
public class Skuddbot {

    private static final Logger logger = LoggerFactory.getLogger(Skuddbot.class);
    private static final ServerManager sm = new ServerManager();

    @Getter private DiscordApi api;
    private String token;
    private CommandManager commandManager;
    @Getter private GlobalSettingsContainer globalSettings;

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
                new BaconCommand(), new ManageAdminsCommand(), new GameCommand(), new ManageMessageCommand(), new ManageDonatorsCommand(), new HelpCommand(), new LogoutCommand(),
                new ExperienceCommand(), new StatsLeaderboardCommand(), new RiotCommand(), new FlipCommand(), new SetPingCommand(), new HugCommand(), new PunchCommand(), new GlobalSettingsCommand(),
                new ClearCooldownCommand(), new SaluteCommand(), new PanicCommand(), new UserInfoCommand(), new AboutCommand(), new ServerInfoCommand(), new BlackjackCommand(), new ChallengeCommand(),
                new InviteCommand(), new CurrenciesCommand(), new CurrenciesLeaderboardCommand(), new DailyBonusCommand(), new TestCommand());
    }

    void registerListeners() {
        logger.info("Registering MessageCreateListener...");
        api.addMessageCreateListener(event -> this.commandManager.process(event.getMessage()));
        api.addMessageCreateListener(event -> MessageListener.run(event.getMessage()));
        logger.info("Registering ReactionAddListener...");
        api.addReactionAddListener(ReactionUtils::run);
        api.addReactionAddListener(ReactionUtils::runButtons);
        api.addReactionAddListener(BlackjackCommand::onReaction);
        api.addReactionAddListener(ChallengeCommand::onReaction);
        logger.info("Registering ServerMemberJoinListener...");
        api.addServerMemberJoinListener(JoinQuitServerListener::join);
        logger.info("Registering ServerMemberLeaveListener...");
        api.addServerMemberLeaveListener(JoinQuitServerListener::leave);
    }

    public HelpGenerator getHelpGenerator(){
        return commandManager;
    }

    public void loadGlobalSettings(){
        globalSettings = new GlobalSettingsSapling().grow();
    }

    public void logout(){
        MessagesUtils.log("Logging out...");

        Iterator<SkuddServer> serverIt = sm.getServers();
        while (serverIt.hasNext()){
            serverIt.next().save();
        }

        globalSettings.save();
        getApi().disconnect();
    }

}
