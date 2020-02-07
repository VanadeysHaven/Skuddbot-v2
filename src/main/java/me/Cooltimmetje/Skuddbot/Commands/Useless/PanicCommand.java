package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * We all sometimes need a little panic in our lives.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class PanicCommand extends Command {

    public PanicCommand() {
        super(new String[]{"panic"}, "PANIC!! SET THE PLACE ON FIRE!!");
    }

    @Override
    public void run(Message message, String content) {
        MessagesUtils.sendPlain(message.getChannel(), "EVERYONE PANIC!!!");
    }

}
