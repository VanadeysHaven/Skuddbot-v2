package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

/**
 * This class is used to recall profiles.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ProfileManager {

    ServerManager sm = new ServerManager();

    public void getUser(long serverId, long userId){
        SkuddServer server = sm.getServer(serverId);
        server.getUser(userId);
        if()
    }

    public void getUser(Identifier id){
        getUser(id.getServerId(), id.getUserId());
    }



}
