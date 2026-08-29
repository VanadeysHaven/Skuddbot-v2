package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/**
 * Command for kicking a user from a server.
 *
 * @author Tim (Vanadey's Haven)
 * @version [not deployed]
 * @since [not deployed]
 */
public class KickCommand extends Command {

    public KickCommand(String[] invokers, String description, String wikiUrl, PermissionLevel requiredPermission, Location allowedLocation) {
        super(invokers, description, wikiUrl, requiredPermission, allowedLocation);
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getArgs(); //Get arguments in array
        Guild server = request.getGuild(); //Get server instance
        User author = request.getSender(); //Get the author
        Message message = request.getMessage(); //Get the message

        if(!message.getMentions().getUsers().isEmpty()) { //Check if there's a mentioned user
            MessagesUtils.addReaction(message, Emoji.X, "You need to specify a user."); //If not, display error
            return; //Stop
        }

        String[] reasonArr = new String[]{}; //New string array
        if (args.length - 2 >= 0) System.arraycopy(args, 2, reasonArr, 0, args.length - 2); //Strip the first 2 elements off the array.

        String reason = MiscUtils.glueStrings("", " ", " ", "", -1, "", reasonArr); //Glue the array together into a String

        if(reason.equalsIgnoreCase("")) //Check if there's a reason
            reason = "Kicked by " + author.getName() + " using Skuddbot."; //If not, display default kick message

        User toKick = message.getMentions().getUsers().get(0); //Get the user that needs to be kicked.
        server.kick(toKick).reason(reason).queue(); //Kick specified user with the reason.

        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Kicked user **" + toKick.getName() + "** with reason `" + reason + "`."); //Display success message.
    }

}
