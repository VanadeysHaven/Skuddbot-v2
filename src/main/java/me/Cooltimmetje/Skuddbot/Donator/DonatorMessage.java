package me.Cooltimmetje.Skuddbot.Donator;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * A donator message.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class DonatorMessage {

    private static final Logger logger = LoggerFactory.getLogger(DonatorMessage.class);

    @Getter
    public enum Type {
        AI_NAME           ("ai_name",           64              ),
        BACON             ("bacon",             512, Emoji.BACON),
        CAKE              ("cake",              512, Emoji.CAKE ),
        KITTY             ("kitty",             512, Emoji.CAT  ),
        PLAYING           ("playing",           128             ),
        PLAYING_CHRISTMAS ("playing_christmas", 128             ),
        PLAYING_NEW_YEAR  ("playing_new_year",  128             ),
        PUPPY             ("puppy",             512, Emoji.DOG  );

        private String dbReference;
        private int maxLength;
        private Emoji emoji;

        Type(String dbReference, int maxLength){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
        }

        Type(String dbReference, int maxLength, Emoji emoji){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
            this.emoji = emoji;
        }

        public static Type getByDbReference(String reference){
            for(Type type : values())
                if(reference.equals(type.getDbReference()))
                    return type;

            return null;
        }
    }

    @Getter private DonatorUser owner;
    @Getter private Type type;
    @Getter private String message;
    @Getter @Setter private long lastShown;

    public DonatorMessage(DonatorUser owner, Type type, String message){
        this.owner = owner;
        this.type = type;
        this.message = message;
        lastShown = 0;

        logger.info("Added DonatorMessage with owner id " + owner.getId() + ", type " + type + " and message " + message);
    }

    public boolean isAllowed(){
        if((System.currentTimeMillis() - lastShown) > (24*60*60*1000)) return true;
        return MiscUtils.randomInt(0,100) < 25;
    }

    public void save(){
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.INSERT_DONATOR_MESSAGE).setString(1, type.getDbReference()).setLong(2, owner.getId()).setString(3, message);
            qe.execute();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

    @Override
    public String toString() {
        return message;
    }
}
