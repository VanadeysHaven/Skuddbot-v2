package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.GlobalSetting;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.PermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.Iterator;

/**
 * Used for viewing info about users.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        super(new String[]{"userinfo", "uinfo"}, "View information about your own account or the specified account.", PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null);
        EmbedBuilder eb = new EmbedBuilder();
        SkuddUser su = null;
        User user = message.getUserAuthor().orElse(null); assert user != null;

        if(message.getMentionedUsers().size() > 0) {
            user = message.getMentionedUsers().get(0);
        } else if(content.split(" ").length >= 2){
            String idStr = content.split(" ")[1];
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
            Iterator<Role> roleIt = user.getRoles(server).iterator();
            while (roleIt.hasNext()) {
                sbRoles.append(", ").append(roleIt.next().getName());
            }
            eb.addField("__Server Roles:__", MiscUtils.stripEveryone(sbRoles.toString().substring(2).trim()));
        }

        eb.setColor(color);
        eb.setFooter("Skuddbot " + Main.getSkuddbot().getGlobalSettings().getString(GlobalSetting.VERSION));

        message.getChannel().sendMessage(eb);
    }
}
