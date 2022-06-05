package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;

/**
 * Command for testing purposes.
 *
 * @author Tim (Vanadey's Haven)
 * @version [not deployed]
 * @since [not deployed]
 */
public final class TestCommand extends Command {

    public TestCommand() {
        super(new String[]{"test"}, "Testing 1 2 3", null, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        request.getChannel().sendMessage("Testing 1 2 3");
    }

}
