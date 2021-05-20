package me.VanadeysHaven.Skuddbot.Commands.Managers;

import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;

/**
 * Command without a prefix
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public abstract class NoPrefixCommand extends Command {

    public NoPrefixCommand(String[] invokers, String description, String wikiUrl, PermissionLevel requiredPermission, Location allowedLocation) {
        super(invokers, description, wikiUrl, requiredPermission, allowedLocation);
    }

}
