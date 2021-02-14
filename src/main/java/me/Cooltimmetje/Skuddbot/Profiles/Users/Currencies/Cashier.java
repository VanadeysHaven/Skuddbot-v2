package me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Exceptions.InsufficientBalanceException;
import me.Cooltimmetje.Skuddbot.Exceptions.InvalidBetException;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;

/**
 * The cashier is responsible of converting strings of text into the actual bet.
 * It also checks whether the user has enough currency and deducts the amount from their balance.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.3
 */
public class Cashier {

    @Getter private SkuddUser user;
    @Getter private Currency currency;

    public Cashier(SkuddUser user, Currency currency) {
        this.user = user;
        this.currency = currency;
    }

    public int placeBet(String betStr) throws InvalidBetException, InsufficientBalanceException {
        int bet = formatBet(betStr);
        if(!user.getCurrencies().hasEnoughBalance(currency, bet)) throw new InsufficientBalanceException(currency, bet);
        user.getCurrencies().incrementInt(currency, bet * -1);

        return bet;
    }

    protected int formatBet(String betStr) throws InvalidBetException {
        betStr = betStr.toLowerCase();
        if(MiscUtils.isInt(betStr)) {
            int bet = Integer.parseInt(betStr);
            if (bet < 0) throw new InvalidBetException(betStr);

            return bet;
        }

        if(betStr.equals("") || betStr.equals("bet")) return user.getSettings().getInt(UserSetting.DEFAULT_BET);
        if(betStr.equals("all")) return user.getCurrencies().getInt(currency);
        if(betStr.equals("half")) return user.getCurrencies().getInt(currency) / 2;
        if(betStr.endsWith("%")) return formatPercentage(betStr);
        if(betStr.endsWith("k")) return formatThousand(betStr);

        throw new InvalidBetException(betStr);
    }

    private int formatPercentage(String betStr) throws InvalidBetException {
        betStr = betStr.substring(0, betStr.length() - 1);
        if(!MiscUtils.isInt(betStr)) throw new InvalidBetException(betStr);
        int percentage = Integer.parseInt(betStr);
        if(percentage < 1 || percentage > 100) throw new InvalidBetException(betStr);

        return (user.getCurrencies().getInt(currency) / 100) * percentage;
    }

    private int formatThousand(String betStr) throws InvalidBetException {
        betStr = betStr.substring(0, betStr.length() - 1);
        if(MiscUtils.isInt(betStr)) {
            int thousand = Integer.parseInt(betStr);
            if (thousand < 1 || thousand > 2147483) throw new InvalidBetException(betStr);
            return thousand * 1000;
        } else if(MiscUtils.isDouble(betStr)) {
            double thousand = Double.parseDouble(betStr);
            if (thousand < 1 || thousand > 2147483) throw new InvalidBetException(betStr);
            return (int) (thousand * 1000);
        } else throw new InvalidBetException(betStr);
    }


}
