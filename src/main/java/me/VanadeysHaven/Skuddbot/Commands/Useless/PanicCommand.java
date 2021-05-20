package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * We all sometimes need a little panic in our lives.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class PanicCommand extends Command {

    public PanicCommand() {
        super(new String[]{"panic"}, "PANIC!! SET THE PLACE ON FIRE!!", null);
    }

    @Override
    public void run(Message message, String content) {
        MessagesUtils.sendPlain(message.getChannel(), "EVERYONE PANIC!!!");
    }

}
