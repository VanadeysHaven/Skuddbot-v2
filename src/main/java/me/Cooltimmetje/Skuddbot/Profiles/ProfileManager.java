package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsSapling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to recall profiles.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ProfileManager {

    private ServerManager sm = new ServerManager();
    private static final Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    public SkuddUser getUser(long serverId, long discordId){
        logger.info("Requested user profile for user id " + discordId + " on server id " + serverId);
        SkuddServer ss = sm.getServer(serverId);
        SkuddUser su = ss.getUser(discordId);

        if(su == null){ //Doesn't exist, create new
            logger.info("Loading from database...");
            Identifier id = new Identifier(serverId, discordId);
            id.save();
            su = new SkuddUser(id, new StatsSapling(id), new UserSettingsSapling(id));
            ss.addUser(su);
        }

        su.setActive(true);
        return su;
    }

    public SkuddUser getUser(Identifier id){
        return getUser(id.getServerId(), id.getDiscordId());
    }



}
