package me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.GlobalSetting;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Utilities.AppearanceManager;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * [class description]
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class GlobalSettingsContainer {

    private HashMap<GlobalSetting,String> settings;

    public GlobalSettingsContainer(GlobalSettingsSapling sapling){
        settings = new HashMap<>();
        processSapling(sapling);
    }

    public void processSapling(GlobalSettingsSapling sapling){
        for(GlobalSetting setting : GlobalSetting.values()){
            String value = sapling.getString(setting);
            if(value != null){
                setString(setting, value, false);
            } else {
                setString(setting, setting.getDefaultValue(), false);
            }
        }
    }

    public void setString(GlobalSetting setting, String value){
        setString(setting, value, true);
    }

    public void setString(GlobalSetting setting, String value, boolean save){
        if(!checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        this.settings.put(setting, value);
        if(save) save(setting);
    }

    public String getString(GlobalSetting setting){
        return settings.get(setting);
    }

    private boolean checkType(String value, GlobalSetting setting){
        ValueType type = setting.getType();
        if(setting == GlobalSetting.CURRENT_AVATAR) {
            return AppearanceManager.Avatar.exists(value);
        }
        return type == ValueType.STRING;
    }

    public void save(){
        for(GlobalSetting setting : GlobalSetting.values())
            save(setting);
    }

    public void save(GlobalSetting setting) {
        QueryExecutor qe = null;
        if(getString(setting).equals(setting.getDefaultValue())){
            try {
                qe = new QueryExecutor(Query.DELETE_GLOBAL_SETTING).setString(1, setting.getDbReference());
                qe.execute();
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(Query.UPDATE_GLOBAL_SETTING).setString(1, setting.getDbReference()).setString(2, getString(setting)).and(3);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        }
    }


}
