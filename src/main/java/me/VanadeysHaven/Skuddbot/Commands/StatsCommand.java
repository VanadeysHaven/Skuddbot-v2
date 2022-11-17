package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Enums.ValueType;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PagedEmbed;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.Stat;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Used to view and edit stats.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.0
 */
public final class StatsCommand extends Command {

    private static final int OVERVIEW_EXPIRE_TIME = 10; //in minutes

    private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);
    private static final UserUtils uu = UserUtils.getInstance();

    private static final String INVALID_ARGS = "Invalid argument usage:\n" +
            "View: `!stats [mention/userId]`\n" +
            "Edit: `!stats <mention/userId> <stat> <add/remove/set> <amount>`";

    public StatsCommand(){
        super(new String[]{"stats"}, "View/edit stats of users with this command.", "https://wiki.skuddbot.xyz/features/stats#view-and-edit");
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        if(args.length >= 5)
            editValue(message, content);
        else if(args.length >= 1)
            showStats(message, content);
        else
            MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
    }

    private void showStats(Message message, String content){
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getAuthor().asUser().orElse(null); assert author != null;

        User target = message.getAuthor().asUser().orElse(null); assert target != null;
        if(args.length > 1) {
            if(!message.getMentionedUsers().isEmpty()){
                target = message.getMentionedUsers().get(0);
            } else if (MiscUtils.isLong(args[1])) {
                if(uu.doesUserExist(args[1])) {
                    target = uu.getUser(args[1]);
                } else {
                    MessagesUtils.addReaction(message, Emoji.X, "The user with ID `" + args[1] + "` doesn't exist.");
                    return;
                }
            } else {
                MessagesUtils.addReaction(message, Emoji.X, "The string `" + args[1] + "` cannot be parsed to a user. - Please provide a mention or ID.");
                return;
            }
        }

        new PagedEmbed(Stat.getPageManager(), message.getChannel(), ProfileManager.getInstance().getUser(server.getId(), target.getId()),
                ServerManager.getInstance().getServer(server.getId()), author.getId()); //create new embed to show the stats
    }

    private void editValue(Message message, String content) { // command name stat operation amount
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getUserAuthor().orElse(null); assert author != null;
        if(!pm.getUser(server.getId(), author.getId()).getPermissions().hasPermission(PermissionLevel.SERVER_ADMIN)){
            MessagesUtils.addReaction(message, Emoji.X, "You are missing a required permission to do this; " + PermissionLevel.SERVER_ADMIN);
            return;
        }

        User user;
        if(!message.getMentionedUsers().isEmpty()){
            user = message.getMentionedUsers().get(0);
            if(!args[1].replace("<@!", "<@").equalsIgnoreCase(user.getMentionTag())) {
                MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
                return;
            }
        } else if (MiscUtils.isLong(args[1])){
            try {
                user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[1])).get();
            } catch (InterruptedException | ExecutionException e) {
                MessagesUtils.addReaction(message, Emoji.X, "User with ID " + args[1] + " not found.");
                return;
            }
        } else {
            MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
            return;
        }
        if(user.isBot()){
            MessagesUtils.addReaction(message, Emoji.X, "User is a bot.");
            return;
        }

        Stat stat;
        try {
            stat = Stat.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException ignored){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[2] + "` is not a stat.");
            return;
        }
        if(!stat.isCanBeEdited()) {
            MessagesUtils.addReaction(message, Emoji.X, "`" + stat + "` cannot be edited.");
            return;
        }

        SkuddUser su = pm.getUser(server.getId(), user.getId());
        if(args[3].equalsIgnoreCase("set")){
            try {
                su.getStats().setString(stat, args[4]);
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Set stat `" + stat + "` to `" + args[4] + "` for user `" + user.getDisplayName(server) + "`");
                return;
            } catch (IllegalArgumentException e){
                MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
            }
        }


        if(stat.getType() == ValueType.INTEGER) {
            if(!MiscUtils.isInt(args[4])){
                MessagesUtils.addReaction(message, Emoji.X, "`" + args[4] + "` is not an valid number for stat `" + stat + "`");
                return;
            }
            int mutationAmount = Integer.parseInt(args[4]);

            switch (args[3].toLowerCase()) {
                case "add":
                    su.getStats().incrementInt(stat, mutationAmount);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + mutationAmount + "` to stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                case "remove":
                    su.getStats().incrementInt(stat, mutationAmount * -1);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Removed `" + mutationAmount + "` from stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                default:
                    MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not an valid operation.");
                    break;
            }
        } else if (stat.getType() == ValueType.LONG){
            if(!MiscUtils.isLong(args[4])){
                MessagesUtils.addReaction(message, Emoji.X, "`" + args[4] + "` is not an number.");
                return;
            }
            long mutationAmount = Long.parseLong(args[4]);

            switch (args[3].toLowerCase()) {
                case "add":
                    su.getStats().incrementLong(stat, mutationAmount);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + mutationAmount + "` to stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                case "remove":
                    su.getStats().incrementLong(stat, mutationAmount * -1);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Removed `" + mutationAmount + "` from stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                default:
                    MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not an valid operation.");
                    break;
            }
        } else {
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not a valid operation for type " + stat.getType());
        }
    }
}
