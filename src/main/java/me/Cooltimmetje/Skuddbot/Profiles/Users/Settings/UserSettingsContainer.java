package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.LevelUpNotification;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * This holds all user settings.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class UserSettingsContainer {

    private Identifier id;
    private HashMap<UserSetting,String> settings;

    public UserSettingsContainer(Identifier id, UserSettingsSapling sapling){
        this.id = id;
        this.settings = new HashMap<>();
        processSettingsSapling(sapling);
    }

    private void processSettingsSapling(UserSettingsSapling sapling){
        for(UserSetting setting : UserSetting.values()){
            String value = sapling.getSetting(setting);
            if(value != null) {
                setString(setting, value, false);
            } else {
                setString(setting, setting.getDefaultValue(), false);
            }
        }
    }

    public void setString(UserSetting setting, String value){
        setString(setting, value, true);
    }

    public void setString(UserSetting setting, String value, boolean save){
        if(!checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        this.settings.put(setting, value);
        if(save) save(setting);
    }

    public String getString(UserSetting setting){
        return this.settings.get(setting);
    }

    public void setBoolean(UserSetting setting, boolean value){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        setString(setting, value+"");
    }

    public boolean getBoolean(UserSetting setting){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        return Boolean.parseBoolean(getString(setting));
    }

    public void toggleBoolean(UserSetting setting){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        setBoolean(setting, !getBoolean(setting));
    }

    public void setLevelUpNotify(LevelUpNotification notification){
        setString(UserSetting.LEVEL_UP_NOTIFY, notification.toString());
    }

    public LevelUpNotification getLevelUpNotify(){
        return LevelUpNotification.valueOf(getString(UserSetting.LEVEL_UP_NOTIFY));
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

    private void save(UserSetting setting){
        QueryExecutor qe = null;
        if (getString(setting).equals(setting.getDefaultValue())) {
            try {
                qe = new QueryExecutor(Query.DELETE_USER_SETTING_VALUE).setInt(1, id.getId()).setString(2, setting.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(Query.UPDATE_USER_SETTING_VALUE).setString(1, setting.getDbReference()).setInt(2, id.getId()).setString(3, getString(setting)).and(4);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        }
    }

    public void save(){
        for(UserSetting setting : UserSetting.values()){
            save(setting);
        }
    }




}
