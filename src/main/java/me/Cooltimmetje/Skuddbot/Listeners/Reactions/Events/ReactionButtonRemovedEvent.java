package me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

/**
 * Event information about the reaction button removed.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.2.1
 */
public class ReactionButtonRemovedEvent extends ReactionButtonEvent {

    public ReactionButtonRemovedEvent(Message message, Emoji emoji, User user){
        super(message, emoji, user);
    }

}
