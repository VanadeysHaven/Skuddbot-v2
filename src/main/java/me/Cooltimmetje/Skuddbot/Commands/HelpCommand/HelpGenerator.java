package me.Cooltimmetje.Skuddbot.Commands.HelpCommand;

import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

/**
 * Interface for the CommandManager to prevent unwanted changes to it.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public interface HelpGenerator {

    String getHelp(Identifier id, int amount, int offset);

    String getHelp(long userId, long serverId, int amount, int offset);

    int getCommandAmount(Identifier id);

    int getCommandAmount(long userId, long serverId);

}
