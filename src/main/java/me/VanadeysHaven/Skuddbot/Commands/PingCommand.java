package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.MessageRequests.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;

/**
 * This class is a simple ping command.
 *
 * @author Tim (Vanadey's Haven)
 * @since 2.3.23
 * @version 2.4
 */
public class PingCommand extends Command {

    public PingCommand(){
        super(new String[] {"ping"}, "Ping command for testing bot responses.", null, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        if(dm.isDonator(request.getSender().getIdLong())){
            String text = dm.getUser(request.getSender().getIdLong()).getPingMessage();
            if(text != null){
                request.reply(Emoji.WHITE_CHECK_MARK, text);
                return;
            }
        }

        request.addReaction(Emoji.WHITE_CHECK_MARK, "PONG!");
    }

}
