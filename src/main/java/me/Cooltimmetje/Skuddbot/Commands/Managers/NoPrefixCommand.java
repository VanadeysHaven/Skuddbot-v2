package me.Cooltimmetje.Skuddbot.Commands.Managers;

import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;

/**
 * Command without a prefix
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
