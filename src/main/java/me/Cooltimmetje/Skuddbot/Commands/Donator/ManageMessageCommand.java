package me.Cooltimmetje.Skuddbot.Commands.Donator;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Commands for donators so they can add messages to the donator pool.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ManageMessageCommand extends Command {

    public ManageMessageCommand() {
        super(new String[]{"message"}, "Used to manage messages in the donator message pool.", PermissionLevel.DONATOR, Location.DM);
    }

    @Override
    public void run(Message message, String content) { //TODO managing of messages
        String[] args = content.split(" ");
        if (args.length < 4) {
            MessagesUtils.addReaction(message, Emoji.X, "Invalid usage: `!message <add> <type> <content>`");
            return;
        }

        DonatorMessage.Type type;
        try {
            type = DonatorMessage.Type.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            MessagesUtils.addReaction(message, Emoji.X, args[2] + " is not a message type.");
            return;
        }

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
        } else {
            dm.addMessage(dm.getUser(message.getAuthor().getId()), type, trimmed).save();
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + trimmed + "` as a `" + type + "` message!");
        }
    }
}
