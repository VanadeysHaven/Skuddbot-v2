package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.ServerSettings.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

/**
 * Command for viewing and altering server settings.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ServerSettingsCommand extends Command {

    private ServerManager sm;

    public ServerSettingsCommand(){
        super(new String[]{"serversettings"}, "You can change and view server settings with this.");
        sm = new ServerManager();
    }

    @Override
    public void run(Message message){
        boolean allowAccess = true; //TODO: Implement permissions.
        if(!allowAccess) {
            MessagesUtils.addReaction(message, Emoji.X, "You do not have permission to do this.");
            return;
        }
        String[] args = message.getContent().split(" ");
        Server server = message.getServer().orElse(null);
        assert server != null;
        SkuddServer ss = sm.getServer(server.getId());
        ServerSetting setting = null;
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
            showAll(message, server, ss);
        } else if (args.length == 2){
            showDetails(message, server, ss, setting);
        } else if (args.length >= 3){
            alterSetting(message, server, ss, setting, newValue);
        }
    }

    private void showAll(Message message, Server server, SkuddServer ss){
        TableArrayGenerator tag = new TableArrayGenerator();
        tag.addRow(new TableRow("Setting", "Value"));
        for(ServerSetting setting : ServerSetting.values()){
            tag.addRow(new TableRow(setting.toString(), ss.getSettings().getString(setting)));
        }
        String table = new TableDrawer(tag.generateArray()).drawTable();

        String sb = "Server settings for " + server.getName() + "\n```\n" + table + "\n```\n" +
                "Type `!serversettings <setting>` for more information about that setting." +
                "Type `!serversettings <setting> <newValue>` to change it.";
        message.getChannel().sendMessage(sb);
    }

    private void showDetails(Message message, Server server, SkuddServer ss, ServerSetting setting){
        String sb = "```\n" +
                "Setting:       " + setting + "\n" +
                "Description:   " + setting.getDescription() + "\n" +
                "Type:          " + setting.getType() + "\n" +
                "Category:      " + setting.getCategory() + "\n" +
                "Default value: " + setting.getDefaultValue() + "\n" +
                "Current value: " + ss.getSettings().getString(setting) + "\n" +
                "```\n" + "To change the value type: `!serversettings " + setting + " <newValue>`";
        message.getChannel().sendMessage(sb);
    }

    private void alterSetting(Message message, Server server, SkuddServer ss, ServerSetting setting, String newValue){
        try {
            ss.getSettings().setString(setting, newValue);
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }
    }

    private ServerSetting fromString(String input){
        String enumSetting = input.split(" ")[1].toUpperCase().replace("-", "_");
        ServerSetting setting = null;
        try {
            setting = ServerSetting.valueOf(enumSetting);
        } catch (IllegalArgumentException ignored){
        }

        return setting;
    }
}
