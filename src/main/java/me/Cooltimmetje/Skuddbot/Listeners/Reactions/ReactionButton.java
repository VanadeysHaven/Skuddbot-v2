package me.Cooltimmetje.Skuddbot.Listeners.Reactions;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about a reaction button.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.1.1
 */
public class ReactionButton {

    private static final Logger logger = LoggerFactory.getLogger(ReactionButton.class);

    @Getter private Emoji emoji;
    @Getter private Message message;
    private Reaction reaction;
    private ReactionButtonCallback callback;
    @Getter private long[] userLocks;
    @Getter @Setter private boolean enabled;

    public ReactionButton(Message message, Emoji emoji, ReactionButtonCallback callback, long... userLocks){
        this.message = message;
        this.emoji = emoji;
        message.addReaction(emoji.getUnicode());
        Reaction reaction = message.getReactionByEmoji(emoji.getUnicode()).orElse(null); assert reaction != null;
        this.reaction = reaction;
        this.callback = callback;
        this.userLocks = userLocks;

        enabled = true;
    }

    public void runButton(User user){
        logger.info("Running callback for user " + user.getIdAsString() + " on message id " + message.getId());
        callback.buttonClicked(new ReactionButtonClickedEvent(message, emoji, reaction, user));
    }

    public boolean userIsAllowedToRun(long userId){
        if(userLocks.length == 0) return true;
        for(long l : userLocks)
            if(l == userId) return true;

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionButton button = (ReactionButton) o;
        return emoji == button.emoji && message.getId() == button.getMessage().getId();
    }

}
