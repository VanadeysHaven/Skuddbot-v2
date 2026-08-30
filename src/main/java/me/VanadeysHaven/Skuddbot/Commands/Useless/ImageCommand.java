package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Donator.DonatorManager;
import me.VanadeysHaven.Skuddbot.Donator.DonatorMessage;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class for handling random image commands.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class ImageCommand extends Command {

    private DonatorManager dm = new DonatorManager();
    private DonatorMessage.Type type;

    public ImageCommand(DonatorMessage.Type type) {
        super(type.getCommands(), type.getCommandDescription(), "https://wiki.skuddbot.xyz/commands/image-commands", PermissionLevel.DEFAULT, Location.BOTH);
        this.type = type;
    }

    @Override
    public void run(CommandRequest request) {
        Message message = request.getMessage();
        MessageChannel channel = request.getChannel();

        boolean allowMultiImg;
        if(channel.getType() == ChannelType.PRIVATE)
            allowMultiImg = true;
        else {
            Guild server = request.getGuild();
            allowMultiImg = sm.getServer(server.getIdLong()).getSettings().getBoolean(ServerSetting.ALLOW_MULTI_IMG);
        }
        int imgAmount = 1;
        String[] args = request.getContent().split(" ");
        if(args.length > 1){
            if(MiscUtils.isInt(args[1]) && allowMultiImg){
                int askedAmount = Math.max(Integer.parseInt(args[1]), 1);
                imgAmount = Math.min(askedAmount, 5);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < imgAmount; i++)
            sb.append(type.getEmoji().getUnicode()).append(" ").append(dm.getMessage(type)).append("\n");

        MessagesUtils.sendPlain(message.getChannel(), sb.toString().trim());
    }
}
