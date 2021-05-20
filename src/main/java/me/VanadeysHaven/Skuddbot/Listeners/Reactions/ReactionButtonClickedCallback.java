package me.VanadeysHaven.Skuddbot.Listeners.Reactions;

import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;

/**
 * Event runnable for reaction buttons
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.1.1
 */
public interface ReactionButtonClickedCallback {

    void run(ReactionButtonClickedEvent event);

}
