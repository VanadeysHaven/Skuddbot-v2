package me.Cooltimmetje.Skuddbot.Profiles.Users.Settings;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Settings for users.
 *
 * @author Tim (Cooltimmetje)
 * @since 2.2.1
 * @version 2.0
 */
@Getter
public enum UserSetting {

    LEVEL_UP_NOTIFY    ("lvl_up_notify",      "This defines how you get notified about you leveling up. You can choose between \"REACTION\", \"MESSAGE\", \"DM\" and \"NOTHING\".", ValueType.STRING,  "REACTION", false, 0                                       ),
    TRACK_ME           ("track_me",           "Defines if the bot will track your activity and stats. Turning off PAUSES progress.",                                                ValueType.BOOLEAN, "true",     false, 0                                       ),
    PROFILE_PRIVATE    ("stats_private",      "This will define if your profile are private, others will not be able to view your profile without you using the command.",          ValueType.BOOLEAN, "false",    false, 0                                       ),
    MENTION_ME         ("mention_me",         "This will define if you will be mentioned in useless commands",                                                                      ValueType.BOOLEAN, "false",    false, 0                                       ),
    MINIGAME_REMINDERS ("minigame_reminders", "Defines if you want to be reminded about pending minigames.",                                                                        ValueType.BOOLEAN, "true",     false, 0                                       ),
    DEFAULT_BET        ("default_bet",        "Defines your default bet used in various minigames",                                                                                 ValueType.INTEGER, "50",       false, 0,     0,   2147483647),
    TIMEZONE           ("timezone",           "Defines the offset in various timebound activities.",                                                                                ValueType.INTEGER, "0",        false, 86400, -12, 12        );

    private String dbReference;
    private String description;
    private ValueType type;
    private String defaultValue;
    private boolean allowSpaces;
    private int cooldown;
    private int minBound;
    private int maxBound;

    UserSetting(String dbReference, String description, ValueType type, String defaultValue, boolean allowSpaces, int cooldown) {
        this(dbReference, description, type, defaultValue, allowSpaces, cooldown, -99999, -99999);
    }

    UserSetting(String dbReference, String description, ValueType type, String defaultValue, boolean allowSpaces, int cooldown, int minBound, int maxBound){
        this.dbReference = dbReference;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
        this.allowSpaces = allowSpaces;
        this.cooldown = cooldown;
        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    public boolean hasBound(){
        return minBound != -99999 && maxBound != -99999;
    }

    public boolean checkBound(int check){
        if(!hasBound()) throw new IllegalStateException("This setting has no bounds.");
        return check >= minBound && check <= maxBound;
    }

    public boolean hasCooldown(){
        return cooldown > 0;
    }

    public static UserSetting getByDbReference(String reference){
        for(UserSetting setting : values())
            if(setting.getDbReference().equals(reference))
                return setting;

        return null;
    }


    public static void saveToDatabase(){
        QueryExecutor qe = null;
        ResultSet rs;
        ArrayList<String> settings = new ArrayList<>();
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_USER_SETTINGS);
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()){
                settings.add(qr.getString("setting_name"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(qe != null) qe.close();
        }
        for(UserSetting setting : values()){
            if(settings.contains(setting.getDbReference())) continue;
            try {
                qe = new QueryExecutor(Query.INSERT_USER_SETTING).setString(1, setting.getDbReference());
                qe.execute();
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if(qe != null) qe.close();
            }
        }
    }

}
