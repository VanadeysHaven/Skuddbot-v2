package me.VanadeysHaven.Skuddbot.Listeners;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.LevelUpNotification;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.Stat;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.RNGManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.text.MessageFormat;

/**
 * Listens for messages and awards XP.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class MessageListener {

    private static final String MESSAGE_FORMAT = "**{0}** has leveled up to **level {1}**.";
    private static final String DM_MESSAGE_FORMAT = "You have leveled up in **{0}**, you are now **level {1}**.";

    private static final ServerManager sm = ServerManager.getInstance();
    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final RNGManager random = new RNGManager();

    public static void run(Message message){
        String content = message.getContentRaw();
        if(message.getAuthor().isBot()) return;
        if(message.getChannel().getType() != ChannelType.TEXT) return;
        Guild server = message.isFromGuild() ? message.getGuild() : null; assert server != null;
        User user = message.getAuthor();
        SkuddServer ss = sm.getServer(server.getIdLong());
        String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX);
        if(content.startsWith(commandPrefix)) return;

        SkuddUser su = pm.getUser(server.getIdLong(), message.getAuthor().getIdLong());
        su.getStats().incrementInt(Stat.EXPERIENCE, random.integer(ss.getSettings().getInt(ServerSetting.XP_MIN), ss.getSettings().getInt(ServerSetting.XP_MAX)));

        if(su.getStats().hasLeveledUp()){
            LevelUpNotification notification = su.getSettings().getLevelUpNotify();
            if(!ss.getSettings().getBoolean(ServerSetting.ALLOW_MSG_LVL_UP_NOTIFY) && notification == LevelUpNotification.MESSAGE){
                notification = LevelUpNotification.REACTION;
            }
            switch(notification) {
                case REACTION:
                    MessagesUtils.addReaction(message, Emoji.ARROW_UP, MessageFormat.format(MESSAGE_FORMAT, message.getMember().getEffectiveName(), su.getStats().getLevelProgress()[0]));
                    break;
                case DM:
                    MessagesUtils.sendEmoji(user.openPrivateChannel().complete(), Emoji.ARROW_UP, MessageFormat.format(DM_MESSAGE_FORMAT, server.getName(), su.getStats().getLevelProgress()[0]));
                    break;
                case MESSAGE:
                    MessagesUtils.sendEmoji(message.getChannel(), Emoji.ARROW_UP, MessageFormat.format(MESSAGE_FORMAT, message.getMember().getEffectiveName(), su.getStats().getLevelProgress()[0]));
                    break;
                default:
                    break;
            }
        }
    }

}
