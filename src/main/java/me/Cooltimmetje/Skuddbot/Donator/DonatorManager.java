package me.Cooltimmetje.Skuddbot.Donator;

import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Query;
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

    public DonatorManager(){
        logger.info("New donator manager created, loading data.");

        loadAll();
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
            while(rs.next()) messages.add(new DonatorMessage(getUser(rs.getLong("discord_id")), DonatorMessage.Type.getByDbReference(rs.getString("data_name")), rs.getString("data_value")));

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            qe.close();
        }
    }

    public DonatorUser getUser(long id){
        for(DonatorUser user : users)
            if(user.getId() == id)
                return user;

        return null;
    }

}
