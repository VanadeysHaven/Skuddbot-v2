package me.Cooltimmetje.Skuddbot.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for easily managing cooldowns
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class CooldownManager {

    private static ArrayList<CooldownManager> managers = new ArrayList<>();

    private int cooldown;
    private HashMap<String,Long> lastUsed;

    public CooldownManager(int cooldown){
        this.cooldown = cooldown;
        lastUsed = new HashMap<>();
        managers.add(this);
    }

    public void startCooldown(String identifier){
        lastUsed.put(identifier, System.currentTimeMillis());
    }

    public boolean isOnCooldown(String identifier) {
        if(!lastUsed.containsKey(identifier)) return false;
        long secondsSinceLastUse = (System.currentTimeMillis() - lastUsed.get(identifier)) / 1000;
        return secondsSinceLastUse <= cooldown;
    }

    public void clear(){
        lastUsed.clear();
    }

    public static void clearAll(){
        for(CooldownManager manager : managers){
            manager.clear();
        }
    }

}
