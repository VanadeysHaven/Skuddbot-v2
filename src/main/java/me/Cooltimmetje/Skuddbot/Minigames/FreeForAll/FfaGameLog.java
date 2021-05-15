package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Minigames.GameLogs.GameLog;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.TimeUtils;

import java.util.ArrayList;

/**
 * Represents a game log for Free For All.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.11
 * @since 2.3.1
 */
public final class FfaGameLog extends GameLog {

    private ArrayList<String> entrants;
    private ArrayList<String> combatLog;
    private ArrayList<String> rewardLog;

    public FfaGameLog(long serverId, String serverName) {
        super("ffa_" + serverId + "_" + System.currentTimeMillis(), "Free For All: " + serverName + " - " + TimeUtils.formatTime());
        entrants = new ArrayList<>();
        combatLog = new ArrayList<>();
        rewardLog = new ArrayList<>();
    }

    @Override
    public String formatLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("ENTRANTS:");
        for(String str : entrants) sb.append("\n").append(str);
        sb.append("\n\n").append("KILL FEED:");
        for(String str : combatLog) sb.append("\n").append(str);
        sb.append("\n\n").append("REWARDS:");
        for(String str : rewardLog) sb.append("\n").append(str);

        return sb.toString().trim();
    }

    public void addEntrant(String str) {
        entrants.add(str);
    }

    public void addCombat(String str){
        combatLog.add(str);
    }

    public void addReward(String str){
        rewardLog.add(str);
    }

}
