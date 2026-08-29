package me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.text.MessageFormat;

/**
 * Hugs punches, whatever, this command can do it.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public abstract class ActionCommand extends Command {

    public ActionCommand(String[] invokers, String description) {
        super(invokers, description, "https://wiki.skuddbot.xyz/commands/action-commands");
    }

    @Override
    public void run(CommandRequest request) {
        Message message = request.getMessage();
        Guild server = request.getGuild();
        User user = request.getUser();
        User selectedUser;
        try {
            selectedUser = getRandomActiveUser(user, server);
        } catch (UnsupportedOperationException e) {
            MessagesUtils.addReaction(message, Emoji.X, "There are no available users! Try again later.");
            return;
        }

        SkuddUser su = pm.getUser(server.getIdLong(), selectedUser.getIdLong());
        boolean allowPing = su.getSettings().getBoolean(UserSetting.MENTION_ME);

        ActionProperties ap = getActionProperties(user.getIdLong());
        String actionString = ap.getActionString();
        boolean shouldCapitalize = ap.shouldCapitalize();
        actionString = MessageFormat.format(actionString, UserUtils.getDisplayName(server, user), allowPing ? selectedUser.getAsMention() : UserUtils.getDisplayName(server, selectedUser));

        if(shouldCapitalize)
            actionString = actionString.toUpperCase();

        MessagesUtils.sendPlain(message.getChannel(), actionString);
    }

    private User getRandomActiveUser(User user, Guild server){
        long id;
        SkuddServer ss = sm.getServer(server.getIdLong());
        do {
            id = ss.getRandomActiveUser();
        } while (id == user.getIdLong());

        return UserUtils.getInstance().getUser(id);
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
