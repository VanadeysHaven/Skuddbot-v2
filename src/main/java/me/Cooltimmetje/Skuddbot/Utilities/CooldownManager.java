package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

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
    private boolean requiresForceClear;

    public CooldownManager(int cooldown){
        this(cooldown, false);
    }

    public CooldownManager(int cooldown, boolean requiresForceClear){
        this.cooldown = cooldown;
        lastUsed = new HashMap<>();
        managers.add(this);
        this.requiresForceClear = requiresForceClear;
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

    public void clear(boolean clearForcefully){
        if(requiresForceClear) if(!clearForcefully) return;

        lastUsed.clear();
    }

    public static void clearAll(boolean clearForcefully){
        for(CooldownManager manager : managers)
            manager.clear(clearForcefully);
    }

}
