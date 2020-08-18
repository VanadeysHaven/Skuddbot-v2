package me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Base abstract event class for reaction button events.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public abstract class ReactionButtonEvent {

    @Getter private Message message;
    @Getter private Emoji emoji;
    @Getter private User user;
    @Getter private ReactionButton button;

    public ReactionButtonEvent(Message message, Emoji emoji, User user, ReactionButton button){
        this.message = message;
        this.emoji = emoji;
        this.user = user;
        this.button = button;
    }

    public ServerMember getUserAsMember(){
        return new ServerMember(getUserId());
    }

    public Identifier getUserId(){
        Server server = message.getServer().orElse(null); assert server != null;
        return new Identifier(server.getId(), user.getId());
    }

}
