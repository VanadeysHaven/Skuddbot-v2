package me.Cooltimmetje.Skuddbot.Profiles.Server;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * This holds all server settings.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ServerSettingsContainer {

    private long serverId;
    private HashMap<ServerSetting,String> settings;

    ServerSettingsContainer(long serverId, ServerSettingsSapling sapling){
        this.serverId = serverId;
        settings = new HashMap<>();
        processSapling(sapling);
    }

    private void processSapling(ServerSettingsSapling sapling){
        for(ServerSetting setting : ServerSetting.values()){
            String value = sapling.getSetting(setting);
            if(value != null){
                setString(setting, value, false);
            } else {
                setString(setting, setting.getDefaultValue(), false);
            }
        }
    }

    public void setString(ServerSetting setting, String value){
        setString(setting, value, true);
    }

    public void setString(ServerSetting setting, String value, boolean save){
        if(!checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        if(value != null) if(value.equalsIgnoreCase("null")) value = null;
        this.settings.put(setting, value);
        if(save) save(setting);
    }

    public String getString(ServerSetting setting){
        return this.settings.get(setting);
    }

    public void setInt(ServerSetting setting, int value){
        if(setting.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Setting " + setting + " is not of type INTEGER");
        setString(setting, value+"");
    }

    public int getInt(ServerSetting setting){
        if(setting.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Setting " + setting + " is not of type INTEGER");
        return Integer.parseInt(getString(setting));
    }

    public void setDouble(ServerSetting setting, double value){
        if(setting.getType() != ValueType.DOUBLE) throw new IllegalArgumentException("Setting " + setting + " is not of type DOUBLE");
        setString(setting, value+"");
    }

    public double getDouble(ServerSetting setting){
        if(setting.getType() != ValueType.DOUBLE) throw new IllegalArgumentException("Setting " + setting + " is not of type DOUBLE");
        return Double.parseDouble(getString(setting));
    }

    public void setLong(ServerSetting setting, long value){
        if(setting.getType() != ValueType.LONG) throw new IllegalArgumentException("Setting " + setting + " is not of type LONG");
        setString(setting, value+"");
    }

    public long getLong(ServerSetting setting){
        if(setting.getType() != ValueType.LONG) throw new IllegalArgumentException("Setting " + setting + " is not of type LONG");
        return Long.parseLong(getString(setting));
    }

    public void setBoolean(ServerSetting setting, boolean value){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting" + setting + " is not of type BOOLEAN");
        setString(setting, value+"");
    }

    public boolean getBoolean(ServerSetting setting){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting" + setting + " is not of type BOOLEAN");
        return Boolean.parseBoolean(getString(setting));
    }

    private boolean checkType(String input, ServerSetting setting){
        if(input == null) return true;
        ValueType type = setting.getType();
        if(type == ValueType.INTEGER){
            return MiscUtils.isInt(input);
        }
        if(type == ValueType.DOUBLE){
            return MiscUtils.isDouble(input);
        }
        if(type == ValueType.LONG){
            return MiscUtils.isLong(input);
        }
        if(type == ValueType.BOOLEAN){
            return MiscUtils.isBoolean(input);
        }

        return type == ValueType.STRING;
    }

    private boolean isDefault(ServerSetting setting, String value){
        if(setting.getDefaultValue() == null && value == null) return true;
        if(value == null && setting.getDefaultValue() != null) return false;
        assert value != null;
        return value.equals(setting.getDefaultValue());
    }

    private void save(ServerSetting setting) {
        QueryExecutor qe = null;
        if (isDefault(setting, getString(setting))){
            try {
                qe = new QueryExecutor(Query.DELETE_SERVER_SETTING_VALUE).setLong(1, serverId).setString(2, setting.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (qe != null) qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(Query.UPDATE_SERVER_SETTING_VALUE).setString(1, setting.getDbReference()).setLong(2, serverId).setString(3, getString(setting)).and(4);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (qe != null) qe.close();
            }
        }
    }


    public void save(){
        for(ServerSetting setting : ServerSetting.values()){
            save(setting);
        }
    }

}
