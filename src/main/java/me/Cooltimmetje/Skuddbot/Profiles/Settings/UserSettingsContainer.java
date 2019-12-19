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
        for(UserSetting setting : UserSetting.values()){
            String value = sapling.getSetting(setting);
            if(value != null) {
                setSetting(setting, value);
            } else {
                setSetting(setting, setting.getDefaultValue());
            }
        }
    }

    public void setSetting(UserSetting setting, String value){
        if(checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        this.settings.put(setting, value);
    }

    private boolean checkType(String input, UserSetting setting){
        //TODO
        return false;
    }

}
