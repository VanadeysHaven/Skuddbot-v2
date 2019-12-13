package me.Cooltimmetje.Skuddbot.Commands;

import lombok.Getter;
import org.javacord.api.entity.message.Message;

public abstract class Command {

    @Getter private String[] invokers;

    public Command(String[] invokers) {
        this.invokers = invokers;
    }

    public abstract void run(Message message);

}
