package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Profiles.DataContainers.UserDataContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

/**
 * This holds all user settings.
 *
 * @author Tim (Cooltimmetje)
 * @since 2.3
 * @version 2.0
 */
public class UserSettingsContainer extends UserDataContainer<UserSetting> {

    public UserSettingsContainer(Identifier id, UserSettingsSapling sapling){
        super(id);
        processSettingsSapling(sapling);
    }

    private void processSettingsSapling(UserSettingsSapling sapling){
        for (UserSetting setting : UserSetting.values()) {
            String value = sapling.getSetting(setting);
            if (value != null) {
                setString(setting, value, false, true);
            } else {
                setString(setting, setting.getDefaultValue(), false, true);
            }
        }
    }

    public void setLevelUpNotify(LevelUpNotification notification) {
        setString(UserSetting.LEVEL_UP_NOTIFY, notification.toString());
    }

    public LevelUpNotification getLevelUpNotify(){
        return LevelUpNotification.valueOf(getString(UserSetting.LEVEL_UP_NOTIFY));
    }

}