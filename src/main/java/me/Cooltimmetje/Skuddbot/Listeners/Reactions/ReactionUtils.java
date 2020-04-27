package me.Cooltimmetje.Skuddbot.Listeners.Reactions;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.DebugReaction;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Listens to reactions being added to messages.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.0
 */
public class ReactionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReactionUtils.class);

    private static ArrayList<ReactionButton> buttons = new ArrayList<>();

    public static void run(ReactionAddEvent event) {
        for(DebugReaction reaction : MessagesUtils.reactions){
            if(event.getUser() != reaction.getMessage().getAuthor().asUser().orElse(null)) continue;

            Reaction reactionObject = event.getReaction().orElse(null); assert reactionObject != null;
            String unicode = reactionObject.getEmoji().asUnicodeEmoji().orElse(null); assert unicode != null;
            if(!unicode.equals(reaction.getEmoji().getUnicode())) continue;

            Message message = event.getMessage().orElse(null); assert message != null;
            if(message.getId() != reaction.getMessage().getId()) continue;

            reaction.post();
            return;
        }
    }

    public static void registerButton(Message message, Emoji emoji, ReactionButtonCallback callback, long... userLocks){
        logger.info("Registering new button on message id " + message.getId() + " with emoji " +  emoji + " locked to users " + Arrays.toString(userLocks));
        message.addReaction(emoji.getUnicode());
        buttons.add(new ReactionButton(message, emoji, callback, userLocks));
    }

    public static void runButtons(ReactionAddEvent event) {
        for (ReactionButton button : buttons) {
            Reaction reactionObject = event.getReaction().orElse(null);
            assert reactionObject != null;
            String unicode = reactionObject.getEmoji().asUnicodeEmoji().orElse(null);
            assert unicode != null;
            if (!button.getEmoji().getUnicode().equals(unicode)) continue;

            Message message = event.getMessage().orElse(null);
            assert message != null;
            if (message.getId() != button.getMessage().getId()) continue;

            if(!button.userIsAllowedToRun(event.getUser().getId())) continue;

            button.runButton(event.getUser());
            return;
        }
    }



}
