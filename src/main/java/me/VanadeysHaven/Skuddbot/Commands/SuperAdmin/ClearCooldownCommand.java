package me.VanadeysHaven.Skuddbot.Commands.SuperAdmin;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Utilities.CooldownManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Command for clearing cooldowns.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class ClearCooldownCommand extends Command {

    public ClearCooldownCommand(){
        super(new String[]{"clearcooldown", "cc"}, "Used to clear cooldowns.", null, PermissionLevel.BOT_ADMIN, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getContent().split(" ");
        Message message = request.getMessage();
        boolean forceClear = false;
        if(args.length > 1) if(args[1].equalsIgnoreCase("-force")) forceClear = true;

        CooldownManager.clearAll(forceClear);
        if(forceClear)
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "All cooldowns cleared forcefully.");
        else
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "All cooldowns cleared!");
    }

}
