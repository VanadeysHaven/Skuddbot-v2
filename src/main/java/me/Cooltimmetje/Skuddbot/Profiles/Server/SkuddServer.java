package me.Cooltimmetje.Skuddbot.Profiles.Server;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class represents a guild, and it's settings and user profiles.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.1.1
 * @version ALPHA-2.0
 */
public class SkuddServer {

    private static final Logger logger = LoggerFactory.getLogger(SkuddServer.class);
    @Getter private long serverId;
    @Getter private ServerSettingsContainer settings;
    private ArrayList<SkuddUser> users;
    private HashMap<Long,Long> lastSeen;
    private RNGManager random;

    public SkuddServer(long serverId, ServerSettingsSapling settingsSapling){
        this.serverId = serverId;
        this.settings = settingsSapling.grow();
        this.users = new ArrayList<>();
        this.lastSeen = new HashMap<>();
        random = new RNGManager();
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
        if(active.size() < 2) {
            LinkedHashMap<Identifier,Integer> top = getTopStats(10, Stat.EXPERIENCE);
            for(Identifier id : top.keySet()) lastSeen.put(id.getDiscordId(), System.currentTimeMillis());
            active = gatherActiveUsers(activeDelay);
        }
        if(active.size() < 2) throw new UnsupportedOperationException("The list is empty, sorry!");

        return active.get(random.integer(0, active.size() - 1));
    }

    public ArrayList<Long> gatherActiveUsers(long activeDelay){
        ArrayList<Long> returnList = new ArrayList<>();
        for (long user : lastSeen.keySet()) {
            long lastSeen = this.lastSeen.get(user);
            if ((System.currentTimeMillis() - lastSeen) < activeDelay)
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

    public LinkedHashMap<Identifier, Integer> getTopStats(int limit, Stat stat){
        if(!stat.isHasLeaderboard()) throw new UnsupportedOperationException("This stat does not have a leaderboard!");

        LinkedHashMap<Identifier, Integer> statValues = new LinkedHashMap<>();
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_STAT_VALUES).setLong(1, serverId).setString(2, stat.getDbReference());
            QueryResult qr = qe.executeQuery();
            while (qr.nextResult()){
                statValues.put(new Identifier(serverId, qr.getLong("discord_id"), qr.getString("twitch_username")), qr.getInt("stat_value"));
                if(statValues.size() >= limit)
                    break;
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }

        return statValues;
    }

    public LinkedHashMap<Identifier, Integer> getTopCurrencies(int limit, Currency currency) {
        if(!currency.isHasLeaderboard()) throw new UnsupportedOperationException("This currency does not have a leaderboard!");

        LinkedHashMap<Identifier, Integer> currencyValues = new LinkedHashMap<>();
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_CURRENCY_VALUES).setLong(1, serverId).setString(2, currency.getDbReference());
            QueryResult qr = qe.executeQuery();
            while (qr.nextResult()){
                currencyValues.put(new Identifier(serverId, qr.getLong("discord_id"), qr.getString("twitch_username")), qr.getInt("currency_value"));
                if(currencyValues.size() >= limit)
                    break;
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }

        return currencyValues;
    }

    public void save(){
        logger.info("Saving server " + serverId);
        for(SkuddUser user : users) {
            user.save();
        }
        getSettings().save();
    }

    public String getName(){
        Server server = Main.getSkuddbot().getApi().getServerById(serverId).orElse(null); assert server != null;
        return server.getName();
    }
}
