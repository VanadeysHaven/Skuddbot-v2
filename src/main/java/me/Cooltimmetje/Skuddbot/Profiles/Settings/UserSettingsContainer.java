package me.Cooltimmetje.Skuddbot.Profiles.Settings;

import me.Cooltimmetje.Skuddbot.Enums.UserSetting;

import java.util.HashMap;

/**
 * <class discription>
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class UserSettingsContainer {

    private HashMap<UserSetting,String> settings;

    public UserSettingsContainer(UserSettingsSapling sapling){
        this.settings = new HashMap<>();
        processStatsSapling(sapling);
    }

    private void processStatsSapling(UserSettingsSapling sapling){
        for(UserSetting stat : UserSetting.values()){
            String value = sapling.getStat(stat);
            if(value != null) {
                setSetting(stat, value);
            } else {
                setSetting(stat, stat.getDefaultValue());
            }
        }
    }

    public void setSetting(UserSetting setting, String value){
        if(checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        this.settings.put(setting, value);
    }

    private boolean checkType(String input, UserSetting setting){

    }

}
