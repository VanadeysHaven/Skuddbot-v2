package me.VanadeysHaven.Skuddbot.Commands;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSettingsContainer;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
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
 * @version 2.3.24
 * @since 2.1.1
 */
public class DailyBonusCommand extends Command {

    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_HOUR = 3600000;
    private static final int PENALTY = 5;

    @Getter
    private enum Bonus {

        HAPPY_NEW_YEAR(1, 1, 64000, 32000, Emoji.FIREWORKS, "Happy 2023!"),
        VALENTINE(14, 2, 10000, 5000, Emoji.HEART, "Happy valentines day!"),
        CHIRSTMAS(25, 12, 250000, 500000, Emoji.SANTA, "Ho ho ho! Merry christmas!"),
        BOXING_DAY(26, 12, 250000, 500000, Emoji.PACKAGE, " Ho ho ho! Merry christmas!"),
        FIVE_DAYS(27, 12, 2000, 1000, Emoji.FIVE, "days until 2023!"),
        FOUR_DAYS(28, 12, 4000, 2000, Emoji.FOUR, "days until 2023!"),
        THREE_DAYS(29, 12, 8000, 4000, Emoji.THREE, "days until 2023!"),
        TWO_DAYS(30, 12, 16000, 8000, Emoji.TWO, "days until 2023!"),
        ONE_DAY(31, 12, 32000, 16000, Emoji.ONE, "days until 2023!");

        final int day;
        final int month;
        final int currencyBonus;
        final int xpBonus;
        final Emoji emoji;
        final String message;

