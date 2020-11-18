package me.Cooltimmetje.Skuddbot.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.SimpleTimeZone;

/**
 * Class for easily managing cooldowns
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class CooldownManager {

    private static ArrayList<CooldownManager> managers = new ArrayList<>();

    private int cooldown;
    private HashMap<String,Long> lastUsed;
    private boolean locked;

    public CooldownManager(int cooldown){
        this(cooldown, false);
    }

    public CooldownManager(int cooldown, boolean locked){
        this.cooldown = cooldown;
        lastUsed = new HashMap<>();
        managers.add(this);
        this.locked = locked;
    }

    public void startCooldown(String identifier){
        lastUsed.put(identifier, System.currentTimeMillis());
    }

    public void startCooldown(long identifier) {
        startCooldown(identifier+"");
    }

    public boolean isOnCooldown(String identifier) {
        if(!lastUsed.containsKey(identifier)) return false;
        long secondsSinceLastUse = (System.currentTimeMillis() - lastUsed.get(identifier)) / 1000;
        return secondsSinceLastUse <= cooldown;
    }

    public boolean isOnCooldown(long identifier){
        return isOnCooldown(identifier+"");
    }

    public long getTimeRemaining(long identifier){
        if(!lastUsed.containsKey(identifier+"")) throw new IllegalStateException("User is not on cooldown");
        long timeSinceCooldownStarted = (System.currentTimeMillis() - lastUsed.get(identifier+"")) / 1000;
        long ret = cooldown - timeSinceCooldownStarted;

        if(ret < 0) throw new IllegalStateException("User is not on cooldown");
        return ret;
    }

    public String formatTime(long identifier){
        return formatTime(identifier, "HH'h' mm'm' ss's'");
    }

    public String formatTime(long identifier, String timeFormat){
        long secondsRemaining = getTimeRemaining(identifier);
        long secondsInDayRemaining = secondsRemaining % 86400;
        long daysRemaining = (secondsRemaining - secondsInDayRemaining) / 86400;

        String formattedTime = "";
        if(secondsInDayRemaining > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
            formatter.setTimeZone(SimpleTimeZone.getTimeZone("GMT"));
            formattedTime = formatter.format(new Date(secondsInDayRemaining*1000));
        }

        String ret = (daysRemaining == 0 ? "" : daysRemaining + "d") + " " + formattedTime;
        if(ret.equalsIgnoreCase(" ")){
            return "0s";
        } else {
            return ret;
        }
    }

    public void clear(boolean clearForcefully){
        if(locked) if(!clearForcefully) return;

        lastUsed.clear();
    }

    public static void clearAll(boolean clearForcefully){
        for(CooldownManager manager : managers)
            manager.clear(clearForcefully);
    }

}
