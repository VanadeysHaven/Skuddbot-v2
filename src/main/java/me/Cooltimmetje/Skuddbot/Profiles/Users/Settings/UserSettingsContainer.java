package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Exceptions.CooldownException;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * This holds all user settings.
 *
 * @author Tim (Cooltimmetje)
 * @since 2.2.1
 * @version 2.0
 */
public class UserSettingsContainer {

    private Identifier id;
    private HashMap<UserSetting,String> settings;
    private HashMap<UserSetting,CooldownManager> cooldowns;

    public UserSettingsContainer(Identifier id, UserSettingsSapling sapling){
        this.id = id;
        this.settings = new HashMap<>();
        cooldowns = new HashMap<>();
        processSettingsSapling(sapling);
    }

    private void processSettingsSapling(UserSettingsSapling sapling){
        try {
            for (UserSetting setting : UserSetting.values()) {
                String value = sapling.getSetting(setting);
                if (value != null) {
                    setString(setting, value, false, true);
                } else {
                    setString(setting, setting.getDefaultValue(), false, true);
                }
            }
        } catch (CooldownException e){
            e.printStackTrace();
            //do nothing cuz it won't be thrown here
        }
    }

    public void setString(UserSetting setting, String value) throws CooldownException{
        setString(setting, value, true, false);
    }

    public void setString(UserSetting setting, String value, boolean save, boolean bypassCooldown) throws CooldownException {
        if(!checkType(value, setting)) throw new IllegalArgumentException("Value " + value + " is unsuitable for setting " + setting + "; not of type " + setting.getType());
        if(isOnCooldown(setting) && !bypassCooldown) throw new CooldownException("You can't change setting `" + setting + "` currently. You can change it again in: " + getCooldownManager(setting).formatTime(id.getDiscordId()));

        this.settings.put(setting, value);
        if(!bypassCooldown) startCooldown(setting);
        if(save) save(setting);
    }

    public String getString(UserSetting setting){
        return this.settings.get(setting);
    }

    public void setBoolean(UserSetting setting, boolean value) throws CooldownException{
        setString(setting, value+"");
    }

    public boolean getBoolean(UserSetting setting){
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        return Boolean.parseBoolean(getString(setting));
    }

    public void toggleBoolean(UserSetting setting) throws CooldownException{
        if(setting.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException("Setting " + setting + " is not of type BOOLEAN");
        setBoolean(setting, !getBoolean(setting));
    }

    public int getInt(UserSetting setting){
        if(setting.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Setting " + setting + " is not of type INTEGER");
        return Integer.parseInt(getString(setting));
    }

    public void setInt(UserSetting setting, int value) throws CooldownException {
        setString(setting, value+"");
    }

    public void setLevelUpNotify(LevelUpNotification notification) throws CooldownException {
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
        if(type == ValueType.INTEGER){
            return MiscUtils.isInt(input);
        }

        return type == ValueType.STRING;
    }

    private void startCooldown(UserSetting setting){
        if(!setting.hasCooldown()) return;
        getCooldownManager(setting).startCooldown(id.getDiscordId());
    }

    public boolean isOnCooldown(UserSetting setting){
        return getCooldownManager(setting).isOnCooldown(id.getDiscordId());
    }

    private CooldownManager getCooldownManager(UserSetting setting){
        CooldownManager manager;
        if(!cooldowns.containsKey(setting)) {
            manager = new CooldownManager(setting.getCooldown());
            cooldowns.put(setting, manager);
        } else manager = cooldowns.get(setting);

        return manager;
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
