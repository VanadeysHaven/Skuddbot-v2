package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

/**
 * Command for testing purposes.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.0
 */
public class TestCommand extends Command {

    public TestCommand() {
        super(new String[]{"test"}, "Testing 1 2 3", Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        new TestInstance(message.getChannel());
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
