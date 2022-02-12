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
import me.VanadeysHaven.Skuddbot.Utilities.EnvironmentVariables.EnvVariable;
import me.VanadeysHaven.Skuddbot.Utilities.EnvironmentVariables.EnvVarsManager;
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

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Timer timer = new Timer();

    @Getter private static Skuddbot skuddbot;

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        EnvVarsManager envVars = EnvVarsManager.getInstance();
        envVars.load(args);
        if(envVars.getEnvVariable(EnvVariable.DISCORD_TOKEN).equalsIgnoreCase(EnvVariable.DISCORD_TOKEN.getDefaultValue())) {
            logger.error("No discord token found!");
            System.exit(-1);
        }

        logger.info("Waiting 10 seconds to allow database to finish starting up...");
        Thread.sleep(10000);

        logger.info("Starting Skuddbot v2...");
        logger.info("Starting database connection for user " + envVars.getEnvVariable(EnvVariable.MYSQL_USER));

        HikariManager.setup(envVars.getEnvVariable(EnvVariable.MYSQL_USER), envVars.getEnvVariable(EnvVariable.MYSQL_PASSWORD),
                envVars.getEnvVariable(EnvVariable.MYSQL_DATABASE), envVars.getEnvVariable(EnvVariable.MYSQL_HOST), envVars.getEnvVariable(EnvVariable.MYSQL_PORT));
        ServerSetting.saveToDatabase();
        UserSetting.saveToDatabase();
        Stat.setup();
        Currency.saveToDatabase();

        String token = envVars.getEnvVariable(EnvVariable.DISCORD_TOKEN);
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
