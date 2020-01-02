package me.Cooltimmetje.Skuddbot.Profiles.Server;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;

import java.util.ArrayList;

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
    private ArrayList<SkuddUser> users;

    public SkuddServer(long serverId, ServerSettingsSapling settingsSapling){
        this.serverId = serverId;
        this.settings = settingsSapling.grow();
        this.users = new ArrayList<>();
    }

    public SkuddUser getUser(long id){
        for(SkuddUser user : users)
            if(user.getId().getDiscordId() == id)
                return user;

        return null;
    }

    public void addUser(SkuddUser su){
        users.add(su);
    }

    public SkuddUser getUser(Identifier id){
        return getUser(id.getDiscordId());
    }

}
