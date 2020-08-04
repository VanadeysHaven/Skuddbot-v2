package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Command used to purge messages.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.2.1
 */
public class PurgeCommand extends Command {

    private static int CONFIRMATION_LIMIT = 500;

    public PurgeCommand() {
        super(new String[]{"purge"}, "Command used to purge messages.", PermissionLevel.SERVER_ADMIN);
    }

    @Override
    public void run(Message message, String content) {
        String str = content.split(" ")[1];
        if(!MiscUtils.isInt(content.split(" ")[1]))
            MessagesUtils.addReaction(message, Emoji.X, str + " is not a integer.");
        int amountToDelete = Integer.parseInt(str);

        new PurgeOperation(message, amountToDelete);
    }

    private class PurgeOperation {

        private Message originMessage;
        private Message ourMessage;
        private int amountToDelete;
        private CompletableFuture<Void> future;
        private ReactionButton confirm;
        private ReactionButton cancel;

        public PurgeOperation(Message originMessage, int amountToDelete){
            this.originMessage = originMessage;
            this.amountToDelete = amountToDelete;

            if(amountToDelete > CONFIRMATION_LIMIT)
                askConfirmation();
            else
                startPurge();
        }

        private void sendMessage(String text){
            if (ourMessage == null)
                ourMessage = MessagesUtils.sendPlain(originMessage.getChannel(), text);
            else
                ourMessage.edit(text);
        }

        private void askConfirmation(){
            sendMessage(Emoji.QUESTION.getUnicode() + " You are about to purge " + amountToDelete + " messages. Are you sure you want to continue? - When you proceed, there's no way to stop this operation!");
            confirm = ReactionUtils.registerButton(ourMessage, Emoji.WHITE_CHECK_MARK, this::confirm, originMessage.getAuthor().getId());
            cancel = ReactionUtils.registerButton(ourMessage, Emoji.X, this::cancel, originMessage.getAuthor().getId());
        }

        private void confirm(ReactionButtonClickedEvent event){
            confirm.unregister();
            cancel.unregister();
            ourMessage.removeAllReactions();
            startPurge();
        }

        private void cancel(ReactionButtonClickedEvent event){
            confirm.unregister();
            cancel.unregister();
            ourMessage.removeAllReactions();
            sendMessage(Emoji.WHITE_CHECK_MARK.getUnicode() + " Purge cancelled.");
        }

        private void startPurge(){
            sendMessage("<a:loading:738035021524238366> **Purging " + amountToDelete + " messages...** *Hang tight, this might take a while!*");
            try {
                future = originMessage.getChannel().getMessagesBefore(amountToDelete, originMessage).get().deleteAll();
                future.thenRun(() -> {
                    originMessage.delete();
                    sendMessage(Emoji.WHITE_CHECK_MARK.getUnicode() + " " + amountToDelete + " messages purged.");
                    ourMessage.removeAllReactions();
                });
            } catch (InterruptedException | ExecutionException e) {
                sendMessage(Emoji.X.getUnicode() + " Oops... something happened. The purge has not been completed fully.");
            }
        }

    }

}
