package me.Cooltimmetje.Skuddbot.Profiles.Server;

import me.Cooltimmetje.Skuddbot.Profiles.DataContainers.ServerDataContainer;

/**
 * This holds all server settings.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.0
 */
public class ServerSettingsContainer extends ServerDataContainer<ServerSetting> {

    ServerSettingsContainer(long serverId, ServerSettingsSapling sapling){
        super(serverId);
        processSapling(sapling);
    }

    private void processSapling(ServerSettingsSapling sapling){
        for(ServerSetting setting : ServerSetting.values()){
            String value = sapling.getSetting(setting);
            if(value != null){
                setString(setting, value, false, true);
            } else {
                setString(setting, setting.getDefaultValue(), false, true);
            }
        }
    }

}
