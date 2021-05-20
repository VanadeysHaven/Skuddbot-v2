package me.VanadeysHaven.Skuddbot.Profiles.Users.Stats;

import me.VanadeysHaven.Skuddbot.Profiles.DataContainers.UserDataContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Identifier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;

/**
 * This class holds the stats for users.
 *
 * @author Tim (Vanadey's Haven)
 * @since 2.3
 * @version 2.0
 */
public class StatsContainer extends UserDataContainer<Stat> {

    private int lastLevel;

    public StatsContainer(Identifier id, StatsSapling sapling){
        super(id);
        processStatsSapling(sapling);
        lastLevel = getLevelProgress()[0];
    }

    private void processStatsSapling(StatsSapling sapling){
        for(Stat stat : Stat.values()){
            String value = sapling.getStat(stat);
            if(value != null) {
                setString(stat, value, false, true);
            } else {
                setString(stat, stat.getDefaultValue(), false, true);
            }
        }
    }

    @Override
    public void setString(Stat field, String value, boolean save, boolean bypassCooldown) {
        if(save && !pm.getUser(getId()).getSettings().getBoolean(UserSetting.TRACK_ME)) return;

        super.setString(field, value, save, bypassCooldown);
    }

    public String getFavouriteTeammate(){
        //TODO
        return "hi";
    }

    public int[] getLevelProgress(){ //[level,progress,cur,lvlup]
        SkuddServer ss = sm.getServer(getId().getServerId());
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
