package me.Cooltimmetje.Skuddbot.Profiles.Users.Stats;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Enums.Stat;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class holds the stats for users.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class StatsContainer {

    private static final ServerManager sm = new ServerManager();
    private static final ProfileManager pm = new ProfileManager();

    private Identifier id;
    private HashMap<Stat,String> stats;
    private int lastLevel;

    public StatsContainer(Identifier id, StatsSapling sapling){
        this.id = id;
        this.stats = new HashMap<>();
        processStatsSapling(sapling);
        lastLevel = getLevelProgress()[0];
    }

    private void processStatsSapling(StatsSapling sapling){
        for(Stat stat : Stat.values()){
            String value = sapling.getStat(stat);
            if(value != null) {
                setString(stat, value, true);
            } else {
                setString(stat, stat.getDefaultValue(), true);
            }
        }
    }

    public void setString(Stat stat, String value){
        setString(stat, value, false);
    }

    public void setString(Stat stat, String value, boolean load){
        if(!checkType(value, stat)) throw new IllegalArgumentException("Value " + value + " is unsuitable for stat " + stat + "; not of type " + stat.getType());
        if(!load && !pm.getUser(id).getSettings().getBoolean(UserSetting.TRACK_ME)) return;
        this.stats.put(stat, value);
        if(load) save(stat);
    }

    public String getString(Stat stat){
        return this.stats.get(stat);
    }

    public void setInt(Stat stat, int value){
        setString(stat, value+"");
    }

    public int getInt(Stat stat){
        if(stat.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        return Integer.parseInt(getString(stat));
    }

    public void incrementInt(Stat stat){
        if(stat.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        incrementInt(stat, 1);
    }

    public void incrementInt(Stat stat, int incrementBy) {
        if(stat.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        setInt(stat, getInt(stat) + incrementBy);
    }

    public String getFavouriteTeammate(){
        //TODO
        return "hi";
    }

    private void save(Stat stat){
        QueryExecutor qe = null;
        if (getString(stat).equals(stat.getDefaultValue())) {
            try {
                qe = new QueryExecutor(Query.DELETE_STAT_VALUE).setInt(1, id.getId()).setString(2, stat.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (qe != null) qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(Query.UPDATE_STAT_VALUE).setString(1, stat.getDbReference()).setInt(2, id.getId()).setString(3, getString(stat)).and(4);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (qe != null) qe.close();
            }
        }
    }

    public void save(){
        for(Stat stat : Stat.values()){
            save(stat);
        }
    }

    private boolean checkType(String input, Stat stat){
        ValueType type = stat.getType();
        if(type == ValueType.INTEGER){
            return MiscUtils.isInt(input);
        }
        if(type == ValueType.JSON){
            //TODO
            return input.equals("{}");
        }

        return type == ValueType.STRING;
    }

    public int[] getLevelProgress(){ //[level,progress,cur,lvlup]
        SkuddServer ss = sm.getServer(id.getServerId());
        int playerExperience = getInt(Stat.EXPERIENCE);
        int level = 1;
        int base = ss.getSettings().getInt(ServerSetting.XP_BASE);
        double multiplier = ss.getSettings().getDouble(ServerSetting.XP_MULTIPLIER);

        int lvlUpReq = base;
        while (playerExperience >= lvlUpReq) {
            playerExperience -= lvlUpReq;
            level++;
            lvlUpReq = (int) (base * Math.pow(multiplier, level - 1));
        }
        int progress = (int) ((double) playerExperience / (double) lvlUpReq * 100);

        return new int[]{level, progress, playerExperience, lvlUpReq};
    }

    public boolean hasLeveledUp(){
        int[] lvl = getLevelProgress();
        if(lvl[0] != lastLevel){
            lastLevel = lvl[0];
            return true;
        }

        return false;
    }

    public String formatLevel(){
        int[] lvl = getLevelProgress();
        return "Level " + lvl[0] + " (" + lvl[1] + "%)";
    }

    public String formatLevelLong(){
        int[] lvl = getLevelProgress();
        return "Level " + lvl[0] + " | Level progress: " + lvl[2] + "/" + lvl[3] + " (" + lvl[1] + "%) | Total experience: " + getInt(Stat.EXPERIENCE) + " XP";
    }
}
