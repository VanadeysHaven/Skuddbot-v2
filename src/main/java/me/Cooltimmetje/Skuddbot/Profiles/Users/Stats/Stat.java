package me.Cooltimmetje.Skuddbot.Profiles.Users.Stats;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Constants for user stats.
 *
 * @author Tim (Cooltimmetje)
 * @since 2.2.1
 * @version 2.0
 */
@Getter
public enum Stat {

    EXPERIENCE                  ("xp",                      ValueType.INTEGER, "Experience",                 "xp",           "0",  Category.NO_CATEGORY,       true,  true,  true ),
    CHALLENGE_WINS              ("chlng_wins",              ValueType.INTEGER, "Wins",                       "wins",         "0",  Category.CHALLENGE,         true,  true,  true ),
    CHALLENGE_LOSSES            ("chlng_losses",            ValueType.INTEGER, "Losses",                     "Losses",       "0",  Category.CHALLENGE,         true,  true,  true ),
    CHALLENGE_WINSTREAK         ("chlng_winstreak",         ValueType.INTEGER, "Winstreak",                  "wins",         "0",  Category.CHALLENGE,         true,  true,  true ),
    CHALLENGE_LONGEST_WINSTREAK ("chlng_longest_winstreak", ValueType.INTEGER, "Longest winstreak",          "wins",         "0",  Category.CHALLENGE,         true,  true,  true ),
    CHALLENGE_BETS_WON          ("chlng_bets_won",          ValueType.INTEGER, "Bets won",                   "bets",         "0",  Category.CHALLENGE,         true,  true,  true ),
    CHALLENGE_BETS_LOST         ("chlng_bets_lost",         ValueType.INTEGER, "Bets lost",                  "bets",         "0",  Category.CHALLENGE,         true,  true,  true ),
    FFA_WINS                    ("ffa_wins",                ValueType.INTEGER, "Wins",                       "wins",         "0",  Category.FREE_FOR_ALL,      true,  true,  true ),
    FFA_LOSSES                  ("ffa_losses",              ValueType.INTEGER, "Losses",                     "losses",       "0",  Category.FREE_FOR_ALL,      true,  true,  true ),
    FFA_HIGHEST_WIN             ("ffa_highest_win",         ValueType.INTEGER, "Most entrants win",          "entrants",     "0",  Category.FREE_FOR_ALL,      true,  true,  true ),
    FFA_KILLS                   ("ffa_kills",               ValueType.INTEGER, "Kills",                      "kills",        "0",  Category.FREE_FOR_ALL,      true,  true,  true ),
    FFA_BETS_WON                ("ffa_bets_won",            ValueType.INTEGER, "Bets won",                   "bets",         "0",  Category.FREE_FOR_ALL,      true,  true,  true ),
    FFA_BETS_LOST               ("ffa_bets_lost",           ValueType.INTEGER, "Bets lost",                  "bets",         "0",  Category.FREE_FOR_ALL,      true,  true,  true ),
    BJ_WINS                     ("bj_wins",                 ValueType.INTEGER, "Wins",                       "hands won",    "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_LOSSES                   ("bj_losses",               ValueType.INTEGER, "Losses",                     "hands lost",   "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_PUSHES                   ("bj_pushes",               ValueType.INTEGER, "Pushes",                     "hands pushed", "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_TWENTY_ONES              ("bj_twenty_ones",          ValueType.INTEGER, "21's",                       "21's",         "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_BLACKJACKS               ("bj_blackjacks",           ValueType.INTEGER, "Blackjack's",                "blackjacks",   "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_DD_WINS                  ("bj_dd_wins",              ValueType.INTEGER, "Double Down Wins",           "hands won",    "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_DD_PUSHES                ("bj_dd_pushes",            ValueType.INTEGER, "Double Down Pushes",         "hands pushed", "0",  Category.BLACKJACK,         true,  true,  true ),
    BJ_DD_LOSSES                ("bj_dd_losses",            ValueType.INTEGER, "Double Down Losses",         "hands lost",   "0",  Category.BLACKJACK,         true,  true,  true ),
    TD_WINS                     ("td_wins",                 ValueType.INTEGER, "Wins",                       "wins",         "0",  Category.TEAM_DEATHMATCH,   true,  true,  true ),
    TD_LOSSES                   ("td_losses",               ValueType.INTEGER, "Losses",                     "losses",       "0",  Category.TEAM_DEATHMATCH,   true,  true,  true ),
    TD_DEFENSES                 ("td_defences",             ValueType.INTEGER, "Teammate defences",          "defences",     "0",  Category.TEAM_DEATHMATCH,   true,  true,  true ),
    TD_HIGHEST_WIN              ("td_highest_win",          ValueType.INTEGER, "Most entrants win",          "entrants",     "0",  Category.TEAM_DEATHMATCH,   true,  true,  true ),
    TD_ALL_SURVIVED             ("td_all_survived",         ValueType.INTEGER, "All teammates survived",     "times",        "0",  Category.TEAM_DEATHMATCH,   true,  true,  true ),
    TD_KILLS                    ("td_kills",                ValueType.INTEGER, "Kills",                      "kills",        "0",  Category.TEAM_DEATHMATCH,   true,  true,  true ),
    TD_TEAMMATES                ("td_teammates",            ValueType.JSON,    "Favourite Teammate",         "",             "{}", Category.TEAM_DEATHMATCH,   false, false, true ),
    DAILY_LAST_CLAIM            ("daily_last_claim",        ValueType.LONG,    "daily_last_claim",           "",             "-1", Category.DAILY_BONUS,       false, true,  false),
    DAILY_CURRENT_STREAK        ("daily_current_streak",    ValueType.INTEGER, "Current claim streak",       "days",         "0",  Category.DAILY_BONUS,       true,  true,  true ),
    DAILY_LONGEST_STREAK        ("daily_longest_streak",    ValueType.INTEGER, "Longest claim steak",        "days",         "0",  Category.DAILY_BONUS,       true,  true,  true ),
    DON_WINS                    ("don_wins",                ValueType.INTEGER, "Wins",                       "wins",         "0",  Category.DOUBLE_OR_NOTHING, true,  true,  true ),
    DON_LOSSES                  ("don_losses",              ValueType.INTEGER, "Losses",                     "losses",       "0",  Category.DOUBLE_OR_NOTHING, true,  true,  true ),
    DON_LONGEST_STREAK          ("don_longest_streak",      ValueType.INTEGER, "Longest double up streak",   "times",        "0",  Category.DOUBLE_OR_NOTHING, true,  true,  true );

    private String dbReference;
    private ValueType type;
    private String name;
    private String suffix;
    private String defaultValue;
    private Category category;
    private boolean hasLeaderboard;
    private boolean canBeEdited;
    private boolean show;

    Stat(String dbReference, ValueType type, String name, String suffix, String defaultValue, Category category, boolean hasLeaderboard, boolean canBeEdited, boolean show){
        this.dbReference = dbReference;
        this.type = type;
        this.name = name;
        this.suffix = suffix;
        this.defaultValue = defaultValue;
        this.hasLeaderboard = hasLeaderboard;
        this.category = category;
        this.canBeEdited = canBeEdited;
        this.show = show;
    }

    public static Stat getByDbReference(String reference){
        for(Stat stat : values())
            if(reference.equals(stat.getDbReference()))
                return stat;

        return null;
    }

    public static ArrayList<Stat> getByCategory(Category category){
        ArrayList<Stat> stats = new ArrayList<>();

        for(Stat stat : values())
            if(stat.getCategory() == category)
                stats.add(stat);

        return stats;
    }

    public static void setup(){
        saveToDatabase();
        StatPageManager.getInstance().calculate();
    }

    private static void saveToDatabase(){
        QueryExecutor qe = null;
        ArrayList<String> stats = new ArrayList<>();
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_STATS);
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()){
                stats.add(qr.getString("stat_name"));
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

    public static String formatStats(){
        StringBuilder sb = new StringBuilder();

        for(Category category : Category.values()){
            if(!category.isShow()) continue;
            sb.append("`").append(MiscUtils.unEnumify(category.toString())).append("`: ");
            StringBuilder sb1 = new StringBuilder();
            for(Stat stat : values()){
                if(!stat.isShow()) continue;
                if(stat.getCategory() == category && stat.isHasLeaderboard()){
                    sb1.append(" | `").append(MiscUtils.unEnumify(stat.toString())).append("`");
                }
            }
            sb.append(sb1.append("\n").substring(3));
        }

        return sb.toString().trim();
    }

    @Getter
    public enum Category {
        NO_CATEGORY       ("Not categorized",     true ),
        CHALLENGE         ("Challenge",           true ),
        FREE_FOR_ALL      ("Free for All",        true ),
        BLACKJACK         ("Blackjack",           true ),
        TEAM_DEATHMATCH   ("Team Deathmatch",     false),
        DAILY_BONUS       ("Daily Bonus",         true ),
        DOUBLE_OR_NOTHING ("Double or Nothing",   true );

        private String name;
        private boolean show;

        Category(String name, boolean show){
            this.name = name;
            this.show = show;
        }

        public ArrayList<Stat> getAll(){
            return getByCategory(this);
        }

    }

}
