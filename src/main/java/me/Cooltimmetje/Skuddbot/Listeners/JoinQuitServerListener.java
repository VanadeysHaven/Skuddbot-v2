package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;

/**
 * Stuff for when users join or leave a server.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.1.1
 * @since 2.0
 */
public class JoinQuitServerListener {

    private static final ServerManager sm = ServerManager.getInstance();

    public static void join(ServerMemberJoinEvent event){
        User user = event.getUser();
        Server server = event.getServer();
        send(server, user, ServerSetting.WELCOME_MESSAGE);

        SkuddServer ss = sm.getServer(server.getId());
        if(ss.getSettings().getString(ServerSetting.ROLE_ON_JOIN) == null) return;
        user.addRole(server.getRolesByName(ss.getSettings().getString(ServerSetting.ROLE_ON_JOIN)).get(0), "User joined the server and was given the ROLE_ON_JOIN role.");
    }

    public static void leave(ServerMemberLeaveEvent event){
        send(event.getServer(), event.getUser(), ServerSetting.GOODBYE_MESSAGE);
    }

    private static void send(Server server, User user, ServerSetting setting){
        SkuddServer ss = sm.getServer(server.getId());
        String text = ss.getSettings().getString(setting);
        long messageChannel =  ss.getSettings().getLong(ServerSetting.WELCOME_GOODBYE_CHANNEL);
        if(text == null || messageChannel == -1) return;

        text = text.replace("$name", user.getDiscriminatedName()).replace("$server", server.getName());
        TextChannel channel = Main.getSkuddbot().getApi().getTextChannelById(messageChannel).orElse(null);
        if(channel == null){
            String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", "=");
            User owner = server.getOwner().orElse(null); assert owner != null;
            MessagesUtils.sendEmoji(owner.getPrivateChannel().orElse(owner.openPrivateChannel().join()), Emoji.WARNING,
                    "I just tried to send a " + setting.toString().toLowerCase().replace("_", " ") + " in your server to a channel `(ID: " + messageChannel + ")` that doesn't exist. You probably might want to update this channel ID using this command in your server: `" + commandPrefix + "serversettings welcome-goodbye-channel <channel id>`\n" +
                            "Note that this error will only appear once and both welcome and goodbye messages have been disabled until you fix this! For more information visit the wiki: " + "https://wiki.skuddbot.xyz/features/server-settings#channel-does-not-exist-error");
            ss.getSettings().setLong(ServerSetting.WELCOME_GOODBYE_CHANNEL, -1);
            return;
        }

        MessagesUtils.sendPlain(channel, text);
    }

}
