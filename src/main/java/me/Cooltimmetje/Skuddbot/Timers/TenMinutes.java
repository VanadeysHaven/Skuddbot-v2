package me.Cooltimmetje.Skuddbot.Timers;

import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.AppearanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.TimerTask;

/**
 * Timer that runs every 10 minutes.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.0
 */
public class TenMinutes extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TenMinutes.class);
    private static final ServerManager sm = ServerManager.getInstance();
    private static final AppearanceManager am = new AppearanceManager();

    @Override
    public void run() {
        logger.info("Ten minute timer running...");
        Iterator<SkuddServer> iterator = sm.getServers();
        while(iterator.hasNext()) iterator.next().runActivity();

        am.tickAppearance();
    }

}
