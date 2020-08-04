package me.Cooltimmetje.Skuddbot.Listeners.Reactions;

import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;

/**
 * Callback for when a reaction button gets a reaction removed.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.2.1
 */
public interface ReactionButtonRemovedCallback {

    void run(ReactionButtonRemovedEvent event);

}
