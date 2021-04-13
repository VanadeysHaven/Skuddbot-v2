package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

/**
 * Class for handling random image commands.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.03
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
    public void run(Message message, String content) {
        boolean allowMultiImg;
        if(message.getChannel().getType() == ChannelType.PRIVATE_CHANNEL)
            allowMultiImg = true;
        else {
            Server server = message.getServer().orElse(null); assert server != null;
            allowMultiImg = sm.getServer(server.getId()).getSettings().getBoolean(ServerSetting.ALLOW_MULTI_IMG);
        }
        int imgAmount = 1;
        String[] args = content.split(" ");
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
