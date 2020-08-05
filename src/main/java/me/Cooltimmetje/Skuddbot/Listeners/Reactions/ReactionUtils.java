package me.Cooltimmetje.Skuddbot.Listeners.Reactions;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
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
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.0
 */
public class ReactionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReactionUtils.class);

    private static ArrayList<ReactionButton> buttons = new ArrayList<>();

    public static void run(ReactionAddEvent event) {
        for(DebugReaction reaction : MessagesUtils.reactions){
            if(event.getUser().isBot()) return;
            Message message = event.getMessage().orElse(null); assert message != null;
            if(message.getId() != reaction.getMessage().getId()) continue;
            if(event.getUser() != reaction.getMessage().getAuthor().asUser().orElse(null) && !reaction.isIgnoreUser()) continue;

            Reaction reactionObject = event.getReaction().orElse(null); assert reactionObject != null;
            String unicode = reactionObject.getEmoji().asUnicodeEmoji().orElse(null); assert unicode != null;
            if(!unicode.equals(reaction.getEmoji().getUnicode())) continue;

            reaction.post();
            return;
        }
    }

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
        if(event.getUser().isBot()) return;

        User user = event.getUser();
        Message message = event.getMessage().orElse(null); assert message != null;
        Reaction reaction = event.getReaction().orElse(null); assert reaction != null;
        ReactionButton button = getButton(user, message, reaction);

        if(button != null) button.runClicked(user);
    }

    public static void runRemoved(ReactionRemoveEvent event){
        if(event.getUser().isBot()) return;

        User user = event.getUser();
        Message message = event.getMessage().orElse(null); assert message != null;
        Reaction reaction = event.getReaction().orElse(null); assert reaction != null;
        ReactionButton button = getButton(user, message, reaction);

        if(button != null) button.runRemoved(user);
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



}
