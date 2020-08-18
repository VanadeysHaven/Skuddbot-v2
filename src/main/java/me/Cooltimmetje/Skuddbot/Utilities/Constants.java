package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Main;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Class with constants.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class Constants {

    public static final int TEN_MINUTE_TIMER_DELAY = 600000;

    public static final long TIMMY_ID = 76593288865394688L;
    public static final long BOT_LOG = 274542577880006656L;

    public static TextChannel getLogChannel(){
        Optional<Channel> channel = Main.getSkuddbot().getApi().getChannelById(BOT_LOG);
        assert channel.isPresent();
        return (TextChannel) channel.get();
    }

    public static ArrayList<Long> adminUsers = new ArrayList<>();

}
