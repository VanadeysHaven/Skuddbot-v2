package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.UserStatsSapling;

/**
 * This class is used to recall profiles.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ProfileManager {

    private ServerManager sm = new ServerManager();

    public SkuddUser getUser(long serverId, long discordId){
        SkuddServer ss = sm.getServer(serverId);
        SkuddUser su = ss.getUser(discordId);

        if(su == null){ //Doesn't exist, create new
            Identifier id = new Identifier(serverId, discordId);
            id.save();
            su = new SkuddUser(id, new UserStatsSapling(id), new UserSettingsSapling(id));
            ss.addUser(su);
        }

        return su;
    }

    public SkuddUser getUser(Identifier id){
        return getUser(id.getServerId(), id.getDiscordId());
    }



}
