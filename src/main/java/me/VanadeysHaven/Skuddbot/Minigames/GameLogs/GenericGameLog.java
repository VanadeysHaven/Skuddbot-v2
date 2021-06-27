package me.VanadeysHaven.Skuddbot.Minigames.GameLogs;

import java.util.ArrayList;

/**
 * Generic simple game log.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.1
 * @since 2.3.1
 */
public final class GenericGameLog extends GameLog {

    private ArrayList<String> log;

    public GenericGameLog(String fileName, String header) {
        super(fileName, header);
        log = new ArrayList<>();
    }

    public void addToLog(String str){
        log.add(str);
    }

    @Override
    public String formatLog() {
        StringBuilder sb = new StringBuilder();
        for(String s : log) sb.append("\n").append(s);
        return sb.toString().trim();
    }

}
