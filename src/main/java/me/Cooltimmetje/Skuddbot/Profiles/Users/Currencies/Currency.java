package me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Database.QueryResult;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Enum for all the types of currencies.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
@Getter
public enum Currency {

    SKUDDBUX ("skuddbux", ValueType.INTEGER, "Skuddbux", "", "0", true, true, true);

    private String dbReference;
    private ValueType type;
    private String name;
    private String suffix;
    private String defaultValue;
    private boolean hasLeaderboard;
    private boolean show;
    private boolean canBeEdited;

    Currency(String dbReference, ValueType type, String name, String suffix, String defaultValue, boolean hasLeaderboard, boolean show, boolean canBeEdited){
        this.dbReference = dbReference;
        this.type = type;
        this.name = name;
        this.suffix = suffix;
        this.defaultValue = defaultValue;
        this.hasLeaderboard = hasLeaderboard;
        this.show = show;
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

}
