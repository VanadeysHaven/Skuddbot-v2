package me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings;

import me.VanadeysHaven.Skuddbot.Database.Query;
import me.VanadeysHaven.Skuddbot.Database.QueryExecutor;
import me.VanadeysHaven.Skuddbot.Enums.ValueType;
import me.VanadeysHaven.Skuddbot.Utilities.AppearanceManager;
import me.VanadeysHaven.Skuddbot.Utilities.EnvironmentVariables.EnvVarsManager;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Container for global settings.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.21
 * @since 2.0
 */
public class GlobalSettingsContainer {

    private final HashMap<GlobalSetting,String> settings;

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
        if(!setting.hasLinkedEnvVariable()) return settings.get(setting);
        String value = EnvVarsManager.getInstance().getEnvVariable(setting.getLinkedEnvVariable());
        if(value.equals(setting.getLinkedEnvVariable().getDefaultValue())) return settings.get(setting);
        return value;
    }

    public void setBoolean(GlobalSetting setting, boolean value){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting" + setting + " is not of type BOOLEAN");
        setString(setting, value+"");
    }

    public boolean getBoolean(GlobalSetting setting) {
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting" + setting + " is not of type BOOLEAN");
        return Boolean.parseBoolean(getString(setting));
    }

    public void setCurrentAvatar(AppearanceManager.Avatar avatar){
        setString(GlobalSetting.CURRENT_AVATAR, avatar.toString());
    }

    public AppearanceManager.Avatar getCurrentAvatar(){
        return AppearanceManager.Avatar.valueOf(getString(GlobalSetting.CURRENT_AVATAR));
    }

    private boolean checkType(String value, GlobalSetting setting){
        ValueType type = setting.getType();
        if(setting == GlobalSetting.CURRENT_AVATAR) {
            return AppearanceManager.Avatar.exists(value);
        }
        if(type == ValueType.BOOLEAN){
            return MiscUtils.isBoolean(value);
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
