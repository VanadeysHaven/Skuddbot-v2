package me.VanadeysHaven.Skuddbot.Profiles;

import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Identifier;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to recall profiles.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.1.1
 * @since 2.0
 */
public class ProfileManager {

    private static ProfileManager instance;

    public static ProfileManager getInstance(){
        if(instance == null)
            instance = new ProfileManager();

        return instance;
    }

    private ServerManager sm = ServerManager.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    private ProfileManager(){}

    public SkuddUser getUser(long serverId, long discordId){
        logger.info("Requested user profile for user id " + discordId + " on server id " + serverId);
        SkuddServer ss = sm.getServer(serverId);
        SkuddUser su = ss.getUser(discordId);

        if(su == null){ //Doesn't exist, create new
            logger.info("Loading from database...");
            Identifier id = new Identifier(serverId, discordId);
            id.save();
            su = new SkuddUser(id);
            ss.addUser(su);
        }

        su.setActive(true);
        return su;
    }

    public SkuddUser getUser(Identifier id){
        return getUser(id.getServerId(), id.getDiscordId());
    }



}
