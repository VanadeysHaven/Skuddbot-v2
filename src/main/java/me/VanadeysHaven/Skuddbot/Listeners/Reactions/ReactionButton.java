package me.VanadeysHaven.Skuddbot.Listeners.Reactions;

import lombok.Getter;
import lombok.Setter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about a reaction button.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.22
 * @since 2.1.1
 */
public final class ReactionButton {

    private static final Logger logger = LoggerFactory.getLogger(ReactionButton.class);

    @Getter private Emoji emoji;
    @Getter private Message message;
    private ReactionButtonClickedCallback clickedCallback;
    private ReactionButtonRemovedCallback removedCallback;
    @Getter private long[] userLocks;
    @Getter @Setter private boolean enabled;
    private long expireAt;
    private boolean oneTimeUse;

    public ReactionButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, long... userLocks){
        this(message, emoji, clickedCallback, null, userLocks);
    }

    public ReactionButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, ReactionButtonRemovedCallback removedCallback, long... userLocks) {
        this.message = message;
        this.emoji = emoji;
        this.clickedCallback = clickedCallback;
        this.removedCallback = removedCallback;
        this.userLocks = userLocks;
        this.expireAt = -1;
        this.oneTimeUse = false;

        enabled = true;
    }

    public void runClicked(User user){
        logger.info("Running clicked callback for user " + user.getIdAsString() + " on message id " + message.getId());
        clickedCallback.run(new ReactionButtonClickedEvent(message, emoji, user, this));

        if(oneTimeUse) unregister();
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

    public boolean isRegistered(){
        return ReactionUtils.isRegistered(this);
    }

    public void expireAfter(long expireAfter) {
        expireAt = System.currentTimeMillis() + expireAfter;
    }

    public boolean isExpired() {
        return expireAt != -1 && expireAt < System.currentTimeMillis();
    }

    public void checkExpire() {
        if(isExpired()) unregister();
    }

    public void setOneTimeUse() {
        oneTimeUse = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionButton button = (ReactionButton) o;
        return emoji == button.emoji && message.getId() == button.getMessage().getId();
    }

}
