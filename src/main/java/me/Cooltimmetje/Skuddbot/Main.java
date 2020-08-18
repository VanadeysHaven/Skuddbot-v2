package me.Cooltimmetje.Skuddbot;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.HikariManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Timers.TenMinutes;
import me.Cooltimmetje.Skuddbot.Utilities.AppearanceManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

/**
 * Main class, this is where the bot starts up from.
 *
 * @author Tim (Cooltimmetje)
 * @since 2.0
 * @versio 2.1
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Timer timer = new Timer();

    @Getter private static Skuddbot skuddbot;

    public static void main(String[] args){
        if(args.length < 3) throw new IllegalArgumentException("Not enough arguments. - Required: 3 - Discord Token, MySql username, Mysql Password");
        logger.info("Starting Skuddbot v2...");
        logger.info("Starting database connection for user " + args[1]);

        String mysqlPass = args[2];
        if(mysqlPass.equals("-nopass")) mysqlPass = ""; //For my testing environment
        //Yes I am too lazy to set a password, leave me alone.

        HikariManager.setup(args[1], mysqlPass);
        ServerSetting.saveToDatabase();
        UserSetting.saveToDatabase();
        Stat.saveToDatabase();
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

        MessagesUtils.log(":robot: Bot started up!"); //TODO: make this better
    }

}
