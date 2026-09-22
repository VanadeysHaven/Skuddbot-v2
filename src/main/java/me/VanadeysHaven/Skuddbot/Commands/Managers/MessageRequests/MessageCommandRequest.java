package me.VanadeysHaven.Skuddbot.Commands.Managers.MessageRequests;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class containing useful commands for message commands
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since ALPHA-2.0
 */
public class MessageCommandRequest implements CommandRequest {

    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final ServerManager sm = ServerManager.getInstance();

    @Getter private final Message message;
    private String content;
    private User sender;
    private Member member;
    private SkuddUser profile;
    private MessageChannel channel;

    public MessageCommandRequest(Message message){
        this.message = message;
    }

    @Override
    public String getContent(){
        if(content == null)
            content = getMessage().getContentRaw().substring(sm.getServer(getGuild().getIdLong()).getSettings().getString(ServerSetting.COMMAND_PREFIX).length());

        return content;
    }

    @Override
    public User getSender(){
        if(sender == null)
            sender = getMessage().getAuthor();

        return sender;
    }

    @Override
    public Member getMember(){
        if(member == null)
            member = getMessage().getMember();

        return member;
    }

    @Override
    public SkuddUser getProfile(){
        if(getChannel().getType() != ChannelType.TEXT)
            throw new UnsupportedOperationException("This message doesn't have a server, thus user profile is not available.");

        if(profile == null)
            profile = pm.getUser(getGuild().getIdLong(), getSender().getIdLong());

        return profile;
    }

    @Override
    public MessageChannel getChannel(){
        if(channel == null)
            channel = getMessage().getChannel();

        return channel;
    }

    @Override
    public Guild getGuild(){
        return getMessage().isFromGuild() ? getMessage().getGuild() : null;
    }

    @Override
    public User getUser(){
        return getSender();
    }

    @Override
    public String[] getArgs() {
        return getContent().split(" ");
    }

    @Override
    public void replyError(String message){
        addReaction(Emoji.X, message);
    }
    
    @Override
    public void reply(Emoji emoji, String message){
        MessagesUtils.sendEmoji(getChannel(), emoji, message);
    }

    @Override
    public void reply(String message) {
        MessagesUtils.sendPlain(getChannel(), message);
    }

    @Override
    public void addReaction(Emoji emoji, String message) {
        MessagesUtils.addReaction(getMessage(), emoji, message);
    }

    @Override
    public MessageCommandRequest asMessageRequest(){
        return this;
    }

    @Override
    public SlashCommandRequest asSlashRequest(){
        throw new IllegalStateException("SlashCommandRequest is not available on slash commands.");
    }
}
