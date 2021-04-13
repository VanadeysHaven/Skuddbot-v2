package me.Cooltimmetje.Skuddbot.Commands.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Command for clearing cooldowns.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class ClearCooldownCommand extends Command {

    public ClearCooldownCommand(){
        super(new String[]{"clearcooldown", "cc"}, "Used to clear cooldowns.", null, PermissionLevel.BOT_ADMIN, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        boolean forceClear = false;
        if(args.length > 1) if(args[1].equalsIgnoreCase("-force")) forceClear = true;

        CooldownManager.clearAll(forceClear);
        if(forceClear)
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "All cooldowns cleared forcefully.");
        else
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "All cooldowns cleared!");
    }

}
