package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Enums.UserStat;

import java.util.HashMap;

/**
 * This class holds the stats for users.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class StatsContainer {

    private HashMap<UserStat,String> stats;

    public StatsContainer(StatsBean bean) {
        this.stats = new HashMap<>();
        processBean(bean);
    }

    private void processBean(StatsBean bean){

    }
}
