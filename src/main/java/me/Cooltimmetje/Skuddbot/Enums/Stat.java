package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Constants for user stats.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
@Getter
public enum Stat {

    EXPERIENCE                  ("xp",                      ValueType.INTEGER, "Experience",             "xp",        "0",  true,  Category.NO_CATEGORY,     true ),
    CHALLENGE_WINS              ("chlng_wins",              ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  Category.CHALLENGE,       true ),
    CHALLENGE_LOSSES            ("chlng_losses",            ValueType.INTEGER, "Losses",                 "Losses",    "0",  true,  Category.CHALLENGE,       true ),
    CHALLENGE_WINSTREAK         ("chlng_winstreak",         ValueType.INTEGER, "Winstreak",              "wins",      "0",  true,  Category.CHALLENGE,       true ),
    CHALLENGE_LONGEST_WINSTREAK ("chlng_longest_winstreak", ValueType.INTEGER, "Longest winstreak",      "wins",      "0",  true,  Category.CHALLENGE,       true ),
    FFA_WINS                    ("ffa_wins",                ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  Category.FREE_FOR_ALL,    true ),
    FFA_LOSSES                  ("ffa_losses",              ValueType.INTEGER, "Losses",                 "losses",    "0",  true,  Category.FREE_FOR_ALL,    true ),
    FFA_HIGHEST_WIN             ("ffa_highest_win",         ValueType.INTEGER, "Most entrants win",      "entrants",  "0",  true,  Category.FREE_FOR_ALL,    true ),
    FFA_KILLS                   ("ffa_kills",               ValueType.INTEGER, "Kills",                  "kills",     "0",  true,  Category.FREE_FOR_ALL,    true ),
    BJ_WINS                     ("bj_wins",                 ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  Category.BLACKJACK,       true ),
    BJ_LOSSES                   ("bj_losses",               ValueType.INTEGER, "Losses",                 "losses",    "0",  true,  Category.BLACKJACK,       true ),
    BJ_PUSHES                   ("bj_pushes",               ValueType.INTEGER, "Pushes",                 "pushes",    "0",  true,  Category.BLACKJACK,       true ),
    BJ_TWENTY_ONES              ("bj_twenty_ones",          ValueType.INTEGER, "21's",                   "21's",      "0",  true,  Category.BLACKJACK,       true ),
    TD_WINS                     ("td_wins",                 ValueType.INTEGER, "Wins",                   "wins",      "0",  true,  Category.TEAM_DEATHMATCH, true ),
    TD_LOSSES                   ("td_losses",               ValueType.INTEGER, "Losses",                 "losses",    "0",  true,  Category.TEAM_DEATHMATCH, true ),
    TD_DEFENSES                 ("td_defences",             ValueType.INTEGER, "Teammate defences",      "defences",  "0",  true,  Category.TEAM_DEATHMATCH, true ),
    TD_HIGHEST_WIN              ("td_highest_win",          ValueType.INTEGER, "Most entrants win",      "entrants",  "0",  true,  Category.TEAM_DEATHMATCH, true ),
    TD_ALL_SURVIVED             ("td_all_survived",         ValueType.INTEGER, "All teammates survived", "times",     "0",  true,  Category.TEAM_DEATHMATCH, true ),
    TD_KILLS                    ("td_kills",                ValueType.INTEGER, "Kills",                  "kills",     "0",  true,  Category.TEAM_DEATHMATCH, true ),
    TD_TEAMMATES                ("td_teammates",            ValueType.JSON,    "Favourite Teammate",     "",          "{}", false, Category.TEAM_DEATHMATCH, false);

    private String dbReference;
    private ValueType type;
    private String name;
    private String suffix;
    private String defaultValue;
    private boolean hasLeaderboard;
    private Category category;
    private boolean canBeEdited;

    Stat(String dbReference, ValueType type, String name, String suffix, String defaultValue, boolean hasLeaderboard, Category category, boolean canBeEdited){
        this.dbReference = dbReference;
        this.type = type;
        this.name = name;
        this.suffix = suffix;
        this.defaultValue = defaultValue;
        this.hasLeaderboard = hasLeaderboard;
        this.category = category;
        this.canBeEdited = canBeEdited;
    }

    public static Stat getByDbReference(String reference){
        for(Stat stat : values())
            if(reference.equals(stat.getDbReference()))
                return stat;

        return null;
    }

    public static void saveToDatabase(){
        QueryExecutor qe = null;
        ResultSet rs;
        ArrayList<String> stats = new ArrayList<>();
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_STATS);
            rs = qe.executeQuery();
            while(rs.next()){
                stats.add(rs.getString("stat_name"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(qe != null) qe.close();
        }
        for(Stat stat : values()){
            if(stats.contains(stat.getDbReference())) continue;
            try {
                qe = new QueryExecutor(Query.INSERT_STAT).setString(1, stat.getDbReference());
                qe.execute();
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if(qe != null) qe.close();
            }
        }
    }

    @Getter
    public enum Category {
        NO_CATEGORY     (""               ),
        CHALLENGE       ("Challenge"      ),
        FREE_FOR_ALL    ("Free For All"   ),
        BLACKJACK       ("Blackjack"      ),
        TEAM_DEATHMATCH ("Team Deathmatch");

        private String name;

        Category(String name){
            this.name = name;
        }
    }

}
