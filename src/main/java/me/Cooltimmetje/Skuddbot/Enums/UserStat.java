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

    XP                          ("xp",                      ValueType.INTEGER, "Experience",             "xp",        "0",  true,  StatCategory.NO_CATEGORY    ),
    CHALLENGE_WINS              ("chlng_wins",              ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  StatCategory.CHALLENGE      ),
    CHALLENGE_LOSSES            ("chlng_losses",            ValueType.INTEGER, "Losses",                 "Losses",    "0",  true,  StatCategory.CHALLENGE      ),
    CHALLENGE_WINSTREAK         ("chlng_winstreak",         ValueType.INTEGER, "Winstreak",              "wins",      "0",  true,  StatCategory.CHALLENGE      ),
    CHALLENGE_LONGEST_WINSTREAK ("chlng_longest_winstreak", ValueType.INTEGER, "Longest winstreak",      "wins",      "0",  true,  StatCategory.CHALLENGE      ),
    FFA_WINS                    ("ffa_wins",                ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  StatCategory.FREE_FOR_ALL   ),
    FFA_LOSSES                  ("ffa_losses",              ValueType.INTEGER, "Losses",                 "losses",    "0",  true,  StatCategory.FREE_FOR_ALL   ),
    FFA_HIGHEST_WIN             ("ffa_highest_win",         ValueType.INTEGER, "Most entrants win",      "entrants",  "0",  true,  StatCategory.FREE_FOR_ALL   ),
    FFA_KILLS                   ("ffa_kills",               ValueType.INTEGER, "Kills",                  "kills",     "0",  true,  StatCategory.FREE_FOR_ALL   ),
    BJ_WINS                     ("bj_wins",                 ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  StatCategory.BLACKJACK      ),
    BJ_LOSSES                   ("bj_losses",               ValueType.INTEGER, "Losses",                 "losses",    "0",  true,  StatCategory.BLACKJACK      ),
    BJ_PUSHES                   ("bj_pushes",               ValueType.INTEGER, "Pushes",                 "pushes",    "0",  true,  StatCategory.BLACKJACK      ),
    BJ_TWENTY_ONES              ("bj_twenty_ones",          ValueType.INTEGER, "21's",                   "21's",      "0",  true,  StatCategory.BLACKJACK      ),
    TD_WINS                     ("td_wins",                 ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  StatCategory.TEAM_DEATHMATCH),
    TD_LOSSES                   ("td_losses",               ValueType.INTEGER, "Losses",                 "losses",    "0",  true,  StatCategory.TEAM_DEATHMATCH),
    TD_DEFENSES                 ("td_defences",             ValueType.INTEGER, "Teammate defences",      "defences",  "0",  true,  StatCategory.TEAM_DEATHMATCH),
    TD_HIGHEST_WIN              ("td_highest_win",          ValueType.INTEGER, "Most entrants win",      "entrants",  "0",  true,  StatCategory.TEAM_DEATHMATCH),
    TD_ALL_SURVIVED             ("td_all_survived",         ValueType.INTEGER, "All teammates survived", "times",     "0",  true,  StatCategory.TEAM_DEATHMATCH),
    TD_KILLS                    ("td_kills",                ValueType.INTEGER, "Kills",                  "kills",     "0",  true,  StatCategory.TEAM_DEATHMATCH),
    TD_TEAMMATES                ("td_teammates",            ValueType.JSON,    "Favourite Teammate",     "",          "{}", false, StatCategory.TEAM_DEATHMATCH);

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
