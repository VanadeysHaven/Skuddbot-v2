package me.Cooltimmetje.Skuddbot.Profiles.DataContainers;

import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;

import java.util.HashMap;

/**
 * Main class that serves as a basis for all data containers.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.3
 */
public class DataContainer<T> {

    private HashMap<T, String> values;
    private HashMap<T, CooldownManager> cooldowns;

    public DataContainer(){
        values = new HashMap<>();
        cooldowns = new HashMap<>();
    }

}
