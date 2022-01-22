package me.VanadeysHaven.Skuddbot;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Database.HikariManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.Stat;
import me.VanadeysHaven.Skuddbot.Timers.TenMinutes;
import me.VanadeysHaven.Skuddbot.Utilities.AppearanceManager;
import me.VanadeysHaven.Skuddbot.Utilities.Constants;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

/**
 * Main class, this is where the bot starts up from.
 *
 * @author Tim (Vanadey's Haven)
 * @since 2.3.2
 * @version 2.1
 */
public final class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Timer timer = new Timer();

    @Getter private static Skuddbot skuddbot;

    public static void main(String[] args){
        long startTime = System.currentTimeMillis();
        if(args.length < 3) throw new IllegalArgumentException("Not enough arguments. - Required: 3 - Discord Token, MySql username, Mysql Password");
        logger.info("Starting Skuddbot v2...");
        logger.info("Starting database connection for user " + args[1]);

        String mysqlPass = args[2];
        if(mysqlPass.equals("-nopass")) mysqlPass = ""; //For my testing environment
        //Yes I am too lazy to set a password, leave me alone.

        HikariManager.setup(args[1], mysqlPass);
        ServerSetting.saveToDatabase();
        UserSetting.saveToDatabase();
        Stat.setup();
        Currency.saveToDatabase();

        String token = args[0];
        skuddbot = new Skuddbot(token);
        skuddbot.registerCommands();
        skuddbot.buildAndLogin();
        skuddbot.registerListeners();
        skuddbot.loadGlobalSettings();
        timer.schedule(new TenMinutes(), Constants.TEN_MINUTE_TIMER_DELAY, Constants.TEN_MINUTE_TIMER_DELAY);
        new AppearanceManager().appearanceStartup();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> getSkuddbot().logout()));

        MessagesUtils.log(":robot: Bot started up! - Startup took " + (System.currentTimeMillis() - startTime) + "ms"); //TODO: make this better
        logger.info("Ready! - Startup took " + (System.currentTimeMillis() - startTime) + "ms");
    }

}
