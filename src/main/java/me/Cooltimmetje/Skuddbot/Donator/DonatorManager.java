package me.Cooltimmetje.Skuddbot.Donator;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()) users.add(new DonatorUser(qr.getLong("id"), qr.getString("ping_message")));
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
        try {
            logger.info("Load messages...");
            qe = new QueryExecutor(Query.LOAD_ALL_MESSAGES);
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()) addMessage(getUser(qr.getLong("discord_id")), DonatorMessage.Type.getByDbReference(qr.getString("data_name")), qr.getString("data_value"));

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

        logger.info("Found valid donator message in " + attempts + " attempts.");
        dm.setLastShown(System.currentTimeMillis());
        return dm;
    }

    public DonatorMessage addMessage(DonatorUser owner, DonatorMessage.Type type, String message){
        if(!presentTypes.contains(type)) presentTypes.add(type);
        DonatorMessage dMsg = new DonatorMessage(owner, type, message);
        messages.add(dMsg);

        return dMsg;
    }

    public void addDonator(long id){
        DonatorUser du = new DonatorUser(id);
        users.add(du);
        du.save();
    }

    public void removeDonator(long id){
        DonatorUser du = getUser(id);
        messages.removeIf(message -> message.getOwner().getId() == id);
        users.remove(du);
        du.purge();
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

    public boolean doesMessageExist(DonatorMessage.Type type, String msg){
        for(DonatorMessage message : messages)
            if(type == message.getType() && msg.equals(message.getMessage()))
                return true;

        return false;
    }

}
