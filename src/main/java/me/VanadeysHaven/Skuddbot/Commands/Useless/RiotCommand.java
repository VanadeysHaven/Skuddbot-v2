package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * (╯°□°）╯︵ ┻━┻
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class RiotCommand extends Command {

    public RiotCommand(){
        super(new String[]{"riot"}, "(╯°□°）╯︵ ┻━┻", null);
    }

    @Override
    public void run(Message message, String content) {
        MessagesUtils.sendPlain(message.getChannel(), "(╯°□°）╯︵ ┻━┻");
    }
}
