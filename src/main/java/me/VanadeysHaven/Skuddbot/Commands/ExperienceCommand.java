package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Users.PermissionManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Used for viewing experience.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class ExperienceCommand extends Command {

    public ExperienceCommand() {
        super(new String[]{"experience", "xp"}, "View your and other's experience and level.", "https://wiki.skuddbot.xyz/systems/experience");
    }

    @Override
    public void run(CommandRequest request) {
        User user = request.getUser();
        Server server = request.getServer();
        SkuddUser su = pm.getUser(server.getId(), user.getId());
        PermissionManager authorPermissions = su.getPermissions();
        String[] args = request.getArgs();
        Message message = request.getMessage();

        if(args.length >= 2){
            if(!message.getMentionedUsers().isEmpty()){
                user = message.getMentionedUsers().get(0);
            } else if (MiscUtils.isLong(args[1])){
                user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[1])).join();
            }

            su = pm.getUser(server.getId(), user.getId());
        }

        if(user.getId() != request.getSender().getId() && su.getSettings().getBoolean(UserSetting.PROFILE_PRIVATE) && !authorPermissions.hasPermission(PermissionLevel.SERVER_ADMIN)){
            MessagesUtils.addReaction(message, Emoji.X, "This user has set their stats to private.");
            return;
        }

        MessagesUtils.sendPlain(message.getChannel(), "**" + user.getDisplayName(server) + " | " + su.getStats().formatLevelLong() + "**");
    }

}
