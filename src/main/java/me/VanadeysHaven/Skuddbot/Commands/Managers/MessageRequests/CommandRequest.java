package me.VanadeysHaven.Skuddbot.Commands.Managers.MessageRequests;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Interface for useful functions for during commands.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.4
 */
public interface CommandRequest {
    
    public String getContent();

    public User getSender();

    public Member getMember();

    public SkuddUser getProfile();
    
    public MessageChannel getChannel();

    public Guild getGuild();

    public User getUser();

    public String[] getArgs();

    public void replyError(String message);

    public void reply(Emoji emoji, String message);

    public void reply(String message);

    public void addReaction(Emoji emoji, String message);

    public MessageCommandRequest asMessageRequest();

    public SlashCommandRequest asSlashRequest();

}
