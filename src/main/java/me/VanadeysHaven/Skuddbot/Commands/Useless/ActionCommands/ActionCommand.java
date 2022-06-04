package me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;

/**
 * Hugs punches, whatever, this command can do it.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public abstract class ActionCommand extends Command {

    public ActionCommand(String[] invokers, String description) {
        super(invokers, description, "https://wiki.skuddbot.xyz/commands/action-commands");
    }

    @Override
    public void run(CommandRequest request) {
        Message message = request.getMessage();
        Server server = request.getServer();
        User user = request.getUser();
        User selectedUser;
        try {
            selectedUser = getRandomActiveUser(user, server);
        } catch (UnsupportedOperationException e) {
            MessagesUtils.addReaction(message, Emoji.X, "There are no available users! Try again later.");
            return;
        }

        SkuddUser su = pm.getUser(server.getId(), selectedUser.getId());
        boolean allowPing = su.getSettings().getBoolean(UserSetting.MENTION_ME);

        ActionProperties ap = getActionProperties(user.getId());
        String actionString = ap.getActionString();
        boolean shouldCapitalize = ap.shouldCapitalize();
        actionString = MessageFormat.format(actionString, user.getDisplayName(server), allowPing ? selectedUser.getMentionTag() : selectedUser.getDisplayName(server));

        if(shouldCapitalize)
            actionString = actionString.toUpperCase();

        MessagesUtils.sendPlain(message.getChannel(), actionString);
    }

    private User getRandomActiveUser(User user, Server server){
        long id;
        SkuddServer ss = sm.getServer(server.getId());
        do {
            id = ss.getRandomActiveUser();
        } while (id == user.getId());

        return Main.getSkuddbot().getApi().getUserById(id).join();
    }

    protected abstract ActionProperties getActionProperties(long userId);

    public class ActionProperties {

        String actionString;
        boolean shouldCapitalize;

        public ActionProperties(String actionString, boolean shouldCapitalize){
            this.actionString = actionString;
            this.shouldCapitalize = shouldCapitalize;
        }

        public String getActionString(){
            return actionString;
        }

        public boolean shouldCapitalize(){
            return shouldCapitalize;
        }
    }

}
