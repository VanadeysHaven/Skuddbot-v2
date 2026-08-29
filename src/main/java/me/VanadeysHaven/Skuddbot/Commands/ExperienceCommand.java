package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Profiles.Users.PermissionManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/**
 * Used for viewing experience.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class ExperienceCommand extends Command {

    public ExperienceCommand() {
        super(new String[]{"experience", "xp"}, "View your and other's experience and level.", "https://wiki.skuddbot.xyz/systems/experience");
    }

    @Override
    public void run(CommandRequest request) {
        User user = request.getUser();
        Guild server = request.getGuild();
        SkuddUser su = pm.getUser(server.getIdLong(), user.getIdLong());
        PermissionManager authorPermissions = su.getPermissions();
        String[] args = request.getArgs();
        Message message = request.getMessage();

        if(args.length >= 2){
            if(!message.getMentions().getUsers().isEmpty()){
                user = message.getMentions().getUsers().get(0);
            } else if (MiscUtils.isLong(args[1])){
                user = UserUtils.getInstance().getUser(args[1]);
            }

            su = pm.getUser(server.getIdLong(), user.getIdLong());
        }

        if(user.getIdLong() != request.getSender().getIdLong() && su.getSettings().getBoolean(UserSetting.PROFILE_PRIVATE) && !authorPermissions.hasPermission(PermissionLevel.SERVER_ADMIN)){
            MessagesUtils.addReaction(message, Emoji.X, "This user has set their stats to private.");
            return;
        }

        MessagesUtils.sendPlain(message.getChannel(), "**" + UserUtils.getDisplayName(server, user) + " | " + su.getStats().formatLevelLong() + "**");
    }

}
