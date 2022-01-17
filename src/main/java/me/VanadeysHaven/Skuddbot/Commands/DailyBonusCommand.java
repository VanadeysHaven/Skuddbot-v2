package me.VanadeysHaven.Skuddbot.Commands;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSettingsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.CurrenciesContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSettingsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.Stat;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.TimeUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Command used to claim daily bonuses.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.2
 * @since 2.1.1
 */
public class DailyBonusCommand extends Command {

    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_HOUR = 3600000;
    private static final int PENALTY = 5;

    @Getter
    private enum Bonus {

        HAPPY_NEW_YEAR(1, 1, 10000, 5000, Emoji.FIREWORKS.getUnicode() + "Happy new year!"),
        VALENTINE(14, 2, 10000, 5000, Emoji.HEART.getUnicode() + " Happy valentines day!");

        final int day;
        final int month;
        final int currencyBonus;
        final int xpBonus;
        final String message;

        Bonus(int day, int month, int currencyBonus, int xpBonus, String message){
            this.day = day;
            this.month = month;
            this.currencyBonus = currencyBonus;
            this.xpBonus = xpBonus;
            this.message = message;
        }

        public static Bonus getForDay(int day, int month){
            for(Bonus b : values())
                if(day == b.getDay() && month == b.getMonth())
                    return b;

            return null;
        }

    }

    public static String MESSAGE_FORMAT = Emoji.GIFT.getUnicode() + " **DAILY BONUS** | *{0}*\n\n" +
            "Daily bonus claimed: {1}\n" +

            "+{2} Skuddbux\n" +
            "+{3} <:xp_icon:458325613015466004>\n" +
            "{4}";

    private final Helper helper;

