package me.VanadeysHaven.Skuddbot.Commands.Managers;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * [class description]
 *
 * @author Tim (Cooltimmetje)
 * @version 2.4
 * @since ALPHA-2.0
 */
public class CommandRequest {

    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final ServerManager sm = ServerManager.getInstance();

    @Getter private final Message message;
    private String content;
    private User sender;
    private Member member;
    private SkuddUser profile;
    private MessageChannel channel;

    public CommandRequest(Message message){
        this.message = message;
    }

    public String getContent(){
        if(content == null)
            content = getMessage().getContentRaw().substring(sm.getServer(getGuild().getIdLong()).getSettings().getString(ServerSetting.COMMAND_PREFIX).length());

        return content;
    }

    public User getSender(){
        if(sender == null)
            sender = getMessage().getAuthor();

        return sender;
    }

    public Member getMember(){
        if(member == null)
            member = getMessage().getMember();

        return member;
    }

    public SkuddUser getProfile(){
        if(getChannel().getType() != ChannelType.TEXT)
            throw new UnsupportedOperationException("This message doesn't have a server, thus user profile is not available.");

        if(profile == null)
            profile = pm.getUser(getGuild().getIdLong(), getSender().getIdLong());

        return profile;
    }

    public MessageChannel getChannel(){
        if(channel == null)
            channel = getMessage().getChannel();

        return channel;
    }


    public Guild getGuild(){
        return getMessage().isFromGuild() ? getMessage().getGuild() : null;
    }

    public User getUser(){
        return getSender();
    }

    public String[] getArgs() {
        return getContent().split(" ");
    }

}
