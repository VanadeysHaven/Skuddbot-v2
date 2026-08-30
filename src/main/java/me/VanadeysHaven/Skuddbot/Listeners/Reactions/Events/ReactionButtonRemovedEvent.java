package me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButton;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/**
 * Event information about the reaction button removed.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.2.1
 */
public class ReactionButtonRemovedEvent extends ReactionButtonEvent {

    public ReactionButtonRemovedEvent(Message message, Emoji emoji, User user, ReactionButton button){
        super(message, emoji, user, button);
    }

}
