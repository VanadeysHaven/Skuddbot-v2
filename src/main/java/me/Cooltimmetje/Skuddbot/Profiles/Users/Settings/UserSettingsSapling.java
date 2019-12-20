package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Enums.UserSetting;

import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles user settings only.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class UserSettingsSapling {

    private HashMap<UserSetting,String> settings;

    public UserSettingsSapling(){
        this.settings = new HashMap<>();
    }

    public void addSetting(UserSetting setting, String value){
        settings.put(setting, value);
    }

    public String getSetting(UserSetting setting){
        if (settings.containsKey(setting))
            return settings.get(setting);
        return null;
    }

    public UserSettingsContainer grow(){
        return new UserSettingsContainer(this);
    }

}
