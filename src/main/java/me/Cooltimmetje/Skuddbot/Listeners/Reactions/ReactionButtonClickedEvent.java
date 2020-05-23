package me.Cooltimmetje.Skuddbot.Listeners.Reactions;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Event information about the reaction button clicked.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.1.1
 */
public class ReactionButtonClickedEvent {

    @Getter private Message message;
    @Getter private Emoji emoji;
    @Getter private User user;

    public ReactionButtonClickedEvent(Message message, Emoji emoji, User user){
        this.message = message;
        this.emoji = emoji;
        this.user = user;
    }

    public void undoReaction(){
        Reaction reaction = message.getReactionByEmoji(emoji.getUnicode()).orElse(null); assert reaction != null;
        reaction.removeUser(user);
    }

    public ServerMember getUserAsMember(){
        return new ServerMember(getUserId());
    }

    public Identifier getUserId(){
        Server server = message.getServer().orElse(null); assert server != null;
        return new Identifier(server.getId(), user.getId());
    }

}
