package me.VanadeysHaven.Skuddbot.Listeners.Reactions;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.ReactionRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Listens to reactions being added to messages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.22
 * @since 2.0
 */
public final class ReactionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReactionUtils.class);

    private final static ArrayList<ReactionButton> buttons = new ArrayList<>();

    public static ReactionButton registerButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, long... userLocks){
        return registerButton(message, emoji, clickedCallback, null,false, userLocks);
    }

    public static ReactionButton registerButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, ReactionButtonRemovedCallback removedCallback, long... userLocks){
        return registerButton(message, emoji, clickedCallback, removedCallback, false, userLocks);
    }

    public static ReactionButton registerButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, boolean invisibleReaction, long... userLocks){
        return registerButton(message, emoji, clickedCallback, null, invisibleReaction, userLocks);
    }

    private static ReactionButton registerButton(Message message, Emoji emoji, ReactionButtonClickedCallback clickedCallback, ReactionButtonRemovedCallback removedCallback, boolean invisibleReaction, long... userLocks){
        logger.info("Registering new button on message id " + message.getId() + " with emoji " +  emoji + " locked to users " + Arrays.toString(userLocks));
        if(!invisibleReaction)
            message.addReaction(emoji.getUnicode());
        ReactionButton button = new ReactionButton(message, emoji, clickedCallback, removedCallback, userLocks);
        buttons.add(button);
        return button;
    }

    public static void unregisterButton(ReactionButton button){
        logger.info("Unregistering button on message id " + button.getMessage().getId() + " with emoji " +  button.getEmoji());
        buttons.removeIf(but -> but.equals(button));
    }

    public static void runClicked(ReactionAddEvent event) {
        User user = event.getUser().orElse(null); assert user != null;
        if(user.isBot()) return;

        Message message = event.getMessage().orElse(null); assert message != null;
        Reaction reaction = event.getReaction().orElse(null); assert reaction != null;
        ReactionButton button = getButton(user, message, reaction);

        if(button != null) button.runClicked(user);
    }

    public static void runRemoved(ReactionRemoveEvent event){
        User user = event.getUser().orElse(null); assert user != null;

        if(user.isBot()) return;
        Message message = event.getMessage().orElse(null); assert message != null;
        Reaction reaction = event.getReaction().orElse(null); assert reaction != null;
        ReactionButton button = getButton(user, message, reaction);

        if(button != null) button.runRemoved(user);
    }

    public static void checkExpire(){
        for(ReactionButton button : buttons){
            button.checkExpire();
        }
    }

    private static ReactionButton getButton(User user, Message message, Reaction reaction){
        for(ReactionButton button : buttons) {
            if(message.getId() != button.getMessage().getId()) continue;

            if(!button.isEnabled()){
                if(reaction == null) continue;
                reaction.removeUser(user);
                continue;
            }

            String unicode = reaction.getEmoji().asUnicodeEmoji().orElse(null); assert unicode != null;
            if(!button.getEmoji().getUnicode().equals(unicode)) continue;
            if(!button.userIsAllowedToRun(user.getId())) continue;

            return button;
        }

        return null;
    }

    public static boolean isRegistered(ReactionButton button) {
        return buttons.contains(button);
    }
}
