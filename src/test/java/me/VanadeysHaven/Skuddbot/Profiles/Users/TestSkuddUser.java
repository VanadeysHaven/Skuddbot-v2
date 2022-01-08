package me.VanadeysHaven.Skuddbot.Profiles.Users;

import me.VanadeysHaven.Skuddbot.Profiles.ServerMember;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.CurrenciesContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Profiles.TestIdentifier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSettingsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.StatsSapling;

public class TestSkuddUser extends SkuddUser {

    public TestSkuddUser(TestIdentifier id) {
        super(id);
    }

    @Override
    public PermissionManager getPermissions() {
        //do nothing
        return null;
    }

    @Override
    public StatsContainer getStats() {
        return new StatsContainer(getId(), new StatsSapling(getId()));
    }

    @Override
    public UserSettingsContainer getSettings() {
        return super.getSettings();
    }

    @Override
    public CurrenciesContainer getCurrencies() {
        return super.getCurrencies();
    }

    @Override
    public void save() {
        //do nothing
    }

    @Override
    public ServerMember asMember() {
        //do nothing
        return null;
    }
}
