package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;

/**
 * This class is a simple ping command.
 *
 * @author Tim (Vanadey's Haven)
 * @since 2.3.23
 * @version 2.0
 */
public class PingCommand extends Command {

    public PingCommand(){
        super(new String[] {"ping"}, "Ping command for testing bot responses.", null, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        if(dm.isDonator(request.getSender().getId())){
            String text = dm.getUser(request.getSender().getId()).getPingMessage();
            if(text != null){
                MessagesUtils.sendEmoji(request.getChannel(), Emoji.WHITE_CHECK_MARK, text);
                return;
            }
        }

        MessagesUtils.addReaction(request.getMessage(), Emoji.WHITE_CHECK_MARK, "PONG!");
    }

}
