package me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Database.Query;
import me.VanadeysHaven.Skuddbot.Database.QueryExecutor;
import me.VanadeysHaven.Skuddbot.Database.QueryResult;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.ValueType;
import me.VanadeysHaven.Skuddbot.Profiles.DataContainers.Data;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Enum for all the types of currencies.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.13
 * @since 2.1.1
 */
@Getter
public enum Currency implements Data {

    SKUDDBUX         ("skuddbux",         ValueType.INTEGER, "Skuddbux",         "skuddbux",              "0", true,  true, true,  true ),
    GIVEAWAY_TICKETS ("giveaway_tickets", ValueType.INTEGER, "Giveaway Tickets", "tickets",               "0", false, true, false, false),
    PRIDE_FLAGS      ("pride_flags",      ValueType.INTEGER, "Pride Flags",      Emoji.PRIDE_FLAG.getUnicode(), "0", false, true, false, false);

    private String dbReference;
    private ValueType type;
    private String name;
    private String suffix;
    private String defaultValue;
    private boolean hasLeaderboard;
    private boolean show;
    private boolean showWhenZero;
    private boolean canBeEdited;

    Currency(String dbReference, ValueType type, String name, String suffix, String defaultValue, boolean hasLeaderboard, boolean show, boolean showWhenZero, boolean canBeEdited){
        this.dbReference = dbReference;
        this.type = type;
        this.name = name;
        this.suffix = suffix;
        this.defaultValue = defaultValue;
        this.hasLeaderboard = hasLeaderboard;
        this.show = show;
        this.showWhenZero = showWhenZero;
        this.canBeEdited = canBeEdited;
    }

    public static Currency getByDbReference(String reference){
        for(Currency currency : values())
            if(reference.equals(currency.getDbReference()))
                return currency;

            return null;
    }

    public static void saveToDatabase(){
        QueryExecutor qe = null;
        ArrayList<String> currencies = new ArrayList<>();
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_CURRENCIES);
            QueryResult qr = qe.executeQuery();
            while (qr.nextResult()){
                currencies.add(qr.getString("currency_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(qe != null) qe.close();
        }
        for(Currency currency : values()){
            if(currencies.contains(currency.getDbReference())) continue;
            try {
                qe = new QueryExecutor(Query.INSERT_CURRENCY).setString(1, currency.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(qe != null) qe.close();
            }
        }
    }

    public static String formatCurrencies(){
        StringBuilder sb = new StringBuilder();
        for(Currency currency : values())
            sb.append(" | `").append(currency.toString()).append("`");

        return sb.toString().substring(3);
    }

    @Override
    public String getTechnicalName() {
        return this.toString();
    }

    @Override
    public String getTerminology() {
        return "currency";
    }

    @Override
    public boolean hasBound() {
        return false;
    }

    @Override
    public boolean checkBound(int i) {
        return true;
    }

    @Override
    public int getMinBound() {
        return -1;
    }

    @Override
    public int getMaxBound() {
        return -1;
    }

    @Override
    public boolean hasCooldown() {
        return false;
    }

    @Override
    public int getCooldown() {
        return -1;
    }

    @Override
    public Query getUpdateQuery() {
        return Query.UPDATE_CURRENCY_VALUE;
    }

    @Override
    public Query getDeleteQuery() {
        return Query.DELETE_CURRENCY_VALUE;
    }

}
