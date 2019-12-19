package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

import javax.print.DocFlavor;

/**
 * Settings for users.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
@Getter
public enum UserSettings {

    LEVEL_UP_NOTIFY    ("lvl_up_notify",      "This defines how you get notified about you leveling up. You can choose between \"REACTION\", \"MESSAGE\", \"DM\" and \"NOTHING\".", ValueType.STRING,  "REACTION"),
    TRACK_ME           ("track_me",           "Defines if the bot will track your activity and stats. Turning off PAUSES progress.",                                                ValueType.BOOLEAN, "true"),
    STATS_PRIVATE      ("stats_private",      "This will define if your stats are private, others will not be able to view your progress without you using the command.",           ValueType.BOOLEAN, "false"),
    MENTION_ME         ("mention_me",         "This will define if you will be mentioned in useless commands",                                                                      ValueType.BOOLEAN, "false"),
    MINIGAME_REMINDERS ("minigame_reminders", "Defines if you want to be reminded about pending minigames.",                                                                        ValueType.BOOLEAN, "true");

    private String dbReference;
    private String description;
    private ValueType type;
    private String defaultValue;

    UserSettings(String dbReference, String description, ValueType type, String defaultValue){
        this.dbReference = dbReference;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
    }


}
