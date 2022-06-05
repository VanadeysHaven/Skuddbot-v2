package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.PermissionManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.Constants;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;

/**
 * Used for viewing info about users.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        super(new String[]{"userinfo", "uinfo"}, "View information about your own account or the specified account.", "https://wiki.skuddbot.xyz/commands/user-info-command", PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        Server server = request.getServer();
        EmbedBuilder eb = new EmbedBuilder();
        SkuddUser su = null;
        User user = request.getUser();
        Message message = request.getMessage();
        String[] args = request.getArgs();

        if(message.getMentionedUsers().size() > 0) {
            user = message.getMentionedUsers().get(0);
        } else if(args.length >= 2){
            String idStr = args[1];
            if(MiscUtils.isLong(idStr)){
                User attemptUser = Main.getSkuddbot().getApi().getUserById(Long.parseLong(idStr)).join();
                if(attemptUser != null) {
                    user = attemptUser;
                }
            }
        }

        PermissionManager permManager = new PermissionManager(user.getId());
        if(server != null) {
            su = pm.getUser(server.getId(), user.getId());
            permManager = su.getPermissions();
        }

        eb.setAuthor(user.getDiscriminatedName(), null, user.getAvatar());
        eb.setThumbnail(user.getAvatar());
        String title = "";
        Color color = Color.GRAY;
        if (Constants.adminUsers.contains(user.getId())) {
            title = "Skuddbot Admin";
            color = Color.RED;
        } else if (dm.isDonator(user.getId())) {
            title = "Skuddbot Donator";
            color = Color.ORANGE;
        }
        if (!title.equals(""))
            eb.setTitle(title);

        eb.addField("__User ID:__", user.getId()+"");
        eb.addField("__Permissions:__", permManager.toString());

        if(server != null){
            eb.addInlineField("__Skuddbot ID:__", su.getId().getId()+"");
            String nick = user.getDisplayName(server);
            if(nick.equals(user.getName())){
                nick = "No nickname";
            }
            eb.addInlineField("__Server Nickname:__", nick);
            StringBuilder sbRoles = new StringBuilder();
            for (Role role : user.getRoles(server)) {
                sbRoles.append(", ").append(role.getName());
            }
            eb.addField("__Server Roles:__", MiscUtils.stripEveryone(sbRoles.substring(2).trim()));
        }

        eb.setColor(color);
        eb.setFooter("Skuddbot " + Main.getSkuddbot().getGlobalSettings().getString(GlobalSetting.VERSION));

        request.getChannel().sendMessage(eb);
    }
}
