package me.Cooltimmetje.Skuddbot.Profiles.Users;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsSapling;

/**
 * This class represents a user and their data and statistics.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class SkuddUser {

    @Getter private Identifier id;
    @Getter private StatsContainer stats;
    @Getter private UserSettingsContainer settings;
    @Getter @Setter boolean active;

    public SkuddUser(Identifier id, StatsSapling stats, UserSettingsSapling settings) {
        this.id = id;
        this.stats = stats.grow();
        this.settings = settings.grow();
        active = true;
    }

    public PermissionManager getPermissions(){
        return new PermissionManager(id);
    }


}
