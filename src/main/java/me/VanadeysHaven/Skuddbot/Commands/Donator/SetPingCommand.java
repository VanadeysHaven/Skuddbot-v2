package me.VanadeysHaven.Skuddbot.Commands.Donator;

import com.vdurmont.emoji.EmojiParser;
import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Donator.DonatorUser;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;

/**
 * Used for donators to set their ping message.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class SetPingCommand extends Command {

    public SetPingCommand(){super(new String[]{"setping"}, "Set your ping message with this command.", null, PermissionLevel.DONATOR, Location.DM);
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getContent().split( " ");
        if(args.length < 2){
            request.replyError("You need to specify your ping message!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i=1; i<args.length; i++){
            sb.append(args[i]).append(" ");
        }
        String ping = EmojiParser.parseToAliases(sb.toString().trim());
        if(ping.equalsIgnoreCase("null")) ping = null;
        DonatorUser du = dm.getUser(request.getMessage().getAuthor().getIdLong());
        du.setPingMessage(ping);
        du.save();

        request.addReaction(Emoji.WHITE_CHECK_MARK, "Updated your ping message to `" + ping + "`!");
    }
}
