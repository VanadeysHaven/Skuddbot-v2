package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.server.Server;

import java.text.MessageFormat;

/**
 * Command used for retrieving the Jackpot amount.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.2.1
 */
public class JackpotCommand extends Command {

    public JackpotCommand() {
        super(new String[]{"jackpot"}, "Shows the current Jackpot amount.", "https://wiki.skuddbot.xyz/systems/jackpot");
    }

    @Override
    public void run(CommandRequest request) {
        Server server = request.getServer();
        String serverName = server.getName();
        int jackpotAmount = sm.getServer(server.getId()).getSettings().getInt(ServerSetting.JACKPOT);
        MessagesUtils.sendEmoji(request.getChannel(), Emoji.MONEYBAG, MessageFormat.format("The current jackpot for **{0}** is **{1} Skuddbux**!", serverName, jackpotAmount));
    }

}
