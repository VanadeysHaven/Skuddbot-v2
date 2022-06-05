package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;

/**
 * We all sometimes need a little panic in our lives.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class PanicCommand extends Command {

    public PanicCommand() {
        super(new String[]{"panic"}, "PANIC!! SET THE PLACE ON FIRE!!", null);
    }

    @Override
    public void run(CommandRequest request) {
        MessagesUtils.sendPlain(request.getChannel(), "EVERYONE PANIC!!!");
    }

}
