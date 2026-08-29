package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.PermissionManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.Constants;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

/**
 * Used for viewing info about users.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        super(new String[]{"userinfo", "uinfo"}, "View information about your own account or the specified account.", "https://wiki.skuddbot.xyz/commands/user-info-command", PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        Guild server = request.getGuild();
        EmbedBuilder eb = new EmbedBuilder();
        SkuddUser su = null;
        User user = request.getUser();
        Message message = request.getMessage();
        String[] args = request.getArgs();

        if(message.getMentions().getUsers().size() > 0) {
            user = message.getMentions().getUsers().get(0);
        } else if(args.length >= 2){
            String idStr = args[1];
            if(MiscUtils.isLong(idStr) && UserUtils.getInstance().doesUserExist(idStr)){
                user = UserUtils.getInstance().getUser(idStr);
            }
        }

        PermissionManager permManager = new PermissionManager(user.getIdLong());
        if(server != null) {
            su = pm.getUser(server.getIdLong(), user.getIdLong());
            permManager = su.getPermissions();
        }

        eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
        eb.setThumbnail(user.getEffectiveAvatarUrl());
        String title = "";
        Color color = Color.GRAY;
        if (Constants.adminUsers.contains(user.getIdLong())) {
            title = "Skuddbot Admin";
            color = Color.RED;
        } else if (dm.isDonator(user.getIdLong())) {
            title = "Skuddbot Donator";
            color = Color.ORANGE;
        }
        if (!title.equals(""))
            eb.setTitle(title);

        eb.addField("__User ID:__", user.getId(), false);
        eb.addField("__Permissions:__", permManager.toString(), false);

        if(server != null){
            eb.addField("__Skuddbot ID:__", su.getId().getId()+"", true);
            String nick = UserUtils.getDisplayName(server, user);
            if(nick.equals(user.getName())){
                nick = "No nickname";
            }
            eb.addField("__Server Nickname:__", nick, true);
            Member member = server.getMember(user);
            if(member != null && !member.getRoles().isEmpty()){
                StringBuilder sbRoles = new StringBuilder();
                for (Role role : member.getRoles()) {
                    sbRoles.append(", ").append(role.getName());
                }
                eb.addField("__Server Roles:__", MiscUtils.stripEveryone(sbRoles.substring(2).trim()), false);
            }
        }

        eb.setColor(color);
        eb.setFooter("Skuddbot " + Main.getSkuddbot().getGlobalSettings().getString(GlobalSetting.VERSION));

        MessagesUtils.sendEmbed(request.getChannel(), eb);
    }
}
