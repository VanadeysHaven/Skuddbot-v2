package me.VanadeysHaven.Skuddbot.Profiles.Users.Settings;

/**
 * Settings for level up notifications.
 *
 * @author Tim (Vanadey's Haven)
 * @since 2.0
 * @version 2.0
 */
public enum LevelUpNotification {

    REACTION, MESSAGE, DM, NOTHING;

    public static boolean exists(String str){
        for(LevelUpNotification notification : values())
            if(notification.toString().equals(str)) return true;

        return false;
    }

}
