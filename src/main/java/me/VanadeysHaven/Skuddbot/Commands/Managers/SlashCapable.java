package me.VanadeysHaven.Skuddbot.Commands.Managers;

import java.util.Optional;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SlashCapable {

    Logger logger = LoggerFactory.getLogger(SlashCapable.class);

    default Optional<SlashCommandData> getCommandData() {
        String name = getInvokers()[0];
        if (!name.matches("^[-_\\p{L}\\p{N}]{1,32}$") || !name.equals(name.toLowerCase())) {
            logger.warn("Skipping slash registration for " + this + ": invoker '" + name + "' is not a valid slash name");
            return Optional.empty();
        }
        if (getDescription().length() < 1 || getDescription().length() > 100) {
            logger.warn("Skipping slash registration for " + this + ": description is not of right size.");
            return Optional.empty();
        }

        return Optional.of(Commands.slash(name, getDescription()));
    }

    String[] getInvokers();

    String getDescription();

}
