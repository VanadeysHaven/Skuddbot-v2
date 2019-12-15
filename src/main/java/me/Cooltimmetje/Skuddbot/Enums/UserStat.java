package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Constants for user stats.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
@Getter
public enum UserStat {

    XP("xp", ValueType.INTEGER, "Experience", "xp", "0", true, StatCategory.NO_CATEGORY),
    CHALLENGE_WINS("chlng_wins", ValueType.INTEGER, "Challenge wins", "0", "wins", true, StatCategory.CHALLENGE),
    CHALLENGE_LOSSES("chlng_losses", ValueType.INTEGER, "Challenge losses", "losses", "0",true, StatCategory.CHALLENGE),
    CHALLENGE_WINSTREAK("chlng_winstreak", ValueType.INTEGER, "Challenge winstreak", "Wins", "0", true, StatCategory.CHALLENGE),
    CHALLENGE_LONGEST_WINSTREAK(),
    FFA_WINS(),
    FFA_LOSSES(),
    FFA_HIGHEST_WIN(),
    FFA_KILLS(),
    BJ_WINS(),
    BJ_LOSSES(),
    BJ_PUSHES(),
    BJ_TWENTY_ONES(),
    TD_WINS(),
    TD_LOSSES(),
    TD_DEFENSES(),
    TD_HIGHEST_WIN(),
    TD_ALL_SURVIVED(),
    TD_KILLS(),
    TD_TEAMMATES();

    private String dbReference;
    private ValueType type;
    private String name;
    private String suffix;
    private String defaultValue;
    private boolean hasLeaderboard;
    private StatCategory category;

    UserStat(String dbReference, ValueType type, String name, String suffix, String defaultValue, boolean hasLeaderboard, StatCategory category){
        this.dbReference = dbReference;
        this.type = type;
        this.name = name;
        this.suffix = suffix;
        this.defaultValue = defaultValue;
        this.hasLeaderboard = hasLeaderboard;
        this.category = category;
    }

    public static UserStat getByDbReference(String reference){
        for(UserStat stat : values())
            if(reference.equals(stat.getDbReference()))
                return stat;

        return null;
    }

}
