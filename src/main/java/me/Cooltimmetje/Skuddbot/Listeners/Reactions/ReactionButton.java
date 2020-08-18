package me.Cooltimmetje.Skuddbot.Listeners.Reactions;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about a reaction button.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.1.1
 */
public class ReactionButton {

    private static final Logger logger = LoggerFactory.getLogger(ReactionButton.class);

    @Getter private Emoji emoji;
    @Getter private Message message;
    private ReactionButtonClickedCallback clickedCallback;
    private ReactionButtonRemovedCallback removedCallback;
    @Getter private long[] userLocks;
    @Getter @Setter private boolean enabled;

    public ReactionButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, long... userLocks){
        this(message, emoji, clickedCallback, null, userLocks);
    }

    public ReactionButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, ReactionButtonRemovedCallback removedCallback, long... userLocks) {
        this.message = message;
        this.emoji = emoji;
        this.clickedCallback = clickedCallback;
        this.removedCallback = removedCallback;
        this.userLocks = userLocks;

        enabled = true;
    }

    public void runClicked(User user){
        logger.info("Running clicked callback for user " + user.getIdAsString() + " on message id " + message.getId());
        clickedCallback.run(new ReactionButtonClickedEvent(message, emoji, user, this));
    }

    public void runRemoved(User user){
        if(removedCallback == null) {
            logger.info("Removed callback triggered for user " + user.getIdAsString() + " on message id " + message.getId() + " but there's no removed callback present.");
            return;
        }

        logger.info("Running removed callback for user " + user.getIdAsString() + " on message id " + message.getId());
        removedCallback.run(new ReactionButtonRemovedEvent(message, emoji, user, this));
    }

    public void removeReaction(User user){
        if(removedCallback != null) throw new UnsupportedOperationException("You can not remove reactions from a button that has a removed callback associated with it.");

        Reaction reaction = getMessage().getReactionByEmoji(getEmoji().getUnicode()).orElse(null); assert reaction != null;
        reaction.removeUser(user);
    }

    public boolean userIsAllowedToRun(long userId){
        if(userLocks.length == 0) return true;
        for(long l : userLocks)
            if(l == userId) return true;

        return false;
    }

    public void unregister(){
        ReactionUtils.unregisterButton(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionButton button = (ReactionButton) o;
        return emoji == button.emoji && message.getId() == button.getMessage().getId();
    }

}
