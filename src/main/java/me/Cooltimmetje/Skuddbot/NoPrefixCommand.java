package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;

/**
 * [class description]
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public abstract class NoPrefixCommand extends Command {

    public NoPrefixCommand(String[] invokers, String description, PermissionLevel requiredPermission, Location allowedLocation) {
        super(invokers, description, requiredPermission, allowedLocation);
    }

}
