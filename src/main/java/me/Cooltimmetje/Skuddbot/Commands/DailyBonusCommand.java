package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSettingsContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Command used to claim daily bonuses.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.1.1
 */
public class DailyBonusCommand extends Command {

    public static String MESSAGE_FORMAT = "**DAILY BONUS** | *{0}*\n\n" +
            "Daily bonus claimed:\n" +
            "+ {1} Skuddbux\n" +
            "+ {2} <:xp_icon:458325613015466004>\n" +
            "{3}";

    public DailyBonusCommand() {
        super(new String[]{"dailybonus", "claim", "db"}, "Claim your daily bonus.");
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddUser su = pm.getUser(server.getId(), message.getAuthor().getId());
        SkuddServer ss = sm.getServer(server.getId());
        ServerSettingsContainer ssc = ss.getSettings();
        StatsContainer sc = su.getStats();

        if(!canClaim(su)){
            MessagesUtils.addReaction(message, Emoji.X, "You can't claim your daily bonus yet."); //TODO: ADD TIMER
            return;
        }
        boolean lostStreak = false;
        int lastClaimStreak = -1;
        if(!isInTimeFrame(su)) {
            if (sc.getLong(Stat.DAILY_LAST_CLAIM) != -1) lostStreak = true;
            lastClaimStreak = sc.getInt(Stat.DAILY_CURRENT_STREAK);
            sc.setInt(Stat.DAILY_CURRENT_STREAK, 0);
        }


        sc.incrementInt(Stat.DAILY_CURRENT_STREAK);
        int currentStreak = sc.getInt(Stat.DAILY_CURRENT_STREAK);
        int longestStreak = sc.getInt(Stat.DAILY_LONGEST_CLAIM_STREAK);

        boolean newLongest = false;
        if(currentStreak > longestStreak){
            newLongest = true;
            sc.setInt(Stat.DAILY_LONGEST_CLAIM_STREAK, currentStreak);
            longestStreak = currentStreak;
        }


        int currencyBonusBase = ssc.getInt(ServerSetting.DAILY_CURRENCY_BONUS);
        int xpBonusBase = ssc.getInt(ServerSetting.DAILY_XP_BONUS);
        int cap = ssc.getInt(ServerSetting.DAILY_BONUS_CAP);
        double multiplier = Math.pow(ssc.getDouble(ServerSetting.DAILY_BONUS_MULTIPLIER), Math.min(currentStreak, cap) - 1);
        int currencyBonus = (int) (currencyBonusBase * multiplier);
        int xpBonus = (int) (xpBonusBase * multiplier);

        su.getCurrencies().incrementInt(Currency.SKUDDBUX, currencyBonus);
        sc.incrementInt(Stat.EXPERIENCE, xpBonus);

        String streakString = "";
        if(lostStreak)
            streakString = "**Claim streak lost:** *" + lastClaimStreak + " days*";
        else if (currentStreak == 2)
            streakString = "**Claim streak started:** *" + currentStreak + " days*";
        else if (currentStreak > 2)
            streakString = "**Claim streak continued:** *" + currentStreak + " days*";

        if(newLongest && currentStreak >= 2)
            streakString += " | **New longest streak!**";

        String msg = MessageFormat.format(MESSAGE_FORMAT, message.getAuthor().getDisplayName(), currencyBonus, xpBonus, streakString);

        MessagesUtils.sendPlain(message.getChannel(), msg);
        sc.setLong(Stat.DAILY_LAST_CLAIM, System.currentTimeMillis());
    }

    private boolean canClaim(SkuddUser su){
        long lastClaim = su.getStats().getLong(Stat.DAILY_LAST_CLAIM);
        if(lastClaim == -1) return true;

        Calendar lastClaimDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        lastClaimDate.setTime(new Date(lastClaim));
        int lastClaimDay = lastClaimDate.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentDate.get(Calendar.DAY_OF_YEAR);

        if(currentDay == 1)
            if(lastClaimDay == 365 || lastClaimDay == 366)
                return true;

        return currentDay - lastClaimDay >= 1;
    }

    private boolean isInTimeFrame(SkuddUser su){
        long lastClaim = su.getStats().getLong(Stat.DAILY_LAST_CLAIM);
        if(lastClaim == -1) return false;

        Calendar lastClaimDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        lastClaimDate.setTime(new Date(lastClaim));
        int lastClaimDay = lastClaimDate.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentDate.get(Calendar.DAY_OF_YEAR);

        if(currentDay == 1)
            if(lastClaimDay == 365 || lastClaimDay == 366)
                return true;

        return currentDay - lastClaimDay == 1;
    }

}
