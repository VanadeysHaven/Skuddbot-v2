package me.Cooltimmetje.Skuddbot.Donator;

import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Query;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Manages everything to do with donator data.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class DonatorManager {

    private static final Logger logger = LoggerFactory.getLogger(DonatorManager.class);

    private static ArrayList<DonatorMessage> messages = new ArrayList<>();
    private static ArrayList<DonatorUser> users = new ArrayList<>();
    private static ArrayList<DonatorMessage.Type> presentTypes = new ArrayList<>();

    public DonatorManager(){
        if(messages.isEmpty() && users.isEmpty()) {
            logger.info("No donator data present, loading data.");
            loadAll();
        }
    }

    private void loadAll(){
        QueryExecutor qe = null;
        try {
            logger.info("Load users...");
            qe = new QueryExecutor(Query.LOAD_ALL_DONATORS);
            ResultSet rs = qe.executeQuery();
            while(rs.next()) users.add(new DonatorUser(rs.getLong("id"), rs.getString("ping_message")));
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
        try {
            logger.info("Load messages...");
            qe = new QueryExecutor(Query.LOAD_ALL_MESSAGES);
            ResultSet rs = qe.executeQuery();
            while(rs.next()) addMessage(getUser(rs.getLong("discord_id")), DonatorMessage.Type.getByDbReference(rs.getString("data_name")), rs.getString("data_value"));

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            qe.close();
        }
    }

    public DonatorMessage getMessage(DonatorMessage.Type type){
        logger.info("Requesting donator message of type " + type);
        if(!presentTypes.contains(type)) throw new IllegalArgumentException("Type " + type + " is not present.");
        int attempts = 0;
        DonatorMessage dm;
        do {
            do {
                dm = messages.get(MiscUtils.randomInt(0, messages.size() - 1));
                attempts++;
            } while (dm.getType() != type);
        } while (!dm.isAllowed());

        logger.info("Found valid donator message in " + attempts + "attempts.");
        dm.setLastShown(System.currentTimeMillis());
        return dm;
    }

    public void addMessage(DonatorUser owner, DonatorMessage.Type type, String message){
        if(!presentTypes.contains(type)) presentTypes.add(type);
        messages.add(new DonatorMessage(owner, type, message));
    }

    public DonatorUser getUser(long id){
        for(DonatorUser user : users)
            if(user.getId() == id)
                return user;

        return null;
    }

    public boolean isDonator(long id){
        for(DonatorUser user : users)
            if(user.getId() == id)
                return true;

        return false;
    }

}
