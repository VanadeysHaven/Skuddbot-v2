package me.Cooltimmetje.Skuddbot.Profiles.Server;

import lombok.Getter;

/**
 * This class represents a guild, and it's settings and user profiles.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class SkuddServer {

    @Getter private long serverId;
    @Getter private ServerSettingsContainer settings;

    public SkuddServer(long serverId, ServerSettingsSapling settingsSapling){
        this.serverId = serverId;
        this.settings = settingsSapling.grow();
    }

}
