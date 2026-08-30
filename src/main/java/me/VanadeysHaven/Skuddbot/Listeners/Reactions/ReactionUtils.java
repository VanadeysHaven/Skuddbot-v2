package me.VanadeysHaven.Skuddbot.Listeners.Reactions;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Listens to reactions being added to messages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
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
            message.addReaction(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(emoji.getUnicode())).queue();
        ReactionButton button = new ReactionButton(message, emoji, clickedCallback, removedCallback, userLocks);
        buttons.add(button);
        return button;
    }

    public static void unregisterButton(ReactionButton button){
        logger.info("Unregistering button on message id " + button.getMessage().getId() + " with emoji " +  button.getEmoji());
        buttons.removeIf(but -> but.equals(button));
    }

    public static void runClicked(MessageReactionAddEvent event) {
        User user = event.retrieveUser().complete(); assert user != null;
        if(user.isBot()) return;

        long messageId = event.getMessageIdLong();
        MessageReaction reaction = event.getReaction();
        ReactionButton button = getButton(user, messageId, getUnicode(reaction), reaction);

        if(button != null) button.runClicked(user);
    }

    public static void runRemoved(MessageReactionRemoveEvent event){
        User user = event.retrieveUser().complete(); assert user != null;

        if(user.isBot()) return;
        long messageId = event.getMessageIdLong();
        MessageReaction reaction = event.getReaction();
        ReactionButton button = getButton(user, messageId, getUnicode(reaction), null);

        if(button != null) button.runRemoved(user);
    }

    /**
     * Extracts the unicode string from a reaction, or null if the reaction is a custom (non-unicode) emoji.
     *
     * @param reaction The reaction to extract the unicode from.
     * @return The unicode string, or null if the reaction is not a unicode emoji.
     */
    private static String getUnicode(MessageReaction reaction){
        EmojiUnion emoji = reaction.getEmoji();
        if(emoji.getType() == net.dv8tion.jda.api.entities.emoji.Emoji.Type.UNICODE)
            return emoji.asUnicode().getName();
        return null;
    }

    public static void checkExpire(){
        for(ReactionButton button : buttons){
            button.checkExpire();
        }
    }

    private static ReactionButton getButton(User user, long messageId, String unicode, MessageReaction reaction){
        for(ReactionButton button : buttons) {
            if(messageId != button.getMessage().getIdLong()) continue;

            if(!button.isEnabled()){
                if(reaction == null) continue;
                reaction.removeReaction(user).queue();
                continue;
            }

            if(unicode == null) continue;
            if(!button.getEmoji().getUnicode().equals(unicode)) continue;
            if(!button.userIsAllowedToRun(user.getIdLong())) continue;

            return button;
        }

        return null;
    }

    public static boolean isRegistered(ReactionButton button) {
        return buttons.contains(button);
    }
}
