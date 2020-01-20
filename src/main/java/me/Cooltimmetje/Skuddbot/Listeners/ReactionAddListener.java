package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Utilities.DebugReaction;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

/**
 * Listens to reactions being added to messages.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ReactionAddListener {

    public static void run(ReactionAddEvent event) {
        for(DebugReaction reaction : MessagesUtils.reactions){
            if(event.getUser() != reaction.getMessage().getAuthor().asUser().orElse(null)) continue;
            Reaction reactionObject = event.getReaction().orElse(null); assert reactionObject != null;
            String unicode = reactionObject.getEmoji().asUnicodeEmoji().orElse(null); assert unicode != null;
            if(!unicode.equals(reaction.getEmoji().getUnicode())) continue;
            Message message = event.getMessage().orElse(null); assert message != null;
            if(message.getId() != reaction.getMessage().getId()) continue;

            reaction.post();
        }
    }

}
