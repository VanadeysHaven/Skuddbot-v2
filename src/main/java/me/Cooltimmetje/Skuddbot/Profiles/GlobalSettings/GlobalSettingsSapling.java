package me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * [class description]
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class GlobalSettingsSapling {

    private HashMap<GlobalSetting,String> settings;

    public GlobalSettingsSapling(){
        settings = new HashMap<>();

        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_GLOBAL_SETTINGS);
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()){
                addSetting(GlobalSetting.getByDbReference(qr.getString("setting")), qr.getString("value"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

    private void addSetting(GlobalSetting setting, String value){
        settings.put(setting, value);
    }

    String getString(GlobalSetting setting){
        if(settings.containsKey(setting))
            return settings.get(setting);

        return null;
    }

    public GlobalSettingsContainer grow() {
        return new GlobalSettingsContainer(this);
    }
}
