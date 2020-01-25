package me.Cooltimmetje.Skuddbot.Profiles.Server;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    private HashMap<Long,Long> lastSeen;

    public SkuddServer(long serverId, ServerSettingsSapling settingsSapling){
        this.serverId = serverId;
        this.settings = settingsSapling.grow();
        this.users = new ArrayList<>();
        this.lastSeen = new HashMap<>();
    }

    public SkuddUser getUser(long id){
        for(SkuddUser user : users)
            if(user.getId().getDiscordId() == id) {
                lastSeen.put(user.getId().getDiscordId(), System.currentTimeMillis());
                return user;
            }

        return null;
    }

    public void addUser(SkuddUser su){
        users.add(su);
        lastSeen.put(su.getId().getDiscordId(), System.currentTimeMillis());
    }

    public SkuddUser getUser(Identifier id){
        return getUser(id.getDiscordId());
    }

    public Long getRandomActiveUser(){
        return getRandomActiveUser(24*60*60*1000);
    }

    public Long getRandomActiveUser(long activeDelay){
        ArrayList<Long> active = gatherActiveUsers(activeDelay);
        if(active.size() < 2) throw new UnsupportedOperationException("The list is empty, sorry!");
        return active.get(MiscUtils.randomInt(0, active.size() - 1));
    }

    public ArrayList<Long> gatherActiveUsers(long activeDelay){
        ArrayList<Long> returnList = new ArrayList<>();
        Iterator<Long> iterator = lastSeen.keySet().iterator();
        while(iterator.hasNext()){
            long user = iterator.next();
            long lastSeen = this.lastSeen.get(user);
            if((System.currentTimeMillis() - lastSeen) < activeDelay)
                returnList.add(user);
        }

        return returnList;
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
