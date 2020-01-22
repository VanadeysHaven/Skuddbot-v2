package me.Cooltimmetje.Skuddbot.Commands.Donator;

import com.vdurmont.emoji.EmojiParser;
import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Donator.DonatorUser;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Used for donators to set their ping message.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class SetPingCommand extends Command {

    public SetPingCommand(){
        super(new String[]{"setping"}, "Set your ping message with this command.", PermissionLevel.DONATOR, Location.DM);
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split( " ");
        if(args.length < 2){
            MessagesUtils.addReaction(message, Emoji.X, "You need to specify your ping message!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i=1; i<args.length; i++){
            sb.append(args[i]).append(" ");
        }
        String ping = EmojiParser.parseToAliases(sb.toString().trim());
        if(ping.equalsIgnoreCase("null")) ping = null;
        DonatorUser du = dm.getUser(message.getAuthor().getId());
        du.setPingMessage(ping);
        du.save();

        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Updated your ping message to `" + ping + "`!");
    }
}
