package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PagedEmbed;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Command for viewing and altering server settings.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.0
 */
public class ServerSettingsCommand extends Command {

    private static final ServerManager sm = ServerManager.getInstance();

    public ServerSettingsCommand(){
        super(new String[]{"serversettings", "ssettings"}, "Change and view server settings.", "https://wiki.skuddbot.xyz/features/server-settings#command", PermissionLevel.SERVER_ADMIN, Location.SERVER);
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getArgs();
        Server server = request.getServer();
        SkuddServer ss = sm.getServer(server.getId());
        ServerSetting setting = null;
        Message message = request.getMessage();
        String newValue = "";
        if(args.length >= 2) {
            setting = fromString(args[1]);
            if (setting == null) {
                MessagesUtils.addReaction(message, Emoji.X, "Setting " + args[1] + " does not exist!");
                return;
            }

            if(args.length >= 3){
                if(!setting.isAllowSpaces()){
                    newValue = args[2];
                } else {
                    StringBuilder sb = new StringBuilder();
                    for(int i = 2; i < args.length; i++){
                        sb.append(args[i]).append(" ");
                    }

                    newValue = sb.toString().trim();
                }
            }
        }

        if(args.length == 1){
            User user = message.getUserAuthor().orElse(null); assert user != null;
            new PagedEmbed(ServerSetting.getPageManager(), message.getChannel(), null, ss, user.getId());
        } else if (args.length == 2){
            showDetails(message, ss, setting);
        } else if (args.length >= 3){
            alterSetting(message, ss, setting, newValue);
        }
    }

    private void showDetails(Message message, SkuddServer ss, ServerSetting setting){
        String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX);
        String sb = "```\n" +
                "Setting:       " + setting + "\n" +
                "Description:   " + setting.getDescription() + "\n" +
                "Type:          " + setting.getType() + "\n" +
                "Category:      " + setting.getCategory() + "\n" +
                "Default value: " + setting.getDefaultValue() + "\n" +
                "Current value: " + ss.getSettings().getString(setting) + "\n" +
                "```\n" + "To change the value type: `" + commandPrefix + "serversettings " + setting + " <newValue>`";
        MessagesUtils.sendPlain(message.getChannel(), sb);
    }

    private void alterSetting(Message message, SkuddServer ss, ServerSetting setting, String newValue){
        try {
            ss.getSettings().setString(setting, newValue);
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Successfully updated setting `" + setting + "` to `" + newValue + "`!");
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }
    }

    private ServerSetting fromString(String input){
        String enumSetting = input.toUpperCase().replace("-", "_");
        ServerSetting setting = null;
        try {
            setting = ServerSetting.valueOf(enumSetting);
        } catch (IllegalArgumentException ignored){
        }

        return setting;
    }

}
