package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Used to view and edit stats.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.0
 */
public class StatsCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);

    private static final String INVALID_ARGS = "Invalid argument usage:\n" +
            "View: `!stats <category/list> [mention/userId]`\n" +
            "Edit: `!stats <mention/userId> <stat> <add/remove/set> <amount>`";

    public StatsCommand(){
        super(new String[]{"stats"}, "View/edit stats from users with this command.");
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        if(args.length == 5)
            editValue(message, content);
        else if(args.length == 2 || args.length == 3)
            showStats(message, content);
        else
            MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
    }

    private void showStats(Message message, String content){
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getUserAuthor().orElse(null); assert author != null;
        User user = author;
        if(args[1].equalsIgnoreCase("list")){
            MessagesUtils.sendPlain(message.getChannel(), Stat.formatStats());
            return;
        }
        if(args.length == 3){
            if(!message.getMentionedUsers().isEmpty()){
                user = message.getMentionedUsers().get(0);
                if(!args[2].replace("<@!", "<@").equalsIgnoreCase(user.getMentionTag())) {
                    MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
                    return;
                }
            } else if (MiscUtils.isLong(args[2])){
                try {
                    user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[2])).get();
                } catch (InterruptedException | ExecutionException e) {
                    MessagesUtils.addReaction(message, Emoji.X, "User with ID " + args[2] + " not found.");
                    return;
                }
            } else {
                MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
                return;
            }
        }
        if(user.isBot()){
            MessagesUtils.addReaction(message, Emoji.X, "User is a bot.");
            return;
        }
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        if(user.getId() != author.getId() && su.getSettings().getBoolean(UserSetting.PROFILE_PRIVATE)){
            if(!pm.getUser(server.getId(), user.getId()).getPermissions().hasPermission(PermissionLevel.SERVER_ADMIN)){
                MessagesUtils.addReaction(message, Emoji.X, "This user has set their profile to private.");
                return;
            }
        }

        Stat.Category category;
        try{
            category = Stat.Category.valueOf(MiscUtils.enumify(args[1]));
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[1] + "` is not a category.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(category.getName() + " stats for: " + user.getDisplayName(server), null, user.getAvatar());
        eb.setTitle("__Server:__ " + server.getName());

        for(Stat stat : Stat.values()) {
            if(!stat.isShow()) continue;
            if (stat.getCategory() != category) continue;
            if (stat == Stat.EXPERIENCE) {
                eb.addInlineField("__" + stat.getName() + ":__", su.getStats().formatLevel());
            } else {
                eb.addInlineField("__" + stat.getName() + ":__", su.getStats().getString(stat) + " " + stat.getSuffix());
            }
        }

        message.getChannel().sendMessage(eb);
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
