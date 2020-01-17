package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;

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
public class GlobalPermissionManager {

    private static DonatorManager dm = new DonatorManager();

    private ArrayList<PermissionLevel> getPermissionsList(long id){
        ArrayList<PermissionLevel> list = new ArrayList<>();

        list.add(PermissionLevel.DEFAULT);
        if(dm.isDonator(id)) list.add(PermissionLevel.DONATOR);
        if(Constants.adminUsers.contains(id)) list.add(PermissionLevel.BOT_ADMIN);
        if(id == Constants.TIMMY_ID) list.add(PermissionLevel.TIMMY);

        return list;
    }

    public Iterator<PermissionLevel> getPermissions(long id){
        return getPermissionsList(id).iterator();
    }

    public boolean hasPermission(long id, PermissionLevel level){
        List<PermissionLevel> list = getPermissionsList(id);
        if(level == PermissionLevel.DONATOR){
            return list.contains(PermissionLevel.DONATOR);
        }
        if(list.contains(PermissionLevel.TIMMY)) return true;
        if(list.contains(PermissionLevel.BOT_ADMIN)) return level != PermissionLevel.TIMMY;
        return list.contains(level);
    }

}