        Bonus(int day, int month, int currencyBonus, int xpBonus, Emoji emoji, String message){
            this.day = day;
            this.month = month;
            this.currencyBonus = currencyBonus;
            this.xpBonus = xpBonus;
            this.emoji = emoji;
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
            "Daily bonus claimed: ({1}) {2}\n" +
            "+{3} Skuddbux\n" +
            "+{4} <:xp_icon:458325613015466004>\n" +
            "{5}";

    private final Helper helper;

    public DailyBonusCommand() {
        super(new String[]{"dailybonus", "claim", "db"}, "Claim your daily bonus.", "https://wiki.skuddbot.xyz/systems/daily-bonus");
        helper = new Helper();
    }

    @Override
    public void run(CommandRequest request) {
        Server server = request.getServer();
        Message message = request.getMessage();
        SkuddUser user = pm.getUser(server.getId(), message.getAuthor().getId());
        SkuddServer sServer = sm.getServer(server.getId());
        ServerSettingsContainer settings = sServer.getSettings();
        UserSettingsContainer uSettings = user.getSettings();
        StatsContainer stats = user.getStats();

        long currentTime = System.currentTimeMillis() + (MILLIS_IN_HOUR * uSettings.getInt(UserSetting.TIMEZONE));
        long currentDay = helper.getCurrentDay(currentTime);
        long lastClaim = user.getStats().getLong(Stat.DAILY_LAST_CLAIM);

        if(!helper.canClaim(lastClaim, currentDay)){
            MessagesUtils.addReaction(message, Emoji.X, "You can't claim your daily bonus yet. - You can claim it again in " + helper.formatTimeUntilNextClaim(currentTime));
            return;
        }

        int currentMultiplier = stats.getInt(Stat.DAILY_CURRENT_MULTIPLIER);
        int initialStreak = stats.getInt(Stat.DAILY_CURRENT_STREAK);
        int longestStreak = stats.getInt(Stat.DAILY_LONGEST_STREAK);
        int weeklyCounter = stats.getInt(Stat.DAILY_WEEKLY_COUNTER);
        int frozenDays = stats.getInt(Stat.DAILY_DAYS_FROZEN);

        int baseCurrency = settings.getInt(ServerSetting.DAILY_BASE_CURRENCY_BONUS);
        int baseExperience = settings.getInt(ServerSetting.DAILY_BASE_EXPERIENCE_BONUS);
        int multiplierCap = settings.getInt(ServerSetting.DAILY_BONUS_MULTIPLIER_CAP);
        double multiplierModifier = settings.getDouble(ServerSetting.DAILY_BONUS_MODIFIER);

        Helper.Calculator calculator = helper.new Calculator(currentTime, lastClaim, currentDay,
                        multiplierCap, multiplierModifier, baseCurrency,
                        baseExperience, currentMultiplier, initialStreak,
                        longestStreak, weeklyCounter, frozenDays)
                .runCalculations();

        calculator.process(user);

        String streakString = "";
        if (calculator.getCurrentStreak() == 1 && initialStreak > 1)
            streakString = "**Claim streak lost:** *" + initialStreak + " days* | " +
                    "**Days missed:** *" + calculator.getMissedDays() + " " + (calculator.getMissedDays() == 1 ? "day" : "days") + "*";
        else if(calculator.getMissedDays()  >= 1 && initialStreak > 1)
            streakString = "**Claim streak reduced:** *" + initialStreak + " days* (-" + (calculator.getMissedDays()  * PENALTY) + " days) | " +
                    "**Days missed:** *" + calculator.getMissedDays()  + " " + (calculator.getMissedDays()  == 1 ? "day" : "days") + "*";
        else if (calculator.getCurrentStreak() == 2)
            streakString = "**Claim streak started:** *" + calculator.getCurrentStreak() + " days*";
        else if (calculator.getCurrentStreak() > 2)
            streakString = "**Claim streak continued:** *" + calculator.getCurrentStreak() + " days*";

        if(calculator.isNewLongest() && calculator.getCurrentStreak() >= 2)
            streakString += " | **New longest streak!**";

        String multiplierString = "x" + calculator.getCurrentMultiplier();
        if(calculator.isPenaltyApplied())
            multiplierString = "~~" + multiplierString + "~~ | **FROZEN**";
        if(calculator.getCurrentMultiplier() == calculator.getMultiplierCap())
            multiplierString += " | **MAX**";
        String bonusStr = calculator.isWeeklyApplied() ? "\n**WEEKLY BONUS APPLIED:** *rewards doubled*" : "";
        if(calculator.isBonusApplied()) {
            bonusStr += "\n**SEASONAL BONUS APPLIED:** " + calculator.getAppliedBonus().getEmoji()  + " *" + calculator.getAppliedBonus().getMessage() + "*";
        }

        String msg = MessageFormat.format(MESSAGE_FORMAT, request.getSender().getDisplayName(), multiplierString, bonusStr, calculator.getCurrencyBonus(), calculator.getExperienceBonus(), streakString);
        MessagesUtils.sendPlain(request.getChannel(), msg);
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
            private Bonus appliedBonus;

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
                    currentStreak = longestStreak;

                    if (currentStreak < 15)
                        currentMultiplier = 14;
                    else
                        currentMultiplier = Math.min(currentStreak, multiplierCap);
                }
                return this;
            }

            public Calculator checkDaysMissed(){
                missedDays = getDaysMissed(lastClaim, currentDay);
                if (missedDays > 0) {
                    frozenDays += missedDays;
                    currentMultiplier = (int) Math.max(currentMultiplier - (PENALTY * missedDays), 0);
                    currentStreak = (int) Math.max(currentStreak - (PENALTY * missedDays), 0);
                }
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
                    appliedBonus = b;
                    currencyBonus += b.getCurrencyBonus();
                    experienceBonus += b.getXpBonus();
                }

                return this;
            }

            public boolean isBonusApplied(){
                return appliedBonus != null;
            }

            public void process(SkuddUser su){
                su.getStats().setLong(Stat.DAILY_LAST_CLAIM, currentDay);
                su.getStats().setInt(Stat.DAILY_CURRENT_MULTIPLIER, currentMultiplier);
                su.getStats().setInt(Stat.DAILY_CURRENT_STREAK, currentStreak);
                su.getStats().setInt(Stat.DAILY_LONGEST_STREAK, longestStreak);
                su.getStats().setInt(Stat.DAILY_WEEKLY_COUNTER, weeklyCounter);
                su.getStats().setInt(Stat.DAILY_DAYS_FROZEN, frozenDays);
                su.getStats().incrementInt(Stat.EXPERIENCE, experienceBonus);
                su.getCurrencies().incrementInt(Currency.SKUDDBUX, currencyBonus);
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
