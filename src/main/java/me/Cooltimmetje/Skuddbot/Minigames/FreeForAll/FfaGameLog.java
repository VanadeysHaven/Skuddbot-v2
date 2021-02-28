package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Minigames.GameLogs.GameLog;
import me.Cooltimmetje.Skuddbot.Utilities.TimeUtils;

import java.util.ArrayList;

/**
 * Represents a game log for Free For All.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3.1
 * @since 2.3.1
 */
public final class FfaGameLog extends GameLog {

    private static ArrayList<String> combatLog;
    private static ArrayList<String> rewardLog;

    public FfaGameLog(long serverId, String serverName) {
        super("ffa_" + serverId + "_" + System.currentTimeMillis(), "Free For All: " + serverName + " - " + TimeUtils.formatTime());
        combatLog = new ArrayList<>();
        rewardLog = new ArrayList<>();
    }

    @Override
    public String formatLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("KILL FEED:");
        for(String str : combatLog) sb.append("\n").append(str);
        sb.append("\n\n").append("REWARDS:");
        for(String str : rewardLog) sb.append("\n").append(str);

        return sb.toString().trim();
    }

    public void addCombat(String str){
        combatLog.add(str);
    }

    public void addReward(String str){
        rewardLog.add(str);
    }

}
