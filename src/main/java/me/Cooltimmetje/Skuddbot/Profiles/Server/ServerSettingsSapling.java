package me.Cooltimmetje.Skuddbot.Profiles.Server;

import me.Cooltimmetje.Skuddbot.Enums.ServerSettings.ServerSetting;

import java.util.HashMap;

/**
 * This class is used to provide an easy "interface" between the database and the profile system. This handles server settings only.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ServerSettingsSapling {

    private HashMap<ServerSetting,String> settings;

    public ServerSettingsSapling(){
        this.settings = new HashMap<>();
    }

    public void addSetting(ServerSetting setting, String value){
        settings.put(setting, value);
    }

    public String getSetting(ServerSetting setting){
        if (settings.containsKey(setting))
            return settings.get(setting);
        return null;
    }

    public ServerSettingsContainer grow(){
        return new ServerSettingsContainer(this);
    }

}
