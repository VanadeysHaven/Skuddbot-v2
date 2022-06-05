package me.VanadeysHaven.Skuddbot.Commands.Managers;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * [class description]
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class CommandRequest {

    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final ServerManager sm = ServerManager.getInstance();

    @Getter private final Message message;
    private String content;
    private MessageAuthor sender;
    private SkuddUser profile;
    private TextChannel channel;
    private Server server;
    private User user;

    public CommandRequest(Message message){
        this.message = message;
    }

    public String getContent(){
        if(content == null)
            content = getMessage().getContent().substring(sm.getServer(getServer().getId()).getSettings().getString(ServerSetting.COMMAND_PREFIX).length());

        return content;
    }

    public MessageAuthor getSender(){
        if(sender == null)
            sender = getMessage().getAuthor();

        return sender;
    }

    public SkuddUser getProfile(){
        if(getChannel().getType() != ChannelType.SERVER_TEXT_CHANNEL)
            throw new UnsupportedOperationException("This message doesn't have a server, thus user profile is not available.");

        if(profile == null)
            profile = pm.getUser(getServer().getId(), getSender().getId());

        return profile;
    }

    public TextChannel getChannel(){
        if(channel == null)
            channel = getMessage().getChannel();

        return channel;
    }


    public Server getServer(){
        return getMessage().getServer().orElse(null);
    }

    public User getUser(){
        if(user == null) {
            user = getSender().asUser().orElse(null);
            assert user != null;
        }

        return user;
    }

    public String[] getArgs() {
        return getContent().split(" ");
    }

}
