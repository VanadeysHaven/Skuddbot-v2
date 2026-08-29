package me.VanadeysHaven.Skuddbot.Listeners;

import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandManager;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Central JDA event dispatcher. Replaces the six Javacord lambda registrations that used
 * to live in {@link me.VanadeysHaven.Skuddbot.Skuddbot#registerListeners()} and forwards
 * each event on to the existing handlers.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class SkuddEventListener extends ListenerAdapter {

    private final CommandManager commandManager;

    public SkuddEventListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        commandManager.process(event.getMessage());
        MessageListener.run(event.getMessage());
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        ReactionUtils.runClicked(event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        ReactionUtils.runRemoved(event);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        JoinQuitServerListener.join(event);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        JoinQuitServerListener.leave(event);
    }

}
