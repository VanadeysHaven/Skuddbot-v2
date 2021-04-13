package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles user settings only.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.0
 * @since 2.0
 */
public class UserSettingsSapling {

    private Identifier id;
    private HashMap<UserSetting,String> settings;

    public UserSettingsSapling(Identifier id){
        this.id = id;
        this.settings = new HashMap<>();

        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_USER_SETTINGS).setInt(1, id.getId());
            QueryResult qr = qe.executeQuery();
            while (qr.nextResult()) {
                addSetting(UserSetting.getByDbReference(qr.getString("setting_name")), qr.getString("setting_value"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

    public void addSetting(UserSetting setting, String value){
        settings.put(setting, value);
    }

    public String getSetting(UserSetting setting){
        if (settings.containsKey(setting))
            return settings.get(setting);
        return null;
    }

    public UserSettingsContainer grow(){
        return new UserSettingsContainer(id, this);
    }

}
