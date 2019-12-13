package me.Cooltimmetje.Skuddbot.Commands;

import org.javacord.api.entity.message.Message;

/**
 * This class is a simple ping command.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class PingCommand extends Command {

    public PingCommand(){
        super(new String[] {"ping"});
    }

    @Override
    public void run(Message message) {
        message.getChannel().sendMessage("PONG!");
    }

}
