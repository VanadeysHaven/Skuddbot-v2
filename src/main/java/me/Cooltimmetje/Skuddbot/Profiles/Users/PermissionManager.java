package me.Cooltimmetje.Skuddbot.Profiles.Users;

import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that used to check the global permissions of an user.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class PermissionManager {

    private static DonatorManager dm = new DonatorManager();
    private static ServerManager sm = new ServerManager();

    private long userId;
    private long serverId;

    public PermissionManager(long userId, long serverId){
        this.userId = userId;
        this.serverId = serverId;
    }

    public PermissionManager(long userId){
        this.userId = userId;
        serverId = -1;
    }

    public PermissionManager(Identifier id){
        userId = id.getDiscordId();
        serverId = id.getServerId();
    }

    private ArrayList<PermissionLevel> getPermissionsList(){
        ArrayList<PermissionLevel> list = new ArrayList<>();

        list.add(PermissionLevel.DEFAULT);
        if(dm.isDonator(userId)) list.add(PermissionLevel.DONATOR);
        if(Constants.adminUsers.contains(userId)) list.add(PermissionLevel.BOT_ADMIN);
        if(userId == Constants.TIMMY_ID) list.add(PermissionLevel.TIMMY);

        if(serverId != -1) {
            User user = Main.getSkuddbot().getApi().getUserById(userId).join();
            Server server = Main.getSkuddbot().getApi().getServerById(serverId).orElse(null);
            SkuddServer ss = sm.getServer(serverId);
            assert server != null;

            List<Role> roles = server.getRolesByName(ss.getSettings().getString(ServerSetting.ADMIN_ROLE));
            if (roles.size() > 0) {
                Role reqRole = roles.get(0);
                if (user.getRoles(server).contains(reqRole)) list.add(PermissionLevel.SERVER_ADMIN);
            }
        }

        return list;
    }

    public Iterator<PermissionLevel> getPermissions(){
        return getPermissionsList().iterator();
    }

    public boolean hasPermission(PermissionLevel level){
        List<PermissionLevel> list = getPermissionsList();
        if(level == PermissionLevel.DONATOR){
            return list.contains(PermissionLevel.DONATOR);
        }
        if(list.contains(PermissionLevel.TIMMY)) return true;
        if(list.contains(PermissionLevel.BOT_ADMIN)) return level != PermissionLevel.TIMMY;
        return list.contains(level);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<PermissionLevel> permIt = getPermissions();
        while(permIt.hasNext()){
            sb.append(", ").append(permIt.next());
        }

        return sb.toString().substring(2).trim();
    }
}
