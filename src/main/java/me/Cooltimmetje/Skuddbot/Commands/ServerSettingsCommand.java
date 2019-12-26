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
        String[] args = message.getContent().split(" ");

        if(allowAccess){
            if(args.length == 1){
                showAll(message);
            } else if (args.length == 2){
                showDetails(message);
            } else if (args.length >= 3){
                alterSetting(message);
            }
        } else {
            MessagesUtils.addReaction(message, Emoji.X, "You do not have permission to do this.");
        }
    }

    private void showAll(Message message){
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddServer ss = sm.getServer(server.getId());
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

    private void showDetails(Message message){
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddServer ss = sm.getServer(server.getId());
        String enumSetting = message.getContent().split(" ")[1];
        ServerSetting setting = fromString(message.getContent().split(" ")[1]);
        if(setting == null) {
            MessagesUtils.addReaction(message, Emoji.X, "Setting " + enumSetting + " does not exist!");
            return;
        }

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

    private void alterSetting(Message message){
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddServer ss = sm.getServer(server.getId());
        String enumSetting = message.getContent().split(" ")[1];
        ServerSetting setting = fromString(message.getContent().split(" ")[1]);
        String newValue = message.getContent().split(" ")[2];
        if(setting == null) {
            MessagesUtils.addReaction(message, Emoji.X, "Setting " + enumSetting + " does not exist!");
            return;
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
