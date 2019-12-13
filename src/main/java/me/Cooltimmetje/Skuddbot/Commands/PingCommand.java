package me.Cooltimmetje.Skuddbot.Commands;

import org.javacord.api.entity.message.Message;

public class PingCommand extends Command {

    public PingCommand(){
        super("!ping");
    }

    @Override
    public void run(Message message) {
        message.getChannel().sendMessage("PONG!");
    }

}
