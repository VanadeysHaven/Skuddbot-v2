package me.VanadeysHaven.Skuddbot.Listeners.Reactions;

import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;

/**
 * Callback for when a reaction button gets a reaction removed.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public interface ReactionButtonRemovedCallback {

    void run(ReactionButtonRemovedEvent event);

}
