package me.Cooltimmetje.Skuddbot.Commands.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Command for clearing cooldowns.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ClearCooldownCommand extends Command {

    public ClearCooldownCommand(){
        super(new String[]{"clearcooldown", "cc"}, "Used to clear cooldowns.", PermissionLevel.BOT_ADMIN, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        CooldownManager.clearAll();
        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "All cooldowns cleared!");
    }

}
