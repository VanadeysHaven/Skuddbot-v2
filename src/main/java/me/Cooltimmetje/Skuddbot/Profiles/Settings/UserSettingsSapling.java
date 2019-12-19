package me.Cooltimmetje.Skuddbot.Profiles.Settings;

import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Enums.UserStat;

import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles setting only.
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

    public String getStat(UserStat setting){
        if (settings.containsKey(setting))
            return settings.get(setting);
        return null;
    }

    public UserSettingsContainer grow(){
        return new UserSettingsContainer(this);
    }

}
