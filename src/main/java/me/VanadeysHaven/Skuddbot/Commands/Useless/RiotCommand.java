package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;

/**
 * (╯°□°）╯︵ ┻━┻
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class RiotCommand extends Command {

    public RiotCommand(){
        super(new String[]{"riot"}, "(╯°□°）╯︵ ┻━┻", null);
    }

    @Override
    public void run(CommandRequest request) {
        MessagesUtils.sendPlain(request.getChannel(), "(╯°□°）╯︵ ┻━┻");
    }
}
