package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Query;
import me.Cooltimmetje.Skuddbot.Enums.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles user settings only.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
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
            ResultSet rs = qe.executeQuery();
            while (rs.next()) {
                addSetting(UserSetting.getByDbReference(rs.getString("setting_name")), rs.getString("setting_value"));
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
