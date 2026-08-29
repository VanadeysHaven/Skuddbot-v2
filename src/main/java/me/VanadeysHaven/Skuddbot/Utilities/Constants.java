package me.VanadeysHaven.Skuddbot.Utilities;

import me.VanadeysHaven.Skuddbot.Main;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;

/**
 * Class with constants.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class Constants {

    public static final int TEN_MINUTE_TIMER_DELAY = 600000;

    public static final long TIMMY_ID = 76593288865394688L;
    public static final long BOT_LOG = 274542577880006656L;

    public static TextChannel getLogChannel(){
        TextChannel channel = Main.getSkuddbot().getApi().getTextChannelById(BOT_LOG);
        assert channel != null;
        return channel;
    }

    public static ArrayList<Long> adminUsers = new ArrayList<>();

}
