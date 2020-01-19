package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Enums.Stat;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

/**
 * Listens for messages and awards XP.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class MessageListener {

    private static final ServerManager sm = new ServerManager();
    private static final ProfileManager pm = new ProfileManager();

    public static void run(Message message){
        String content = message.getContent();
        if(!message.getAuthor().isRegularUser()) return;
        if(message.getChannel().getType() != ChannelType.SERVER_TEXT_CHANNEL) return;
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddServer ss = sm.getServer(server.getId());
        String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX);
        if(content.startsWith(commandPrefix)) return;

        SkuddUser su = pm.getUser(server.getId(), message.getAuthor().getId());
        su.getStats().incrementInt(Stat.EXPERIENCE, MiscUtils.randomInt(ss.getSettings().getInt(ServerSetting.XP_MIN), ss.getSettings().getInt(ServerSetting.XP_MAX)));
        if(su.getStats().hasLeveledUp()){
            MessagesUtils.addReaction(message, Emoji.ARROW_UP, "**" + message.getAuthor().getDisplayName() + "** has leveled up to **level " + su.getStats().getLevelProgress()[0] + "**."); //todo implement setting
        }
    }

}
