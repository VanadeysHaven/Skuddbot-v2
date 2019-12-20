package me.Cooltimmetje.Skuddbot.Profiles.Users;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.UserStatsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.UserStatsSapling;

/**
 * This class represents a user and their data and statistics.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class SkuddUser {

    @Getter private Identifier id;
    @Getter private UserStatsContainer stats;
    @Getter private UserSettingsContainer settings;

    public SkuddUser(Identifier id, UserStatsSapling stats, UserSettingsSapling settings){
        this.id = id;
        this.stats = stats.grow();
        this.settings = settings.grow();
    }

}
