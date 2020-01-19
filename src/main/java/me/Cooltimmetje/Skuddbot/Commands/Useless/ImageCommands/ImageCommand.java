package me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;

/**
 * Class for handling random image commands.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public abstract class ImageCommand extends Command {

    private DonatorManager dm = new DonatorManager();
    private DonatorMessage.Type type;

    public ImageCommand(String[] invokers, String description, DonatorMessage.Type type) {
        super(invokers, description, PermissionLevel.DEFAULT, Location.BOTH);
        this.type = type;
    }

    @Override
    public void run(Message message, String content) {
        MessagesUtils.sendPlain(message.getChannel(), type.getEmoji().getUnicode() + " " + dm.getMessage(type));
    }
}
