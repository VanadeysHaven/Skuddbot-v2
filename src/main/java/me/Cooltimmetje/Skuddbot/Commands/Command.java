package me.Cooltimmetje.Skuddbot.Commands;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import org.javacord.api.entity.message.Message;

/**
 * This class is a command, can be extended to other classes to make for a flexible command system.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public abstract class Command {

    @Getter private String[] invokers;
    @Getter private String description;
    @Getter private PermissionLevel requiredPermission;
    @Getter private Location allowedLocation;

    public Command(String[] invokers, String description, PermissionLevel requiredPermission, Location allowedLocation) {
        this.invokers = invokers;
        this.description = description;
        this.requiredPermission = requiredPermission;
        this.allowedLocation = allowedLocation;
    }

    public Command(String[] invokers, String description){
        this(invokers, description, PermissionLevel.DEFAULT, Location.SERVER);
    }

    public abstract void run(Message message, String content);

    public enum Location {
        SERVER, DM, BOTH
    }

}
