package me.Cooltimmetje.Skuddbot.Commands.Donator;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
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
    public void run(Message message, String content) {
        message.getChannel().sendMessage("hello");
    }
}
