package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Enums.UserStat;

import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles stats only.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class UserStatsSapling {

    private HashMap<UserStat,String> stats;
    public UserStatsSapling(){
        this.stats = new HashMap<>();
    }

    public void addStat(UserStat stat, String value){
        stats.put(stat, value);
    }

    public String getStat(UserStat stat){
        if (stats.containsKey(stat))
            return stats.get(stat);
        return null;
    }

    public UserStatsContainer grow(){
        return new UserStatsContainer(this);
    }

}