    public DailyBonusCommand() {
        super(new String[]{"dailybonus", "claim", "db"}, "Claim your daily bonus.", "https://wiki.skuddbot.xyz/systems/daily-bonus");
        helper = new Helper();
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        SkuddUser user = pm.getUser(server.getId(), message.getAuthor().getId());
        SkuddServer sServer = sm.getServer(server.getId());
        ServerSettingsContainer settings = sServer.getSettings();
        UserSettingsContainer uSettings = user.getSettings();
        CurrenciesContainer currencies = user.getCurrencies();
        StatsContainer stats = user.getStats();

        long currentTime = System.currentTimeMillis() + (MILLIS_IN_HOUR * uSettings.getInt(UserSetting.TIMEZONE));
        long currentDay = helper.getCurrentDay(currentTime);
        long lastClaim = user.getStats().getLong(Stat.DAILY_LAST_CLAIM);

        if(!helper.canClaim(lastClaim, currentDay)){
            MessagesUtils.addReaction(message, Emoji.X, "You can't claim your daily bonus yet. - You can claim it again in " + helper.formatTimeUntilNextClaim(currentTime));
            return;
        }

        int currentMultiplier = stats.getInt(Stat.DAILY_MULTIPLIER);
        int currentStreak = stats.getInt(Stat.DAILY_CURRENT_STREAK);
        int longestStreak = stats.getInt(Stat.DAILY_LONGEST_STREAK);

        long daysMissed = 0;
        long lastClaimStreak = 0;

        if(helper.hasDaysMissed(lastClaim, currentTime)) {
            daysMissed = helper.getDaysMissed(lastClaim, currentDay);

            lastClaimStreak = stats.getInt(Stat.DAILY_CURRENT_STREAK);
            int penalty = (int) Math.min(currentStreak - 1, PENALTY * daysMissed);
            stats.incrementInt(Stat.DAILY_CURRENT_STREAK, penalty * -1);
            stats.incrementIntBounds(Stat.DAILY_MULTIPLIER, penalty * -1, 1, settings.getInt(ServerSetting.DAILY_BONUS_MULTIPLIER_CAP));
        } else {
            stats.incrementInt(Stat.DAILY_CURRENT_STREAK);
            stats.incrementIntBounds(Stat.DAILY_MULTIPLIER, 1, 1, settings.getInt(ServerSetting.DAILY_BONUS_MULTIPLIER_CAP));
        }


        boolean newLongest = false;
        if(currentStreak > longestStreak){
            newLongest = true;
            stats.setInt(Stat.DAILY_LONGEST_STREAK, currentStreak);
        }

        int currencyBonus = settings.getInt(ServerSetting.DAILY_CURRENCY_BONUS);
        int xpBonus = settings.getInt(ServerSetting.DAILY_XP_BONUS);
        int cap = settings.getInt(ServerSetting.DAILY_BONUS_MULTIPLIER_CAP);
        double multiplier = Math.pow(settings.getDouble(ServerSetting.DAILY_BONUS_MODIFIER), currentMultiplier);
        currencyBonus *= multiplier;
        xpBonus *= multiplier;
        boolean applyWeekly = stats.getInt(Stat.DAILY_DAYS_SINCE_WEEKLY) >= 7;

        if(applyWeekly){
            xpBonus *= 2;
            currencyBonus *= 2;
            stats.setInt(Stat.DAILY_DAYS_SINCE_WEEKLY, 1);
        } else {
            stats.incrementInt(Stat.DAILY_DAYS_SINCE_WEEKLY);
        }
        Bonus b = Bonus.getForDay(helper.getDay(currentTime), helper.getMonth(currentTime));
        if(b != null) {
            xpBonus += b.getXpBonus();
            currencyBonus += b.getCurrencyBonus();
        }

        user.getCurrencies().incrementInt(Currency.SKUDDBUX, currencyBonus);
        stats.incrementInt(Stat.EXPERIENCE, xpBonus);

        String streakString = "";
        if (currentStreak == 1 && lastClaimStreak > 1)
            streakString = "**Claim streak lost:** *" + lastClaimStreak + " days* | " +
                    "**Days missed:** *" + daysMissed + " " + (daysMissed == 1 ? "day" : "days") + "*";
        else if(daysMissed >= 1 && lastClaimStreak > 1)
            streakString = "**Claim streak reduced:** *" + currentStreak + " days* (-" + (daysMissed * PENALTY) + " days) | " +
                    "**Days missed:** *" + daysMissed + " " + (daysMissed == 1 ? "day" : "days") + "*";
        else if (currentStreak == 2)
            streakString = "**Claim streak started:** *" + currentStreak + " days*";
        else if (currentStreak > 2)
            streakString = "**Claim streak continued:** *" + currentStreak + " days*";

        if(newLongest && currentStreak >= 2)
            streakString += " | **New longest streak!**";

        String bonusStr = applyWeekly ? "\n**WEEKLY BONUS APPLIED:** *rewards doubled*" : "";
        if(b != null) {
            bonusStr += "\n**SEASONAL BONUS APPLIED:** " + b.getMessage();
            if(helper.getDay(currentTime) == 30){
                bonusStr += " (" + currencies.getString(Currency.PRIDE_FLAGS) + " pride flags applied!)";
            }
        }

        String msg = MessageFormat.format(MESSAGE_FORMAT, message.getAuthor().getDisplayName(), bonusStr, currencyBonus, xpBonus, streakString);

        MessagesUtils.sendPlain(message.getChannel(), msg);
        stats.setLong(Stat.DAILY_LAST_CLAIM, helper.getCurrentDay(currentTime));
        stats.incrementInt(Stat.DAILY_DAYS_SINCE_WEEKLY);
    }

    static class Helper {

        @Getter
        class Calculator {

            private final long currentTime;
            private final long currentDay;
            private final long lastClaim;

            private final int multiplierCap;
            private final double multiplierModifier;
            private final int baseCurrency;
            private final int baseExperience;

            private int currentMultiplier;
            private int currentStreak;
            private int longestStreak;

            private int weeklyCounter;
            private int frozenDays;

            private boolean penaltyApplied;
            private boolean weeklyApplied;
            private boolean newLongest;
            private long missedDays;
            private int currencyBonus;
            private int experienceBonus;

