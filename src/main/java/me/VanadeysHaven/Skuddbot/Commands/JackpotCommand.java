package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.entities.Guild;

import java.text.MessageFormat;

/**
 * Command used for retrieving the Jackpot amount.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.2.1
 */
public class JackpotCommand extends Command {

    public JackpotCommand() {
        super(new String[]{"jackpot"}, "Shows the current Jackpot amount.", "https://wiki.skuddbot.xyz/systems/jackpot");
    }

    @Override
    public void run(CommandRequest request) {
        Guild server = request.getGuild();
        String serverName = server.getName();
        int jackpotAmount = sm.getServer(server.getIdLong()).getSettings().getInt(ServerSetting.JACKPOT);
        MessagesUtils.sendEmoji(request.getChannel(), Emoji.MONEYBAG, MessageFormat.format("The current jackpot for **{0}** is **{1} Skuddbux**!", serverName, jackpotAmount));
    }

}
