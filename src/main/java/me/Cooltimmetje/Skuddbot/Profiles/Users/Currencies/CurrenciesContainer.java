package me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Container for holding currencies.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.1.1
 */
public class CurrenciesContainer {

    private static final ServerManager sm = new ServerManager();
    private static final ProfileManager pm = new ProfileManager();

    private Identifier id;
    private HashMap<Currency,String> currencies;

    public CurrenciesContainer(Identifier id, CurrenciesSapling sapling){
        this.id = id;
        this.currencies = new HashMap<>();
        processStatsSapling(sapling);
    }

    private void processStatsSapling(CurrenciesSapling sapling){
        for(Currency currency : Currency.values()){
            setString(currency, sapling.getCurrency(currency), true);
        }
    }

    public void setString(Currency currency, String value){
        setString(currency, value, false);
    }

    public void setString(Currency currency, String value, boolean load){
        if(!checkType(value, currency)) throw new IllegalArgumentException("Value " + value + " is unsuitable for currency " + currency + "; not of type" + currency.getType());
        if(!load && !pm.getUser(id).getSettings().getBoolean(UserSetting.TRACK_ME)) return;
        this.currencies.put(currency, value);
        if(!load) save(currency);
    }

    public String getString(Currency currency){
        return this.currencies.get(currency);
    }

    public void setInt(Currency currency, int value){
        setString(currency, value+"");
    }

    public int getInt(Currency currency){
        if(currency.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        return Integer.parseInt(getString(currency));
    }

    public void incrementInt(Currency currency){
        if(currency.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        incrementInt(currency, 1);
    }

    public void incrementInt(Currency currency, int incrementBy){
        if(currency.getType() != ValueType.INTEGER) throw new IllegalArgumentException("Stat is not of type INTEGER");
        setInt(currency, getInt(currency) + incrementBy);
    }

    public void save(){
        for(Currency currency : Currency.values())
            save(currency);
    }

    public void save(Currency currency){
        QueryExecutor qe = null;
        if(getString(currency).equals(currency.getDefaultValue())){
            try {
                qe = new QueryExecutor(Query.DELETE_CURRENCY_VALUE).setInt(1, id.getId()).setString(2, currency.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(qe != null) qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(Query.UPDATE_CURRENCY_VALUE).setString(1, currency.getDbReference()).setInt(2, id.getId()).setString(3, getString(currency)).and(4);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(qe != null) qe.close();
            }
        }
    }

    private boolean checkType(String input, Currency currency){
        ValueType type = currency.getType();
        if(type == ValueType.INTEGER){
            return MiscUtils.isInt(input);
        }

        return type == ValueType.STRING;
    }

    @Override
    public String toString(){
        TableArrayGenerator tag = new TableArrayGenerator(new TableRow("Currency", "Amount"));
        for(Currency currency : Currency.values())
            tag.addRow(new TableRow(currency.toString(), getString(currency)));

        return new TableDrawer(tag).drawTable();
    }




}
