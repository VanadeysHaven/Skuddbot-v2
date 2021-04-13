package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.LevelUpNotification;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;

/**
 * Listens for messages and awards XP.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.1.1
 * @since 2.0
 */
public class MessageListener {

    private static final String MESSAGE_FORMAT = "**{0}** has leveled up to **level {1}**.";
    private static final String DM_MESSAGE_FORMAT = "You have leveled up in **{0}**, you are now **level {1}**.";

    private static final ServerManager sm = ServerManager.getInstance();
    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final RNGManager random = new RNGManager();

    public static void run(Message message){
        String content = message.getContent();
        if(!message.getAuthor().isRegularUser()) return;
        if(message.getChannel().getType() != ChannelType.SERVER_TEXT_CHANNEL) return;
        Server server = message.getServer().orElse(null); assert server != null;
        User user = message.getAuthor().asUser().orElse(null); assert user != null;
        SkuddServer ss = sm.getServer(server.getId());
        String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX);
        if(content.startsWith(commandPrefix)) return;

        SkuddUser su = pm.getUser(server.getId(), message.getAuthor().getId());
        su.getStats().incrementInt(Stat.EXPERIENCE, random.integer(ss.getSettings().getInt(ServerSetting.XP_MIN), ss.getSettings().getInt(ServerSetting.XP_MAX)));

        if(su.getStats().hasLeveledUp()){
            LevelUpNotification notification = su.getSettings().getLevelUpNotify();
            if(!ss.getSettings().getBoolean(ServerSetting.ALLOW_MSG_LVL_UP_NOTIFY) && notification == LevelUpNotification.MESSAGE){
                notification = LevelUpNotification.REACTION;
            }
            switch(notification) {
                case REACTION:
                    MessagesUtils.addReaction(message, Emoji.ARROW_UP, MessageFormat.format(MESSAGE_FORMAT, message.getAuthor().getDisplayName(), su.getStats().getLevelProgress()[0]));
                    break;
                case DM:
                    MessagesUtils.sendEmoji(user.getPrivateChannel().orElse(user.openPrivateChannel().join()), Emoji.ARROW_UP, MessageFormat.format(DM_MESSAGE_FORMAT, server.getName(), su.getStats().getLevelProgress()[0]));
                    break;
                case MESSAGE:
                    MessagesUtils.sendEmoji(message.getChannel(), Emoji.ARROW_UP, MessageFormat.format(MESSAGE_FORMAT, message.getAuthor().getDisplayName(), su.getStats().getLevelProgress()[0]));
                    break;
                default:
                    break;
            }
        }
    }

}
