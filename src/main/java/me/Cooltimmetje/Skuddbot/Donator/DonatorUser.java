package me.Cooltimmetje.Skuddbot.Donator;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * A donator user.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class DonatorUser {

    private static final Logger logger = LoggerFactory.getLogger(DonatorUser.class);

    @Getter private long id;
    @Getter @Setter private String pingMessage;


    public DonatorUser(long id, String pingMessage){
        this.id = id;
        this.pingMessage = pingMessage;

        logger.info("Added DonatorUser with id " + id + " and ping message " + pingMessage);
    }

    public DonatorUser(long id){
        this.id = id;
        this.pingMessage = null;

        logger.info("Added DonatorUser with id " + id);
    }

    public void save(){
        logger.info("Saving donator with id + " + id);

        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.UPDATE_DONATOR).setLong(1, id).setString(2, pingMessage).and(3);
            qe.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

    public void purge(){
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.PURGE_DONATOR_DATA).setLong(1, id);
            qe.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
        try {
            qe = new QueryExecutor(Query.DELETE_DONATOR).setLong(1, id);
            qe.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            qe.close();
        }
    }

}
