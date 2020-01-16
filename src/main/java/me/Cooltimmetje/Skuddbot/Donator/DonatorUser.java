package me.Cooltimmetje.Skuddbot.Donator;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A donator user.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class DonatorUser {

    private static final Logger logger = LoggerFactory.getLogger(DonatorUser.class);

    @Getter private long id;
    @Getter private String pingMessage;


    public DonatorUser(long id, String pingMessage){
        this.id = id;
        this.pingMessage = pingMessage;

        logger.info("Added DonatorUser with id " + id + " and ping message " + pingMessage);
    }

}
