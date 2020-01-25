package me.Cooltimmetje.Skuddbot.Commands.Useless.ActionCommands;

import me.Cooltimmetje.Skuddbot.Commands.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;

/**
 * Hugs punches, whatever, this command can do it.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public abstract class ActionCommand extends Command {

    private String actionString;

    public ActionCommand(String[] invokers, String description, String actionString) {
        super(invokers, description, Location.BOTH);
        this.actionString = actionString;
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        User user = message.getUserAuthor().orElse(null); assert user != null;
        User selectedUser;
        try {
            selectedUser = getRandomActiveUser(user, server);
        } catch (UnsupportedOperationException e) {
            MessagesUtils.addReaction(message, Emoji.X, "There are no available users! Try again later.");
            return;
        }

        SkuddUser su = pm.getUser(server.getId(), selectedUser.getId());
        boolean allowPing = su.getSettings().getBoolean(UserSetting.MENTION_ME);

        MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(actionString, allowPing ? selectedUser.getMentionTag() : selectedUser.getDisplayName(server)));
    }

    protected User getRandomActiveUser(User user, Server server){
        long id;
        SkuddServer ss = sm.getServer(server.getId());
        do {
            id = ss.getRandomActiveUser();
        } while (id == user.getId());

        return Main.getSkuddbot().getApi().getUserById(id).join();
    }

}