package me.Cooltimmetje.Skuddbot.Commands.Donator;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import org.javacord.api.entity.message.Message;

/**
 * <class discription>
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class AddMessageCommand extends Command {

    public AddMessageCommand() {
        super(new String[]{"addmsg"}, "Used to add messages to the donator message pool.", PermissionLevel.DONATOR);
    }

    @Override
    public void run(Message message, String content) {

    }
}
