package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.NoPrefixCommand;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * o7 CMDR
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class SaluteCommand extends NoPrefixCommand {

    public SaluteCommand() {
        super(new String[]{"o7"}, "o7 CMDR", PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null);
        if (server == null) {
            User user = message.getUserAuthor().orElse(null); assert user != null;
            MessagesUtils.sendPlain(user.getPrivateChannel().orElse(user.openPrivateChannel().join()), "o7");
            return;
        }

        ArrayList<KnownCustomEmoji> emoji = new ArrayList<>(server.getCustomEmojis());
        if(emoji.isEmpty()){
            MessagesUtils.sendPlain(message.getChannel(), "o7");
            return;
        }

        MessagesUtils.sendPlain(message.getChannel(), emoji.get(MiscUtils.randomInt(0, emoji.size() - 1)).getMentionTag() + "7");
    }

}
