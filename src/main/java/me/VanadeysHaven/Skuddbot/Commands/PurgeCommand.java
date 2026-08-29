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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used to purge messages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
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
                ourMessage.editMessage(text).queue();
        }

        private void askConfirmation(){
            sendMessage(Emoji.QUESTION.getUnicode() + " You are about to purge " + amountToDelete + " messages. Are you sure you want to continue? - When you proceed, there's no way to stop this operation!");
            confirm = ReactionUtils.registerButton(ourMessage, Emoji.WHITE_CHECK_MARK, this::confirm, originMessage.getAuthor().getIdLong());
            cancel = ReactionUtils.registerButton(ourMessage, Emoji.X, this::cancel, originMessage.getAuthor().getIdLong());
        }

        private void confirm(ReactionButtonClickedEvent event){
            confirm.unregister();
            cancel.unregister();
            ourMessage.clearReactions().queue();
            startPurge();
        }

        private void cancel(ReactionButtonClickedEvent event){
            confirm.unregister();
            cancel.unregister();
            ourMessage.clearReactions().queue();
            sendMessage(Emoji.WHITE_CHECK_MARK.getUnicode() + " Purge cancelled.");
        }

        private void startPurge(){
            sendMessage("<a:loading:738035021524238366> **Purging " + amountToDelete + " messages...** *Hang tight, this might take a while!*");
            try {
                MessageChannel channel = originMessage.getChannel();
                List<Message> toDelete = new ArrayList<>();
                Message before = originMessage;
                while(toDelete.size() < amountToDelete){
                    int batch = Math.min(100, amountToDelete - toDelete.size());
                    List<Message> history = channel.getHistoryBefore(before, batch).complete().getRetrievedHistory();
                    if(history.isEmpty()) break;
                    toDelete.addAll(history);
                    before = history.get(history.size() - 1);
                }

                channel.purgeMessages(toDelete);
                originMessage.delete().queue();
                sendMessage(Emoji.WHITE_CHECK_MARK.getUnicode() + " " + amountToDelete + " messages purged.");
                ourMessage.clearReactions().queue();
            } catch (RuntimeException e) {
                sendMessage(Emoji.X.getUnicode() + " Oops... something happened. The purge has not been completed fully.");
            }
        }

    }

}
