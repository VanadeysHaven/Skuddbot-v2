package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Minigames.GameLogs.GenericGameLog;
import me.VanadeysHaven.Skuddbot.Profiles.ServerMember;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.TimeUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

/**
 * Command for testing purposes.
 *
 * @author Tim (Vanadey's Haven)
 * @version [not deployed]
 * @since [not deployed]
 */
public final class TestCommand extends Command {

    public TestCommand() {
        super(new String[]{"test"}, "Testing 1 2 3", null, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        long curTime = System.currentTimeMillis();
        GenericGameLog log = new GenericGameLog("test_" + curTime, "Test Log: " + TimeUtils.formatTime(curTime));
        ServerMember member = new ServerMember(message.getServer().get().getId(), message.getAuthor().getId());

        log.addToLog("Hi!");
        log.addToLog("I'm a game log!");
        log.addToLog("I was created at " + TimeUtils.formatTime(curTime) + " by " + member.getGameLogName());
        log.addToLog("Thank you for reading!");

        log.sendLog(message, Emoji.MAILBOX_WITH_MAIL, 1);
    }

    private class TestInstance {

        private Message message;
        private TextChannel channel;

        private TestInstance(TextChannel channel){
            this.channel = channel;
            sendMessage("Waiting for reaction change...");
            ReactionUtils.registerButton(message, Emoji.EYES, this::added, this::removed);
        }

        private void added(ReactionButtonClickedEvent event){
            sendMessage("Reaction was added.");
        }

        private void removed(ReactionButtonRemovedEvent event) {
            sendMessage("Reaction was removed.");
        }

        private void sendMessage(String text){
            if(message == null)
                message = MessagesUtils.sendPlain(channel, text);
            else
                message.edit(text);
        }

    }

}
