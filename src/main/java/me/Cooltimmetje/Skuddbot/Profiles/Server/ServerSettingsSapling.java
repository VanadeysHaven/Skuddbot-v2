package me.Cooltimmetje.Skuddbot.Profiles.Server;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles server settings only.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ServerSettingsSapling {

    private long id;
    private HashMap<ServerSetting,String> settings;

    public ServerSettingsSapling(long id){
        this.id = id;
        settings = new HashMap<>();

        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_SERVER_SETTINGS).setLong(1, id);
            QueryResult qr = qe.executeQuery();
            while (qr.nextResult()){
                addSetting(ServerSetting.getByDbReference(qr.getString("setting_name")), qr.getString("setting_value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

    private void addSetting(ServerSetting setting, String value){
        settings.put(setting, value);
    }

    String getSetting(ServerSetting setting){
        if (settings.containsKey(setting))
            return settings.get(setting);
        return null;
    }

    ServerSettingsContainer grow(){
        return new ServerSettingsContainer(id, this);
    }

}
