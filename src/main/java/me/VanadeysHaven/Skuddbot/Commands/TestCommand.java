package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonRemovedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
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
        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Test", 60000, false);
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
