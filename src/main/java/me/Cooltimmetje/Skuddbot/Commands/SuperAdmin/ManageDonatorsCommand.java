package me.Cooltimmetje.Skuddbot.Commands.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import org.javacord.api.entity.message.Message;

/**
 * Used to manage donators.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ManageDonatorsCommand extends Command {


    public ManageDonatorsCommand() {
        super(new String[]{"donators"}, "Used to add and remove donators.", PermissionLevel.BOT_ADMIN, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {

    }

}
