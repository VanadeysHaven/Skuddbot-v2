package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Enums.UserStat;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.HashMap;

/**
 * This class holds the stats for users.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class UserStatsContainer {

    private HashMap<UserStat,String> stats;

    public UserStatsContainer(UserStatsSapling sapling){
        this.stats = new HashMap<>();
        processStatsSapling(sapling);
    }

    private void processStatsSapling(UserStatsSapling sapling){
        for(UserStat stat : UserStat.values()){
            String value = sapling.getStat(stat);
            if(value != null) {
                setStat(stat, value);
            } else {
                setStat(stat, stat.getDefaultValue());
            }
        }
    }

    public void setStat(UserStat stat, String value){
        if(!checkType(value, stat)) throw new IllegalArgumentException("Value " + value + " is unsuitable for stat " + stat + "; not of type " + stat.getType());
        this.stats.put(stat, value);
    }

    public void setStat(UserStat stat, int value){
        setStat(stat, value+"");
    }

    public String getString(UserStat stat){
        return this.stats.get(stat);
    }

    public int getInt(UserStat stat){
        if(stat.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        return Integer.parseInt(getString(stat));
    }

    public void incrementStat(UserStat stat){
        if(stat.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        incrementStat(stat, 1);
    }

    public void incrementStat(UserStat stat, int incrementBy) {
        if(stat.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        setStat(stat, getInt(stat) + incrementBy);
    }

    private boolean checkType(String input, UserStat stat){
        ValueType type = stat.getType();
        if(type == ValueType.INTEGER){
            return MiscUtils.isInt(input);
        }
        if(type == ValueType.JSON){
            //TODO
        }

        return true;
    }

    public String getFavouriteTeammate(){
        //TODO
        return "hi";
    }
}
