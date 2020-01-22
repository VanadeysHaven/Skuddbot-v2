package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
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
        super(new String[] {"ping"}, "Ping command for testing bot responses.", Location.BOTH);
    }

    @Override
    public void run(Message message, String messageContent) {
        if(dm.isDonator(message.getAuthor().getId())){
            String text = dm.getUser(message.getAuthor().getId()).getPingMessage();
            if(text != null){
                MessagesUtils.sendEmoji(message.getChannel(), Emoji.WHITE_CHECK_MARK, text);
                return;
            }
        }

        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "PONG!");
    }

}
