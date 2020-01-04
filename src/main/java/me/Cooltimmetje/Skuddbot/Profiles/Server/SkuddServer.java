package me.Cooltimmetje.Skuddbot.Profiles.Server;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * This class represents a guild, and it's settings and user profiles.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class SkuddServer {

    private static final Logger logger = LoggerFactory.getLogger(SkuddServer.class);
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

    public void runActivity(){
        logger.info("Running activity check for server " + serverId);
        ArrayList<SkuddUser> inactive = new ArrayList<>();
        for(SkuddUser user : users){
            if(!user.isActive()) {
                inactive.add(user);
            } else {
                user.setActive(false);
            }
        }
        for(SkuddUser user : inactive){
            logger.info(user.getId().toString() + " is inactive, removing...");
            users.remove(user);
        }
    }

}
