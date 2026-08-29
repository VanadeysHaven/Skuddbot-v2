package me.VanadeysHaven.Skuddbot.Commands.Donator;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Donator.DonatorMessage;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.entities.Message;

/**
 * Commands for donators so they can add messages to the donator pool.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class ManageMessageCommand extends Command {

    public ManageMessageCommand() {
        super(new String[]{"message"}, "Used to manage messages in the donator message pool.", null, PermissionLevel.DONATOR, Location.DM);
    }

    @Override
    public void run(CommandRequest request) { //TODO managing of messages
        String[] args = request.getContent().split(" ");
        DonatorMessage.Type type;

        if(args.length < 3){
            MessagesUtils.addReaction(request.getMessage(), Emoji.X, "Invalid usage: `!message <add> <type> <content/images>`");
            return;
        }

        try {
            type = DonatorMessage.Type.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            MessagesUtils.addReaction(request.getMessage(), Emoji.X, args[2] + " is not a message type.");
            return;
        }

        if (args.length >= 4) {
            addMessage(request.getMessage(), type, args);
        } else if(type.isAcceptsImages()){
            addImage(request.getMessage(), type);
        } else {
            if(!type.isAcceptsImages() && request.getMessage().getAttachments().size() > 0){
                MessagesUtils.addReaction(request.getMessage(), Emoji.X, "Type `" + type + "` does not support image uploads!");
            }
            MessagesUtils.addReaction(request.getMessage(), Emoji.X, "Invalid usage: `!message <add> <type> <content/images>`");
        }

    }

    private void addMessage(Message message, DonatorMessage.Type type, String[] args){
        StringBuilder sb = new StringBuilder();
        for(int i=3; i < args.length; i++){
            sb.append(args[i]).append(" ");
        }

        String input = sb.toString().trim();
        String trimmed = input.substring(0, Math.min(input.length(), type.getMaxLength()));

        if(dm.doesMessageExist(type, trimmed)){
            MessagesUtils.addReaction(message, Emoji.X, "This message and type combination already exists.");
            return;
        }

        if(input.length() > type.getMaxLength()){
            MessagesUtils.addReaction(message, Emoji.WARNING, "The message exceeds the __" + type.getMaxLength() + " character limit__ set for this type. For your convenience this is the message trimmed down to the correct length: \n```" + trimmed + "```");
            return;
        }

        dm.addMessage(dm.getUser(message.getAuthor().getIdLong()), type, trimmed).save();
        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + trimmed + "` as a `" + type + "` message!");
    }

    private void addImage(Message message, DonatorMessage.Type type){
        int amountAdded = 0;

        if(message.getAttachments().size() <= 0){
            MessagesUtils.addReaction(message, Emoji.X, "Invalid usage: `!message <add> <type> <content/images>`");
            return;
        }

        DonatorMessage lastAdded = null;
        for(Message.Attachment attachment : message.getAttachments())
            if(attachment.isImage() && !dm.doesMessageExist(type, attachment.getUrl())) {
                lastAdded = dm.addMessage(dm.getUser(message.getAuthor().getIdLong()), type, attachment.getUrl());
                lastAdded.save();
                amountAdded++;
            }

        if(amountAdded == 1){
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + lastAdded.getMessage() + "` as a `" + type + "` message!");
        } else {
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + amountAdded + "` images as a `" + type + "` message!");
        }
    }
}
