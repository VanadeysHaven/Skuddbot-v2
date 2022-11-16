package me.VanadeysHaven.Skuddbot.Timers;

import me.VanadeysHaven.Skuddbot.Commands.ServerSettingsCommand;
import me.VanadeysHaven.Skuddbot.Minigames.FreeForAll.FfaCommand;
import me.VanadeysHaven.Skuddbot.Minigames.GameLogs.GameLogSender;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PagedEmbed;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Utilities.AppearanceManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.PagedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * Timer that runs every 10 minutes.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.0
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
            PagedMessage.runAutoExpire();
            PagedEmbed.runAutoExpire();
            GameLogSender.runExpire();
        } catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            MessagesUtils.log("```\n" + sw.toString() + "\n```");
        }
    }

}
