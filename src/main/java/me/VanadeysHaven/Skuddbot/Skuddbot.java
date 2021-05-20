package me.VanadeysHaven.Skuddbot;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Commands.*;
import me.VanadeysHaven.Skuddbot.Commands.Donator.GameCommand;
import me.VanadeysHaven.Skuddbot.Commands.Donator.ManageMessageCommand;
import me.VanadeysHaven.Skuddbot.Commands.Donator.SetPingCommand;
import me.VanadeysHaven.Skuddbot.Commands.HelpCommand.HelpCommand;
import me.VanadeysHaven.Skuddbot.Commands.HelpCommand.HelpGenerator;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandManager;
import me.Cooltimmetje.Skuddbot.Commands.SuperAdmin.*;
import me.VanadeysHaven.Skuddbot.Commands.SuperAdmin.*;
import me.VanadeysHaven.Skuddbot.Commands.Useless.*;
import me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands.HugCommand;
import me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands.PunchCommand;
import me.Cooltimmetje.Skuddbot.Commands.Useless.*;
import me.VanadeysHaven.Skuddbot.Donator.DonatorMessage;
import me.VanadeysHaven.Skuddbot.Listeners.JoinQuitServerListener;
import me.VanadeysHaven.Skuddbot.Listeners.MessageListener;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Minigames.Blackjack.BlackjackCommand;
import me.VanadeysHaven.Skuddbot.Minigames.Challenge.ChallengeCommand;
import me.VanadeysHaven.Skuddbot.Minigames.DoubleOrNothing.DonCommand;
import me.VanadeysHaven.Skuddbot.Minigames.FreeForAll.FfaCommand;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsSapling;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Commands.*;
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
 * @author Tim (Vanadey's Haven)
 * @since 2.3.03
 * @version 2.0
 */
public final class Skuddbot {

    private static final Logger logger = LoggerFactory.getLogger(Skuddbot.class);
    private static final ServerManager sm = ServerManager.getInstance();

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
        this.api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
    }

    void registerCommands() {
        logger.info("Registering global commands...");
        commandManager.registerCommand(new PingCommand(), new ServerSettingsCommand(), new UserSettingsCommand(), new StatsCommand(),
                new ManageAdminsCommand(), new GameCommand(), new ManageMessageCommand(), new ManageDonatorsCommand(), new HelpCommand(), new LogoutCommand(),
                new ExperienceCommand(), new StatsLeaderboardCommand(), new RiotCommand(), new FlipCommand(), new SetPingCommand(), new HugCommand(), new PunchCommand(),
                new GlobalSettingsCommand(), new ClearCooldownCommand(), new SaluteCommand(), new PanicCommand(), new UserInfoCommand(), new AboutCommand(),
                new ServerInfoCommand(), new BlackjackCommand(), new ChallengeCommand(), new InviteCommand(), new CurrenciesCommand(), new CurrenciesLeaderboardCommand(),
                new DailyBonusCommand(), new DonCommand(), new FfaCommand(), new PurgeCommand(), new JackpotCommand());

        for(DonatorMessage.Type type : DonatorMessage.Type.values())
            if(type.isAcceptsImages())
                commandManager.registerCommand(new ImageCommand(type));
    }

    void registerListeners() {
        logger.info("Registering MessageCreateListener...");
        api.addMessageCreateListener(event -> this.commandManager.process(event.getMessage()));
        api.addMessageCreateListener(event -> MessageListener.run(event.getMessage()));
        logger.info("Registering ReactionAddListener...");
        api.addReactionAddListener(ReactionUtils::run);
        api.addReactionAddListener(ReactionUtils::runClicked);
        logger.info("Registering ReactionRemoveListener...");
        api.addReactionRemoveListener(ReactionUtils::runRemoved);
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
