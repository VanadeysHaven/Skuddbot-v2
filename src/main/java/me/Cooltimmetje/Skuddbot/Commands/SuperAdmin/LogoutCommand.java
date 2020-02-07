package me.Cooltimmetje.Skuddbot.Commands.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Logout command
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class LogoutCommand extends Command {

    public LogoutCommand(){
        super(new String[]{"logout"}, "Logs the bot out and saves all data.", PermissionLevel.TIMMY, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        if(!message.getMentionedUsers().isEmpty() && message.getMentionedUsers().get(0).getId() == Main.getSkuddbot().getApi().getApplicationInfo().join().getClientId()){
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "yes");
            Main.getSkuddbot().logout();
        } else {
            MessagesUtils.addReaction(message, Emoji.X, "no");
        }
    }
}
