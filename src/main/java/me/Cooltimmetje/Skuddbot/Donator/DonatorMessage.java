package me.Cooltimmetje.Skuddbot.Donator;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * A donator message.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3.02
 * @since 2.0
 */
public class DonatorMessage {

    private static final Logger logger = LoggerFactory.getLogger(DonatorMessage.class);
    private static RNGManager random = new RNGManager();

    @Getter
    public enum Type {
        AI_NAME           ("ai_name",           64,                 false),
        BACON             ("bacon",             512, Emoji.BACON,   true ),
        CAKE              ("cake",              512, Emoji.CAKE,    true ),
        GUINEA_PIG        ("guinea_pig",        512, Emoji.HAMSTER, true ), //Yes I know, hamster emoji, leave me alone
        KITTY             ("kitty",             512, Emoji.CAT,     true ),
        PLAYING           ("playing",           128,                false),
        PLAYING_CHRISTMAS ("playing_christmas", 128,                false),
        PLAYING_NEW_YEAR  ("playing_new_year",  128,                false),
        PUPPY             ("puppy",             512, Emoji.DOG,     true );

        private String dbReference;
        private int maxLength;
        private Emoji emoji;
        private boolean acceptsImages;

        Type(String dbReference, int maxLength, boolean acceptsImages){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
            this.acceptsImages = acceptsImages;
        }

        Type(String dbReference, int maxLength, Emoji emoji, boolean acceptsImages){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
            this.emoji = emoji;
            this.acceptsImages = acceptsImages;
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
        return random.integer(0, 100) < 25;
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
