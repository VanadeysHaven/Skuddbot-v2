package me.Cooltimmetje.Skuddbot;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.HikariManager;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettings.ServerSetting;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class, this is where the bot starts up from.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    @Getter private static Skuddbot skuddbot;

    public static void main(String[] args){
        if(args.length < 3) throw new IllegalArgumentException("Not enough arguments. - Required: 3 - Discord Token, MySql username, Mysql Password");
        logger.info("Starting Skuddbot v2...");
        logger.info("Starting database connection for user " + args[1]);
        HikariManager.setup(args[1], args[2]);
        ServerSetting.saveToDatabase();
        UserSetting.saveToDatabase();

        String token = args[0];
        skuddbot = new Skuddbot(token);
        skuddbot.registerCommands();
        skuddbot.buildAndLogin();
        skuddbot.registerListeners();
    }

}
