package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButton;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Command used to purge messages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.2.1
 */
public class PurgeCommand extends Command {

    private static int CONFIRMATION_LIMIT = 500;

    public PurgeCommand() {
        super(new String[]{"purge"}, "Command used to purge messages.", "https://wiki.skuddbot.xyz/moderation-tools/purge", PermissionLevel.SERVER_ADMIN);
    }

    @Override
    public void run(CommandRequest request) {
        String str = request.getArgs()[1];
        if(!MiscUtils.isInt(str))
            MessagesUtils.addReaction(request.getMessage(), Emoji.X, str + " is not a integer.");
        int amountToDelete = Integer.parseInt(str);

        new PurgeOperation(request.getMessage(), amountToDelete);
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
