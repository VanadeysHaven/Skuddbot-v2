package me.Cooltimmetje.Skuddbot.Commands.Managers;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import org.javacord.api.entity.message.Message;

import java.text.MessageFormat;

/**
 * This class is a command, can be extended to other classes to make for a flexible command system.
 *
 * @author Tim (Cooltimmetje)
 * @since 2.2.1
 * @version 2.0
 */
public abstract class Command {

    protected static final ServerManager sm = ServerManager.getInstance();
    protected static final ProfileManager pm = ProfileManager.getInstance();
    protected static final DonatorManager dm = new DonatorManager();

    private static final String HELP_FORMAT = "{0}\n" +
            "> *{1}*\n" +
            "> Wiki: {2}\n";
    private static final String HELP_FORMAT_NO_WIKI =  "{0}\n" +
            "> *{1}*\n";
    private static final int MAX_ALIASES = 3;
    private static final PermissionLevel DEFAULT_PERMISSION = PermissionLevel.DEFAULT;
    private static final Location DEFAULT_LOCATION = Location.SERVER;

    @Getter private String[] invokers;
    @Getter private String description;
    private String wikiUrl;
    @Getter private PermissionLevel requiredPermission;
    @Getter private Location allowedLocation;

    public Command(String[] invokers, String description, String wikiUrl, PermissionLevel requiredPermission, Location allowedLocation) {
        this.invokers = invokers;
        this.description = description;
        this.wikiUrl = wikiUrl;
        this.requiredPermission = requiredPermission;
        this.allowedLocation = allowedLocation;
    }

    public Command(String[] invokers, String description, String wikiUrl){
        this(invokers, description, wikiUrl, DEFAULT_PERMISSION, DEFAULT_LOCATION);
    }

    public Command(String[] invokers, String description, String wikiUrl, PermissionLevel requiredPermission){
        this(invokers, description, wikiUrl, requiredPermission, DEFAULT_LOCATION);
    }

    public Command(String[] invokers, String description, String wikiUrl, Location allowedLocation){
        this(invokers, description, wikiUrl, DEFAULT_PERMISSION, allowedLocation);
    }

    public String formatHelp(String commandPrefix){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_ALIASES && i < invokers.length; i++){
            sb.append("`");
            if(!(this instanceof NoPrefixCommand))
                sb.append(commandPrefix);
            sb.append(invokers[i]).append("`, ");
        }
        String invokerString = sb.toString();
        invokerString = invokerString.substring(0, invokerString.length() - 2);
        if(invokers.length > MAX_ALIASES) {
            int remainingAliases = invokers.length - MAX_ALIASES;
            invokerString += " +" + remainingAliases + " other " + (remainingAliases == 1 ? "alias" : "aliases");
        }

        if(hasWikiUrl())
            return MessageFormat.format(HELP_FORMAT, invokerString, description, getWikiUrl());
        else
            return MessageFormat.format(HELP_FORMAT_NO_WIKI, invokerString, description);
    }

    public boolean hasWikiUrl(){
        return wikiUrl != null;
    }

    public String getWikiUrl(){
        if(wikiUrl == null)
            return "N/A";

        if(wikiUrl.contains("wiki.skuddbot.xyz"))
            return "<" + wikiUrl + ">";

        return wikiUrl;
    }

    public abstract void run(Message message, String content);

    public enum Location {
        SERVER, DM, BOTH
    }

}