            public Calculator(long currentTime, long lastClaim, long currentDay,
                              int multiplierCap, double multiplierModifier, int baseCurrency,
                              int baseExperience, int currentMultiplier, int currentStreak,
                              int longestStreak, int weeklyCounter, int frozenDays) {
                this.currentTime = currentTime;
                this.currentDay = currentDay;
                this.lastClaim = lastClaim;

                this.multiplierCap = multiplierCap;
                this.multiplierModifier = multiplierModifier;
                this.baseCurrency = baseCurrency;
                this.baseExperience = baseExperience;

                this.currentMultiplier = currentMultiplier;
                this.currentStreak = currentStreak;
                this.longestStreak = longestStreak;

                this.weeklyCounter = weeklyCounter;
                this.frozenDays = frozenDays;

                penaltyApplied = false;
                weeklyApplied = false;
                newLongest = false;
                missedDays = -1;
                currencyBonus = 0;
                experienceBonus = 0;
            }

            public Calculator runCalculations(){
                initializeMultipliers();
                checkDaysMissed();
                countUp();
                return calculateBonuses();
            }


            public Calculator initializeMultipliers(){
                if(currentMultiplier == -1) {
                    if (currentStreak < 15)
                        currentMultiplier = 14;
                    else
                        currentMultiplier = Math.min(currentStreak, multiplierCap);

                    currentStreak = longestStreak;
                }

                return this;
            }

            public Calculator checkDaysMissed(){
                missedDays = getDaysMissed(lastClaim, currentDay);
                frozenDays += missedDays;
                currentMultiplier = (int) Math.max(currentMultiplier - (PENALTY * missedDays), 0);
                currentStreak = (int) Math.max(currentStreak - (PENALTY * missedDays), 0);
                return this;
            }

            public Calculator countUp(){
                if(frozenDays > 0) {
                    frozenDays--;
                    penaltyApplied = true;
                    return this;
                }
                if(weeklyCounter >= 7) {
                    weeklyApplied = true;
                    weeklyCounter = -1;
                }

                currentStreak++;
                if(longestStreak < currentStreak) {
                    longestStreak = currentStreak;
                    newLongest = true;
                }
                weeklyCounter++;
                currentMultiplier = Math.min(currentMultiplier + 1, multiplierCap);

                return this;
            }

            public Calculator calculateBonuses(){
                if(penaltyApplied) {
                    currencyBonus = baseCurrency;
                    experienceBonus = baseExperience;
                } else {
                    currencyBonus = (int) (baseCurrency * Math.pow(multiplierModifier, currentMultiplier - 1));
                    experienceBonus = (int) (baseExperience * Math.pow(multiplierModifier, currentMultiplier - 1));

                    if(weeklyApplied) {
                        currencyBonus *= 2;
                        experienceBonus *= 2;
                    }
                }

                Bonus b = Bonus.getForDay(getDay(currentTime), getMonth(currentTime));
                if(b != null) {
                    currencyBonus += b.getCurrencyBonus();
                    experienceBonus += b.getXpBonus();
                }

                return this;
            }

        }

        public int getDay(long currentTime) {
            Date current = new Date(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(current);

            return cal.get(Calendar.DAY_OF_MONTH);
        }

        public int getMonth(long currentTime) {
            Date current = new Date(currentTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(current);

            return cal.get(Calendar.MONTH) + 1;
        }

        public long getCurrentDay(long currentTime) {
            long currentWholeDayMillis = currentTime - (currentTime % MILLIS_IN_DAY);
            return currentWholeDayMillis / MILLIS_IN_DAY;
        }

        public boolean canClaim(long lastClaim, long currentDay) {
            if (lastClaim == -1) return true;
            return currentDay > lastClaim;
        }

        public long getDaysMissed(long lastClaim, long currentDay) {
            if (lastClaim == -1) return 0;
            return (currentDay - lastClaim) - 1;
        }

        public boolean hasDaysMissed(long lastClaim, long currentTime) {
            return getDaysMissed(lastClaim, currentTime) > 0;
        }

        public long getTimeUntilNextClaim(long currentTime) {
            long nextDayStartsAt = currentTime + MILLIS_IN_DAY;
            nextDayStartsAt = nextDayStartsAt - (nextDayStartsAt % MILLIS_IN_DAY);

            return nextDayStartsAt - currentTime;
        }

        public String formatTimeUntilNextClaim(long currentTime) {
            return TimeUtils.formatTimeRemaining(getTimeUntilNextClaim(currentTime));
        }

    }
}
