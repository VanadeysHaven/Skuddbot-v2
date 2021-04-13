package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.text.MessageFormat;

/**
 * Command used for retrieving the Jackpot amount.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public class JackpotCommand extends Command {

    public JackpotCommand() {
        super(new String[]{"jackpot"}, "Shows the current Jackpot amount.", "https://wiki.skuddbot.xyz/systems/jackpot");
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        String serverName = server.getName();
        int jackpotAmount = sm.getServer(server.getId()).getSettings().getInt(ServerSetting.JACKPOT);
        MessagesUtils.sendEmoji(message.getChannel(), Emoji.MONEYBAG, MessageFormat.format("The current jackpot for **{0}** is **{1} Skuddbux**!", serverName, jackpotAmount));
    }

}
