package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for easily managing cooldowns
 *
 * @author Tim (Vanadey's Haven)
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
        long timeSinceCooldownStarted = (System.currentTimeMillis() - lastUsed.get(identifier+""));
        long ret = (cooldown * 1000) - timeSinceCooldownStarted;

        if(ret < 0) throw new IllegalStateException("User is not on cooldown");
        return ret;
    }

    public String formatTime(long identifier){
        return TimeUtils.formatTimeRemaining(getTimeRemaining(identifier));
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
