package me.Cooltimmetje.Skuddbot.Donator;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        AI_NAME           ("ai_name",           64 ),
        BACON             ("bacon",             512),
        CAKE              ("cake",              512),
        KITTY             ("kitty",             512),
        PLAYING           ("playing",           128),
        PLAYING_CHRISTMAS ("playing_christmas", 128),
        PLAYING_NEW_YEAR  ("playing_new_year",  128),
        PUPPY             ("puppy",             512);

        private String dbReference;
        private int maxLength;

        Type(String dbReference, int maxLength){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
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

    public DonatorMessage(DonatorUser owner, Type type, String message){
        this.owner = owner;
        this.type = type;
        this.message = message;

        logger.info("Added DonatorMessage with owner id " + owner.getId() + ", type " + type + " and message " + message);
    }

}
