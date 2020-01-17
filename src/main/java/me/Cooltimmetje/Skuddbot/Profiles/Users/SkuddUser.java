package me.Cooltimmetje.Skuddbot.Profiles.Users;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalPermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsSapling;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a user and their data and statistics.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class SkuddUser {

    private static DonatorManager dm = new DonatorManager();
    private static ServerManager sm = new ServerManager();
    private static GlobalPermissionManager gpm = new GlobalPermissionManager();

    @Getter private Identifier id;
    @Getter private StatsContainer stats;
    @Getter private UserSettingsContainer settings;
    @Getter @Setter boolean active;


    public SkuddUser(Identifier id, StatsSapling stats, UserSettingsSapling settings){
        this.id = id;
        this.stats = stats.grow();
        this.settings = settings.grow();
        this.active = true;
    }

    private ArrayList<PermissionLevel> getPermissionsList(){
        ArrayList<PermissionLevel> list = new ArrayList<>();
        Iterator<PermissionLevel> global = gpm.getPermissions(id.getDiscordId());
        while(global.hasNext()){
            list.add(global.next());
        }
        User user = Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join();
        Server server = Main.getSkuddbot().getApi().getServerById(id.getServerId()).orElse(null);
        SkuddServer ss = sm.getServer(id.getServerId());
        assert server != null;

        List<Role> roles = server.getRolesByName(ss.getSettings().getString(ServerSetting.ADMIN_ROLE));
        if(roles.size() > 0){
            Role reqRole = roles.get(0);
            if(user.getRoles(server).contains(reqRole)) list.add(PermissionLevel.SERVER_ADMIN);
        }

        return list;
    }

    public Iterator<PermissionLevel> getPermissionLevels() {
        return getPermissionsList().iterator();
    }

    public boolean hasPermissionLevel(PermissionLevel level){
        boolean global = gpm.hasPermission(id.getDiscordId(), level);
        if(global) return true;

        List<PermissionLevel> list = getPermissionsList();
        return getPermissionsList().contains(level);
    }

}
