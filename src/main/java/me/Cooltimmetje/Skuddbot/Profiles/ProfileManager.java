package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.CurrenciesSapling;
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
 * @version ALPHA-2.1.1
 * @since ALPHA-2.0
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
            su = new SkuddUser(id, new StatsSapling(id), new UserSettingsSapling(id), new CurrenciesSapling(id));
            ss.addUser(su);
        }

        su.setActive(true);
        return su;
    }

    public SkuddUser getUser(Identifier id){
        return getUser(id.getServerId(), id.getDiscordId());
    }



}
