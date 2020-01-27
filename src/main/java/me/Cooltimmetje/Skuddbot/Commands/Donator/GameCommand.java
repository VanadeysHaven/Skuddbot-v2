package me.Cooltimmetje.Skuddbot.Commands.Donator;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Command used to change the current playing status.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class GameCommand extends Command {

    public GameCommand() {
        super(new String[]{"game"}, "Used to change the current playing status.", PermissionLevel.DONATOR, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        if(content.split(" ").length < 2){
            MessagesUtils.addReaction(message, Emoji.X, "You need to specify what the bot needs to play!");
            return;
        }

        String game = content.substring(5);
        Main.getSkuddbot().getApi().updateActivity(game);
        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Game updated to: `" + game + "`!");
    }
}
