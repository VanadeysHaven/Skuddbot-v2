package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * This class is a simple ping command.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class PingCommand extends Command {

    public PingCommand(){
        super(new String[] {"ping"}, "Ping command for testing bot responses.", PermissionLevel.DEFAULT);
    }

    @Override
    public void run(Message message, String messageContent) {
//        message.getChannel().sendMessage("PONG!");
        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "PONG!");
    }

}
