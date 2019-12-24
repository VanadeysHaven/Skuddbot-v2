package me.Cooltimmetje.Skuddbot.Listeners;

import me.Cooltimmetje.Skuddbot.Utilities.DebugReaction;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
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
            if(!event.getReaction().orElse(null).getEmoji().asUnicodeEmoji().orElse(null).equals(reaction.getEmoji())) continue; //TODO: FIX NULL WARNINGS

            reaction.post();
        }
    }

}
