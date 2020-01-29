package me.Cooltimmetje.Skuddbot.Commands.SuperAdmin;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.GlobalSetting;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import me.Cooltimmetje.Skuddbot.Utilities.AppearanceManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import org.javacord.api.entity.message.Message;

/**
 * Command for editing global settings.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class GlobalSettingsCommand extends Command {

    public GlobalSettingsCommand() {
        super(new String[]{"globalsettings", "gsettings"}, "Viewing and changing global settings.", PermissionLevel.BOT_ADMIN, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        GlobalSetting setting = null;
        String newValue = "";
        if(args.length >= 2) {
            setting = fromString(args[1]);
            if(setting == null){
                MessagesUtils.addReaction(message, Emoji.X, "Setting " + args[1] + " does not exist.");
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

        if(args.length == 1) {
            showAll(message);
        } else if(args.length == 2){
            showDetails(message, setting);
        } else if(args.length >= 3){
            alterSetting(message, setting, newValue);
        }
    }

    private void showAll(Message message){
        GlobalSettingsContainer settings = Main.getSkuddbot().getGlobalSettings();
        TableArrayGenerator tag = new TableArrayGenerator(new TableRow("Setting", "Value"));
        for(GlobalSetting setting : GlobalSetting.values())
            tag.addRow(new TableRow(setting.toString(), settings.getString(setting)));
        String table = new TableDrawer(tag.generateArray()).drawTable();

        String msg = "Global settings:\n```\n" + table + "\n```\n";

        MessagesUtils.sendPlain(message.getChannel(), msg);
    }

    private void showDetails(Message message, GlobalSetting setting){
        GlobalSettingsContainer settings = Main.getSkuddbot().getGlobalSettings();
        String msg = "```\n" +
                "Setting: " + setting + "\n" +
                "Name: " + setting.getName() + "\n" +
                "Type: " + setting.getType() + "\n" +
                "Default value: " + setting.getDefaultValue() + "\n" +
                "Current value: " + settings.getString(setting) + "\n```";

        MessagesUtils.sendPlain(message.getChannel(), msg);
    }

    private void alterSetting(Message message, GlobalSetting setting, String newValue){
        GlobalSettingsContainer settings = Main.getSkuddbot().getGlobalSettings();
        try {
            if(setting == GlobalSetting.CURRENT_AVATAR){
                new AppearanceManager().setAvatar(AppearanceManager.Avatar.valueOf(newValue.toUpperCase().replace("_", "-")));
            } else {
                settings.setString(setting, newValue);
            }
            MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Successfully updated setting `" + setting + "` to `" + newValue + "`!");
        } catch (IllegalArgumentException e) {
            MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
        }
    }

    private GlobalSetting fromString(String input){
        String enumSetting = input.toUpperCase().replace("-", "_");
        GlobalSetting setting = null;
        try {
            setting = GlobalSetting.valueOf(enumSetting);
        } catch (IllegalArgumentException ignored) {
        }

        return setting;
    }
}
