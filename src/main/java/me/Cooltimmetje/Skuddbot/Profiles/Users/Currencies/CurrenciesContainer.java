package me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies;

import me.Cooltimmetje.Skuddbot.Profiles.DataContainers.UserDataContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

/**
 * Container for holding currencies.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3
 * @since 2.1.1
 */
public class CurrenciesContainer extends UserDataContainer<Currency> {

    public CurrenciesContainer(Identifier id, CurrenciesSapling sapling){
        super(id);
        processCurrenciesSapling(sapling);
    }

    private void processCurrenciesSapling(CurrenciesSapling sapling){
        for(Currency currency : Currency.values()){
            String value = sapling.getCurrency(currency);
            if(value != null) {
                setString(currency, value, false, true);
            } else {
                setString(currency, currency.getDefaultValue(), false, true);
            }
        }
    }

    public boolean hasEnoughBalance(Currency currency, int amount){
        return getInt(currency) >= amount;
    }

    public Cashier getCashier(Currency currency){
        return new Cashier(getId().asServerMember().asSkuddUser(), currency);
    }

//    @Override
//    public String toString(){
//        TableArrayGenerator tag = new TableArrayGenerator(new TableRow("Currency", "Amount"));
//        for(Currency currency : Currency.values())
//            tag.addRow(new TableRow(currency.toString(), getString(currency)));
//
//        return new TableDrawer(tag).drawTable();
//    }




}
