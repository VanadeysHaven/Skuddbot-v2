package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * (╯°□°）╯︵ ┻━┻
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class RiotCommand extends Command {

    public RiotCommand(){
        super(new String[]{"riot"}, "(╯°□°）╯︵ ┻━┻");
    }

    @Override
    public void run(Message message, String content) {
        MessagesUtils.sendPlain(message.getChannel(), "(╯°□°）╯︵ ┻━┻");
    }
}
