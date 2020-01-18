package me.Cooltimmetje.Skuddbot.Commands;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import org.javacord.api.entity.message.Message;

import java.text.MessageFormat;

/**
 * This class is a command, can be extended to other classes to make for a flexible command system.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public abstract class Command {

    private static final String HELP_FORMAT =  "- {0}\n*{1}*";
    private static final int MAX_ALIASES = 3;

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

    public String formatHelp(String invoker){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_ALIASES && i < invokers.length; i++){
            sb.append("`").append(invoker).append(invokers[i]).append("`, ");
        }
        String invokerString = sb.toString();
        invokerString = invokerString.substring(0, invokerString.length() - 2);
        if(invokers.length > MAX_ALIASES) {
            int remainingAliases = invokers.length - MAX_ALIASES;
            invokerString += " +" + remainingAliases + " other " + (remainingAliases == 1 ? "alias" : "aliases");
        }

        return MessageFormat.format(HELP_FORMAT, invokerString, description);
    }

    public enum Location {
        SERVER, DM, BOTH
    }

}
