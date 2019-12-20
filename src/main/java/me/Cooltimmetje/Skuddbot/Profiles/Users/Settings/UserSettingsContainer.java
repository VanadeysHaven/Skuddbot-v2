package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Enums.LevelUpNotification;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.util.HashMap;

/**
 * This holds all user settings.
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
                setString(setting, value);
            } else {
                setString(setting, setting.getDefaultValue());
            }
        }
    }

    public void setString(UserSetting setting, String value){
        if(checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        this.settings.put(setting, value);
    }

    public String getString(UserSetting setting){
        return this.settings.get(setting);
    }

    public boolean getBoolean(UserSetting setting){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        return Boolean.parseBoolean(getString(setting));
    }

    public void setBoolean(UserSetting setting, boolean value){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        setString(setting, value+"");
    }

    public void toggleBoolean(UserSetting setting){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        setBoolean(setting, !getBoolean(setting));
    }

    public LevelUpNotification getLevelUpNotify(){
        return LevelUpNotification.valueOf(getString(UserSetting.LEVEL_UP_NOTIFY));
    }

    public void setLevelUpNotify(LevelUpNotification notification){
        setString(UserSetting.LEVEL_UP_NOTIFY, notification.toString());
    }

    private boolean checkType(String input, UserSetting setting){
        ValueType type = setting.getType();
        if(type == ValueType.BOOLEAN){
            return MiscUtils.isBoolean(input);
        }
        if(setting == UserSetting.LEVEL_UP_NOTIFY){
            return LevelUpNotification.exists(input);
        }

        return type == ValueType.STRING;
    }

}
