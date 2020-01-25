package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Settings for users.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
@Getter
public enum UserSetting {

    LEVEL_UP_NOTIFY    ("lvl_up_notify",      "This defines how you get notified about you leveling up. You can choose between \"REACTION\", \"MESSAGE\", \"DM\" and \"NOTHING\".", ValueType.STRING,  "REACTION", false),
    TRACK_ME           ("track_me",           "Defines if the bot will track your activity and stats. Turning off PAUSES progress.",                                                ValueType.BOOLEAN, "true",     false),
    STATS_PRIVATE      ("stats_private",      "This will define if your stats are private, others will not be able to view your progress without you using the command.",           ValueType.BOOLEAN, "false",    false),
    MENTION_ME         ("mention_me",         "This will define if you will be mentioned in useless commands",                                                                      ValueType.BOOLEAN, "false",    false),
    MINIGAME_REMINDERS ("minigame_reminders", "Defines if you want to be reminded about pending minigames.",                                                                        ValueType.BOOLEAN, "true",     false); //TODO

    private String dbReference;
    private String description;
    private ValueType type;
    private String defaultValue;
    private boolean allowSpaces;

    UserSetting(String dbReference, String description, ValueType type, String defaultValue, boolean allowSpaces){
        this.dbReference = dbReference;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
        this.allowSpaces = allowSpaces;
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
            rs = qe.executeQuery();
            while(rs.next()){
                settings.add(rs.getString("setting_name"));
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
