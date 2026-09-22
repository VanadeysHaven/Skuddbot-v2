package me.VanadeysHaven.Skuddbot.Commands.Donator;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.MessageRequests.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import net.dv8tion.jda.api.entities.Activity;

/**
 * Command used to change the current playing status.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class GameCommand extends Command {

    public GameCommand() {
        super(new String[]{"game"}, "Used to change the current playing status.", null, PermissionLevel.DONATOR, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        if(request.getContent().split(" ").length < 2){
            request.replyError("You need to specify what the bot needs to play!");
            return;
        }

        String game = request.getContent().substring(5);
        Main.getSkuddbot().getApi().getPresence().setActivity(Activity.playing(game));
        request.addReaction(Emoji.WHITE_CHECK_MARK, "Game updated to: `" + game + "`!");
    }
}
