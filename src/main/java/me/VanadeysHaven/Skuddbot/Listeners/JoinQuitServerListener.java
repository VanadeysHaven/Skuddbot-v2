package me.VanadeysHaven.Skuddbot.Listeners;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

/**
 * Stuff for when users join or leave a server.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class JoinQuitServerListener {

    private static final ServerManager sm = ServerManager.getInstance();

    public static void join(GuildMemberJoinEvent event){
        Member member = event.getMember();
        Guild server = event.getGuild();
        send(server, member.getUser(), ServerSetting.WELCOME_MESSAGE);

        SkuddServer ss = sm.getServer(server.getIdLong());
        if(ss.getSettings().getString(ServerSetting.ROLE_ON_JOIN) == null) return;
        server.addRoleToMember(member, server.getRolesByName(ss.getSettings().getString(ServerSetting.ROLE_ON_JOIN), false).get(0))
                .reason("User joined the server and was given the ROLE_ON_JOIN role.").queue();
    }

    public static void leave(GuildMemberRemoveEvent event){
        send(event.getGuild(), event.getUser(), ServerSetting.GOODBYE_MESSAGE);
    }

    private static void send(Guild server, User user, ServerSetting setting){
        SkuddServer ss = sm.getServer(server.getIdLong());
        String text = ss.getSettings().getString(setting);
        long messageChannel =  ss.getSettings().getLong(ServerSetting.WELCOME_GOODBYE_CHANNEL);
        if(text == null || messageChannel == -1) return;

        text = text.replace("$name", user.getName()).replace("$server", server.getName());
        TextChannel channel = Main.getSkuddbot().getApi().getTextChannelById(messageChannel);
        if(channel == null){
            String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", "=");
            Member owner = server.getOwner(); assert owner != null;
            MessagesUtils.sendEmoji(owner.getUser().openPrivateChannel().complete(), Emoji.WARNING,
                    "I just tried to send a " + setting.toString().toLowerCase().replace("_", " ") + " in your server to a channel `(ID: " + messageChannel + ")` that doesn't exist. You probably might want to update this channel ID using this command in your server: `" + commandPrefix + "serversettings welcome-goodbye-channel <channel id>`\n" +
                            "Note that this error will only appear once and both welcome and goodbye messages have been disabled until you fix this! For more information visit the wiki: " + "https://wiki.skuddbot.xyz/features/server-settings#channel-does-not-exist-error");
            ss.getSettings().setLong(ServerSetting.WELCOME_GOODBYE_CHANNEL, -1);
            return;
        }

        MessagesUtils.sendPlain(channel, text);
    }

}
