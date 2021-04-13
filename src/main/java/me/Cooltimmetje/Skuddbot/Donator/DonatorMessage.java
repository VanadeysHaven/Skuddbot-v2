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
 * @version 2.3.03
 * @since 2.0
 */
public class DonatorMessage {

    private static final Logger logger = LoggerFactory.getLogger(DonatorMessage.class);
    private static RNGManager random = new RNGManager();

    @Getter
    public enum Type {

        /* Strings */
        AI_NAME           ("ai_name",           64 ),
        PLAYING           ("playing",           128),
        PLAYING_CHRISTMAS ("playing_christmas", 128),
        PLAYING_NEW_YEAR  ("playing_new_year",  128),

        /* Images */
        BACON             ("bacon",             512, Emoji.BACON,   true, "mmm... Bacon... *drools*",                    "bacon"),
        BAT               ("bat",               512, Emoji.BAT,     true, "For when you need a sky pupper in your life", "bat", "skypupper"),
        BUNNY             ("bunny",             512, Emoji.RABBIT,  true, "Bun bun",                                     "bunny", "bune", "rabbit", "bun", "bunbun", "boopersnoot",  "flufferbutt", "floppetyhoppety", "wigglesniffers"),
        CAKE              ("cake",              512, Emoji.CAKE,    true, "CAKE! HAPPY BIRTHDAY!",                       "cake"),
        GUINEA_PIG        ("guinea_pig",        512, Emoji.HAMSTER, true, "Cute balls of flooooffffff",                  "gp", "guinea", "piggie", "guineapigs", "guineapiggie", "piggies", "wheek", "guineapig"), //Yes I know, hamster emoji, leave me alone
        KITTY             ("kitty",             512, Emoji.CAT,     true, "Kittttieeessss",                              "kitty", "cat", "pussy", "kitten"),
        OWL               ("owl",               512, Emoji.OWL,     true, "Hoot hoot!",                                  "owl", "hoots", "hoot"),
        PANDA             ("panda",             512, Emoji.PANDA,   true, "Deadly balls of floof",                       "panda"),
        PUPPY             ("puppy",             512, Emoji.DOG,     true, "PUPPERRRRRRRRR",                              "puppy", "emergencypuppy", "wuff", "dogger", "doggo", "dog", "pupper", "riit", "rogged", "woowoo", "dogo", "dogggo", "doogo", "dogoo", "owo", "doggerino", "addit", "doggy", "defectius"),
        SEAL              ("seal",              512, Emoji.SEAL,    true, "Seal of approval",                            "seal"),
        SNAKE             ("snake",             512, Emoji.SNAKE,   true, "Snek",                                        "snake", "snek", "dangernoodle", "murderspagurders", "dangernoodles", "cautionramen", "hazardspaghetti", "slipperytubedudes", "noperopes", "sneksdosneaks", "stringsdostings", "linguinisdomeanies");

        private String dbReference;
        private int maxLength;
        private Emoji emoji;
        private boolean acceptsImages;
        private String commandDescription;
        private String[] commands;

        Type(String dbReference, int maxLength){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
            this.acceptsImages = false;
        }

        Type(String dbReference, int maxLength, Emoji emoji, boolean acceptsImages, String commandDescription, String... commands){
            this.dbReference = dbReference;
            this.maxLength = maxLength;
            this.emoji = emoji;
            this.acceptsImages = acceptsImages;
            this.commandDescription = commandDescription;
            this.commands = commands;
        }

        public static Type getByDbReference(String reference){
            for(Type type : values())
                if(reference.equals(type.getDbReference()))
                    return type;

            return null;
        }

        private static void saveToDatabase(){ //TODO
//            QueryExecutor qe = null;
//            ArrayList<String> stats = new ArrayList<>();
//            try {
//                qe = new QueryExecutor(Query.SELECT_ALL_STATS);
//                QueryResult qr = qe.executeQuery();
//                while(qr.nextResult()){
//                    stats.add(qr.getString("stat_name"));
//                }
//            } catch (SQLException e){
//                e.printStackTrace();
//            } finally {
//                if(qe != null) qe.close();
//            }
//            for(Stat stat : values()){
//                if(stats.contains(stat.getDbReference())) continue;
//                try {
//                    qe = new QueryExecutor(Query.INSERT_STAT).setString(1, stat.getDbReference());
//                    qe.execute();
//                } catch (SQLException e){
//                    e.printStackTrace();
//                } finally {
//                    if(qe != null) qe.close();
//                }
//            }
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
