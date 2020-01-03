package me.Cooltimmetje.Skuddbot.Profiles.Users.Stats;

import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Query;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStat;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles stats only.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class UserStatsSapling {

    private Identifier id;
    private HashMap<UserStat,String> stats;

    public UserStatsSapling(Identifier id){
        this.id = id;
        this.stats = new HashMap<>();

        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_STATS).setInt(1, id.getId());
            ResultSet rs = qe.executeQuery();
            while(rs.next()){
                addStat(UserStat.getByDbReference(rs.getString("stat_name")), rs.getString("stat_value"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
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
        return new UserStatsContainer(id, this);
    }

}
