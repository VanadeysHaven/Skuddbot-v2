package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.PermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.ExecutionException;

/**
 * Used to view and edit stats.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class StatsCommand extends Command {

    private ProfileManager pm = new ProfileManager();

    public StatsCommand(){
        super(new String[]{"stats"}, "View stats from users with this command.");
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        MessageAuthor author = message.getAuthor(); assert author != null;
        User user = message.getAuthor().asUser().orElse(null); assert user != null;
        if(!message.getMentionedUsers().isEmpty()) {
            user = message.getMentionedUsers().get(0);
        }
        if(args.length >= 2 && MiscUtils.isLong(args[1])) {
            try {
                user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[1])).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        Server server = message.getServer().orElse(null); assert server != null;
        PermissionManager authorPermissions = pm.getUser(server.getId(), author.getId()).getPermissions();
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        if(args.length >= 5)
            if(authorPermissions.hasPermission(PermissionLevel.SERVER_ADMIN)) {
                editValue(message, content, su, user, server);
                return;
            }

        if(user.getId() != author.getId() && su.getSettings().getBoolean(UserSetting.PROFILE_PRIVATE) && !authorPermissions.hasPermission(PermissionLevel.SERVER_ADMIN)) {
            MessagesUtils.addReaction(message, Emoji.X, "This user has set their stats to private.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Stats for: " + user.getDisplayName(server), null, user.getAvatar());
        eb.setTitle("__Server:__ " + server.getName());

        for(Stat.Category category : Stat.Category.values()){
            if(category != Stat.Category.NO_CATEGORY) eb.addField("\u200B", "__" + category.getName() + ":__");
            for(Stat stat : Stat.values()) {
                if (stat.getCategory() != category) continue;
                if (stat == Stat.EXPERIENCE) {
                    eb.addInlineField("__" + stat.getName() + ":__", su.getStats().formatLevel());
                } else {
                    eb.addInlineField("__" + stat.getName() + ":__", su.getStats().getString(stat) + " " + stat.getSuffix());
                }
            }
        }

        message.getChannel().sendMessage(eb);
    }

    private void editValue(Message message, String content, SkuddUser su, User user, Server server) { // command name stat operation amount
        String[] args = content.split(" ");
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
        if(!MiscUtils.isInt(args[4])){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[4] + "` is not an number.");
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
            case "set":
                su.getStats().setInt(stat, mutationAmount);
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Set stat `" + stat + "` to `" + mutationAmount + "` for user `" + user.getDisplayName(server) + "`");
                break;
            default:
                MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not an valid operation.");
                break;
        }
    }
}
