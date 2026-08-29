package me.VanadeysHaven.Skuddbot;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Commands.*;
import me.VanadeysHaven.Skuddbot.Commands.Donator.GameCommand;
import me.VanadeysHaven.Skuddbot.Commands.Donator.ManageMessageCommand;
import me.VanadeysHaven.Skuddbot.Commands.Donator.SetPingCommand;
import me.VanadeysHaven.Skuddbot.Commands.HelpCommand.HelpCommand;
import me.VanadeysHaven.Skuddbot.Commands.HelpCommand.HelpGenerator;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandManager;
import me.VanadeysHaven.Skuddbot.Commands.SuperAdmin.*;
import me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands.HugCommand;
import me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands.PunchCommand;
import me.VanadeysHaven.Skuddbot.Commands.Useless.*;
import me.VanadeysHaven.Skuddbot.Donator.DonatorMessage;
import me.VanadeysHaven.Skuddbot.Listeners.SkuddEventListener;
import me.VanadeysHaven.Skuddbot.Minigames.Blackjack.BlackjackCommand;
import me.VanadeysHaven.Skuddbot.Minigames.Challenge.ChallengeCommand;
import me.VanadeysHaven.Skuddbot.Minigames.DoubleOrNothing.DonCommand;
import me.VanadeysHaven.Skuddbot.Minigames.FreeForAll.FfaCommand;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsSapling;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
 * @since 2.3.22
 * @version 2.4
 */
public final class Skuddbot {

    private static final Logger logger = LoggerFactory.getLogger(Skuddbot.class);
    private static final ServerManager sm = ServerManager.getInstance();

    @Getter private JDA api;
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

    void buildAndLogin() throws InterruptedException {
        logger.info("Building client and logging in...");
        this.api = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .build()
                .awaitReady();
    }

    void registerCommands() {
        logger.info("Registering global commands...");
        commandManager.registerCommand(new PingCommand(), new ServerSettingsCommand(), new UserSettingsCommand(), new StatsCommand(),
                new ManageAdminsCommand(), new GameCommand(), new ManageMessageCommand(), new ManageDonatorsCommand(), new HelpCommand(), new LogoutCommand(),
                new ExperienceCommand(), new StatsLeaderboardCommand(), new RiotCommand(), new FlipCommand(), new SetPingCommand(), new HugCommand(), new PunchCommand(),
                new GlobalSettingsCommand(), new ClearCooldownCommand(), new SaluteCommand(), new PanicCommand(), new UserInfoCommand(), new AboutCommand(),
                new ServerInfoCommand(), new BlackjackCommand(), new ChallengeCommand(), new InviteCommand(), new CurrenciesCommand(), new CurrenciesLeaderboardCommand(),
                new DailyBonusCommand(), new DonCommand(), new FfaCommand(), new PurgeCommand(), new JackpotCommand());

//        commandManager.registerCommand(new TestCommand());

        for(DonatorMessage.Type type : DonatorMessage.Type.values())
            if(type.isAcceptsImages())
                commandManager.registerCommand(new ImageCommand(type));
    }

    void registerListeners() {
        logger.info("Registering event listener...");
        api.addEventListener(new SkuddEventListener(commandManager));
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
        getApi().shutdown();
    }

}
