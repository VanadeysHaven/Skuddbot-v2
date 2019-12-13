package me.Cooltimmetje.Skuddbot.Commands;

import lombok.Getter;
import org.javacord.api.entity.message.Message;

/**
 * This class is a command, can be extended to other classes to make for a flexible command system.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public abstract class Command {

    @Getter private String[] invokers;

    public Command(String[] invokers) {
        this.invokers = invokers;
    }

    public abstract void run(Message message);

}
