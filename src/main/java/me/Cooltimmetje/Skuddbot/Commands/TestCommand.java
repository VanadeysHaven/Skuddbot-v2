package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Minigames.GameLogs.GenericGameLog;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TimeUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

/**
 * Command for testing purposes.
 *
 * @author Tim (Cooltimmetje)
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

        log.addToLog("Hi!");
        log.addToLog("I'm a game log!");
        log.addToLog("I was created at " + TimeUtils.formatTime(curTime));
        log.addToLog("Thank you for reading!");

        log.sendLog(message, Emoji.MAILBOX_WITH_MAIL);
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
