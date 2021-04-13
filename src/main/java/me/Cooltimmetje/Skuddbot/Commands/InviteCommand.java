package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import org.javacord.api.entity.message.Message;

/**
 * Command used to get the invite link for the bot.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.1
 */
public class InviteCommand extends Command {

    public InviteCommand() {
        super(new String[]{"invite"}, "Command used to get the invite link.", null, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        message.getChannel().sendMessage("You can use this link to invite the bot to your server: <https://discordapp.com/oauth2/authorize?client_id=209779487309692929&scope=bot&permissions=8>\n" +
                "You can use this link to join the Skuddbot Test server: <https://discord.gg/GmrwEka>");
    }
}
