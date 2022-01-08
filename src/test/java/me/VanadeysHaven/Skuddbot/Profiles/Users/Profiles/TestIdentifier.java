package me.VanadeysHaven.Skuddbot.Profiles.Users.Profiles;

import me.VanadeysHaven.Skuddbot.Profiles.ServerMember;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Identifier;

public class TestIdentifier extends Identifier {

    public TestIdentifier() {
        super(123, 123, "aTwitchUser");
    }

    @Override
    public int getId() {
        return 123;
    }

    @Override
    public void save() {
        //do nothing
    }

    @Override
    public ServerMember asServerMember() {
        //do nothing
        return null;
    }

}
