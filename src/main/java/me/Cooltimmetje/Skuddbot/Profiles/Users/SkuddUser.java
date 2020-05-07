package me.Cooltimmetje.Skuddbot.Profiles.Users;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.CurrenciesContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.CurrenciesSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsSapling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a user and their data and statistics.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.0
 */
public class SkuddUser {

    private static final Logger logger = LoggerFactory.getLogger(SkuddUser.class);

    @Getter private Identifier id;
    @Getter private StatsContainer stats;
    @Getter private UserSettingsContainer settings;
    @Getter private CurrenciesContainer currencies;
    @Getter @Setter boolean active;

    public SkuddUser(Identifier id, StatsSapling stats, UserSettingsSapling settings, CurrenciesSapling currencies) {
        this.id = id;
        this.stats = stats.grow();
        this.settings = settings.grow();
        this.currencies = currencies.grow();
        active = true;
    }

    public PermissionManager getPermissions(){
        return new PermissionManager(id);
    }

    public void save() {
        logger.info("Saving user " + id.toString());
        getStats().save();
        getSettings().save();
        getCurrencies().save();
    }

    public ServerMember asMember(){
        return new ServerMember(id);
    }

}
