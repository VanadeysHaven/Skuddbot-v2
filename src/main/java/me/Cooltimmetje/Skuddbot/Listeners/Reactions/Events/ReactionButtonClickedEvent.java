package me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;

/**
 * Event information about the reaction button clicked.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.1.1
 */
public class ReactionButtonClickedEvent extends ReactionButtonEvent {

    public ReactionButtonClickedEvent(Message message, Emoji emoji, User user){
        super(message, emoji, user);
    }

    public void undoReaction(){
        Reaction reaction = getMessage().getReactionByEmoji(getEmoji().getUnicode()).orElse(null); assert reaction != null; //TODO: move this operation into the ReactionButton class
        reaction.removeUser(getUser());
    }

}