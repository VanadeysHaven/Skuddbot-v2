package me.Cooltimmetje.Skuddbot.Commands;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Command for viewing and altering server settings.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2.1
 * @since ALPHA-2.0
 */
public class ServerSettingsCommand extends Command {

    private static final int OVERVIEW_EXPIRE_TIME = 10; //in minutes

    private static final ServerManager sm = ServerManager.getInstance();
    private static ArrayList<Overview> overviews = new ArrayList<>();

    public ServerSettingsCommand(){
        super(new String[]{"serversettings", "ssettings"}, "Change and view server settings.", PermissionLevel.SERVER_ADMIN, Location.SERVER);
    }

    @Override
    public void run(Message message, String content){
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
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
            User user = message.getUserAuthor().orElse(null); assert user != null;
            deactivateOverview(user);
            new Overview(message, user, server);
        } else if (args.length == 2){
            showDetails(message, ss, setting);
        } else if (args.length >= 3){
            alterSetting(message, ss, setting, newValue);
        }
    }

    private String showOverview(Server server, SkuddServer ss, int length, int offset){
        TableArrayGenerator tag = new TableArrayGenerator();
        tag.addRow(new TableRow("Setting", "Value"));

        ServerSetting[] arr = ServerSetting.grab(length, offset);
        for(ServerSetting setting : arr){
            tag.addRow(new TableRow(setting.toString(), ss.getSettings().getString(setting)));
        }
        String table = new TableDrawer(tag.generateArray()).drawTable();
        String commandPrefix = ss.getSettings().getString(ServerSetting.COMMAND_PREFIX);

        String sb = "Server settings for **" + server.getName() + "** | $page\n```\n" + table + "\n```\n" +
                "Type `" + commandPrefix + "serversettings <setting>` for more information about that setting." +
                "Type `" + commandPrefix + "serversettings <setting> <newValue>` to change it.\n" +
                "Use the " + Emoji.ARROW_LEFT.getUnicode() + " and " + Emoji.ARROW_RIGHT.getUnicode() + " reactions to navigate between pages. Click the " + Emoji.ARROWS_CC.getUnicode() + " reaction to refresh the current page.";

        return sb;
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

    private void deactivateOverview(User user){
        Iterator<Overview> it = overviews.iterator();
        while(it.hasNext()){
            Overview cur = it.next();
            if(cur.getUser().getId() == user.getId()) {
                cur.unregisterButtons(true);
                overviews.remove(cur);
            }
        }
    }

    public static void clearOverviews(){
        Iterator<Overview> it = overviews.iterator();
        while(it.hasNext()){
            Overview overview = it.next();

            long expireTimeMillis = OVERVIEW_EXPIRE_TIME * 60 * 1000;
            if(expireTimeMillis > overview.getTimeSinceInitiation())
                overview.unregisterButtons(true);

            overviews.remove(overview);
        }
    }

    private class Overview {

        private static final int PAGE_LENGTH = 5;

        private int page;
        private final int maxPage;
        private final TextChannel channel;
        private final Server server;
        private final long timeInitiated;
        @Getter private final User user;

        private Message message;
        private final List<ReactionButton> buttons;

        private Overview(Message message, User user, Server server){
            this.channel = message.getChannel();
            this.server = server;
            this.user = user;
            this.timeInitiated = System.currentTimeMillis();
            message.delete();
            page = 1;
            maxPage = ServerSetting.getPagesAmount(PAGE_LENGTH);
            buttons = new ArrayList<>();

            sendMessage();
            buttons.add(ReactionUtils.registerButton(this.message, Emoji.ARROW_LEFT, this::prevPage, user.getId()));
            buttons.add(ReactionUtils.registerButton(this.message, Emoji.ARROW_RIGHT, this::nextPage, user.getId()));
            buttons.add(ReactionUtils.registerButton(this.message, Emoji.ARROWS_CC, this::refresh, user.getId()));
            overviews.add(this);
        }

        private void sendMessage(){
            if(message == null)
                message = MessagesUtils.sendPlain(channel, getMessage());
            else
                message.edit(getMessage());
        }

        private String getMessage(){
            return showOverview(server, sm.getServer(server.getId()), PAGE_LENGTH, (page - 1) * maxPage).replace("$page", "`Page " + page + "/" + maxPage + "`");
        }

        private void nextPage(ReactionButtonClickedEvent event) {
            int newPage = page + 1;
            if(checkRange(newPage)) {
                page = newPage;
                sendMessage();
            }

            event.undoReaction();
        }

        private void refresh(ReactionButtonClickedEvent event){
            event.undoReaction();
            sendMessage();
        }

        private void prevPage(ReactionButtonClickedEvent event) {
            int newPage = page - 1;
            if(checkRange(newPage)) {
                page = newPage;
                sendMessage();
            }

            event.undoReaction();
        }

        private boolean checkRange(int newPage){
            return newPage >= 1 && newPage <= maxPage;
        }

        private void delete(ReactionButtonClickedEvent event) {
            unregisterButtons(false);
            message.delete();
        }

        private void unregisterButtons(boolean removeReactions){
            for(ReactionButton button : buttons)
                button.unregister();

            if(removeReactions)
                message.removeAllReactions();
        }

        private long getTimeSinceInitiation(){
            return System.currentTimeMillis() - timeInitiated;
        }
    }

}
