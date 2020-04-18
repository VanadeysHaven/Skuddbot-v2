package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.PermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Used for viewing experience.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ExperienceCommand extends Command {

    private static final ProfileManager pm = new ProfileManager();

    public ExperienceCommand() {
        super(new String[]{"experience", "xp"}, "View your and other's experience and level.");
    }

    @Override
    public void run(Message message, String content) {
        User user = message.getAuthor().asUser().orElse(null); assert user != null;
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        PermissionManager authorPermissions = su.getPermissions();
        String[] args = content.split(" ");

        if(args.length >= 2){
            if(!message.getMentionedUsers().isEmpty()){
                user = message.getMentionedUsers().get(0);
            } else if (MiscUtils.isLong(args[1])){
                user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[1])).join();
            }

            su = pm.getUser(server.getId(), user.getId());
        }

        if(user.getId() != message.getAuthor().getId() && su.getSettings().getBoolean(UserSetting.PROFILE_PRIVATE) && !authorPermissions.hasPermission(PermissionLevel.SERVER_ADMIN)){
            MessagesUtils.addReaction(message, Emoji.X, "This user has set their stats to private.");
            return;
        }

        MessagesUtils.sendPlain(message.getChannel(), "**" + user.getDisplayName(server) + " | " + su.getStats().formatLevelLong() + "**");
    }

}
