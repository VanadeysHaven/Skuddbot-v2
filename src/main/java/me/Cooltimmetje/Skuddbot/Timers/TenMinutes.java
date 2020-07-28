package me.Cooltimmetje.Skuddbot.Timers;

import me.Cooltimmetje.Skuddbot.Commands.ServerSettingsCommand;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FfaCommand;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.AppearanceManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * Timer that runs every 10 minutes.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.0
 */
public class TenMinutes extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TenMinutes.class);
    private static final ServerManager sm = ServerManager.getInstance();
    private static final AppearanceManager am = new AppearanceManager();

    @Override
    public void run() {
        try {
            logger.info("Ten minute timer running...");
            Iterator<SkuddServer> iterator = sm.getServers();
            while (iterator.hasNext()) iterator.next().runActivity();

            am.tickAppearance();
            FfaCommand.runReminders();
            ServerSettingsCommand.clearOverviews();
        } catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            MessagesUtils.log("```\n" + sw.toString() + "\n```");
        }
    }

}
