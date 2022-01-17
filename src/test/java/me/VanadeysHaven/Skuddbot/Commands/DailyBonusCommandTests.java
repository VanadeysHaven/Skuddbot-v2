package me.VanadeysHaven.Skuddbot.Commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.aggregator.AggregateWith;

import static org.junit.jupiter.api.Assertions.*;

public class DailyBonusCommandTests {

    private final static long testCurrentTimeMillis = 1642433093940L;
    private final static long testCurrentTimeMillisWithBonus = 1641074041290L;
    private final static DailyBonusCommand.Helper helper = new DailyBonusCommand.Helper();

    @Test
    public void testGetDayReturnsCorrectDay(){
        assertEquals(17, helper.getDay(testCurrentTimeMillis));
    }

    @Test
    public void testGetMonthReturnsCorrectMonth(){
        assertEquals(1, helper.getMonth(testCurrentTimeMillis));
    }

    @Test
    public void testGetCurrentDayReturnsCorrectDay(){
        assertEquals(19009, helper.getCurrentDay(testCurrentTimeMillis));
    }

    @Test
    public void testCanClaimDeniesClaim(){
        assertFalse(helper.canClaim(18993, 18993));
    }

    @Test
    public void testCanClaimAllowsClaim(){
        assertTrue(helper.canClaim(18993, 18994));
    }

    @Test
    public void testCanClaimAllowsClaimForNewUser(){
        assertTrue(helper.canClaim(-1, 18994));
    }

    @Test
    public void testGetDaysMissedReturnsCorrectAmountForNewUser(){
        assertEquals(0, helper.getDaysMissed(-1, 18993));
    }

    @Test
    public void testGetDaysMissedReturnsCorrectAmount(){
        assertEquals(0, helper.getDaysMissed(18993, 18994));
        assertEquals(1, helper.getDaysMissed(18993, 18995));
    }

    @Test
    public void testGetTimeUntilNextClaimReturnsCorrectAmount(){
        assertEquals(30906060, helper.getTimeUntilNextClaim(testCurrentTimeMillis));
    }

    @Test
    public void testMultiplierInitializesCorrectlyForNewUser(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, -1, -1,
                        30, -1, -1,
                        -1, -1, -1,
                        0, -1, -1)
                        .initializeMultipliers();

        assertEquals(14, calculator.getCurrentMultiplier());
        assertEquals(0, calculator.getCurrentStreak());
        assertEquals(0, calculator.getLongestStreak());
    }

    @Test
    public void testMultiplierAndStreakInitializesCorrectlyForUserBelowCap(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, -1, -1,
                        30, -1, -1,
                        -1, -1, 17,
                        100, -1, -1)
                        .initializeMultipliers();

        assertEquals(17, calculator.getCurrentMultiplier());
        assertEquals(100, calculator.getCurrentStreak());
        assertEquals(100, calculator.getCurrentStreak());
    }

    @Test
    public void testMultiplierAndStreakInitializesCorrectlyForUserAboveCap(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, -1, -1,
                        30, -1, -1,
                        -1, -1, 55,
                        100, -1, -1)
                        .initializeMultipliers();

        assertEquals(30, calculator.getCurrentMultiplier());
        assertEquals(100, calculator.getCurrentStreak());
        assertEquals(100, calculator.getCurrentStreak());
    }

    @Test
    public void testSkipMultiplierInitialization(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, -1, -1,
                        30, -1, -1,
                        -1, 6, 6,
                        6, -1, -1)
                        .initializeMultipliers();

        assertEquals(6, calculator.getCurrentMultiplier());
        assertEquals(6, calculator.getCurrentStreak());
        assertEquals(6, calculator.getLongestStreak());
    }

    @Test
    public void testMissedDaysIsCalculatedCorrectly(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, 18992, 18994,
                        -1, -1, -1,
                        -1, 15, 15,
                        15, -1, 0)
                        .initializeMultipliers().checkDaysMissed();
        assertEquals(1, calculator.getFrozenDays());
        assertEquals(10, calculator.getCurrentMultiplier());
        assertEquals(10, calculator.getCurrentStreak());
        assertEquals(15, calculator.getLongestStreak());

        calculator =
                helper.new Calculator(-1, 18993, 18994,
                        -1, -1, -1,
                        -1, -1, -1,
                        -1, -1, 0)
                        .checkDaysMissed();
        assertEquals(0, calculator.getFrozenDays());
    }

    @Test
    public void testCountUpCorrectlyWhenNoFrozenDays(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, 18993, 18994,
                        30, -1, -1,
                        -1, 20, 35,
                        20, 4, 0)
                        .initializeMultipliers().checkDaysMissed().countUp();

        assertFalse(calculator.isPenaltyApplied());
        assertFalse(calculator.isWeeklyApplied());
        assertEquals(36, calculator.getCurrentStreak());
        assertEquals(36, calculator.getLongestStreak());
        assertTrue(calculator.isNewLongest());
        assertEquals(5, calculator.getWeeklyCounter());
        assertEquals(21, calculator.getCurrentMultiplier());
    }

    @Test
    public void testWeeklyAppliedCorrectly(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, 18993, 18994,
                        30, -1, -1,
                        -1, 20, 35,
                        20, 7, 0)
                        .initializeMultipliers().checkDaysMissed().countUp();

        assertTrue(calculator.isWeeklyApplied());
        assertEquals(0, calculator.getWeeklyCounter());
    }

    @Test
    public void testFrozenDaysIsAppliedCorrectly(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, 18993, 18994,
                        30, -1, -1,
                        -1, 20, 35,
                        35, 7, 3)
                        .initializeMultipliers().checkDaysMissed().countUp();

        assertTrue(calculator.isPenaltyApplied());
        assertEquals(2, calculator.getFrozenDays());
        assertFalse(calculator.isWeeklyApplied());
        assertEquals(35, calculator.getCurrentStreak());
        assertEquals(35, calculator.getLongestStreak());
        assertFalse(calculator.isNewLongest());
        assertEquals(7, calculator.getWeeklyCounter());
        assertEquals(20, calculator.getCurrentMultiplier());
    }

    @Test
    public void testFrozenDaysIsAppliedCorrectlyWithOneMissedDay(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, 18992, 18994,
                        30, -1, -1,
                        -1, 20, 35,
                        35, 6, 0)
                        .initializeMultipliers().checkDaysMissed().countUp();

        assertTrue(calculator.isPenaltyApplied());
        assertEquals(15, calculator.getCurrentMultiplier());
        assertEquals(30, calculator.getCurrentStreak());
        assertEquals(35, calculator.getLongestStreak());
        assertEquals(6, calculator.getWeeklyCounter());
        assertEquals(0, calculator.getFrozenDays());
    }

    @Test
    public void testMultiplierCapsCorrectly(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(-1, 18993, 18994,
                        30, -1, -1,
                        -1, 30, 30,
                        30, 4, 0)
                        .initializeMultipliers().checkDaysMissed().countUp();

        assertEquals(30, calculator.getCurrentMultiplier());
        assertEquals(31, calculator.getCurrentStreak());
        assertEquals(31, calculator.getLongestStreak());
    }

    @Test
    public void testCorrectBonusesForUserUnderCap(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillis, 18993, 18994,
                        30, 1.2, 250,
                        500, 16, 16,
                        16, 4, 0)
                        .runCalculations();

        assertEquals(4622, calculator.getCurrencyBonus());
        assertEquals(9244, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesForUserOverCap(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillis, 18993, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 4, 0)
                        .runCalculations();

        assertEquals(49453, calculator.getCurrencyBonus());
        assertEquals(98906, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusForUserWithWeekly(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillis, 18993, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 7, 0)
                        .runCalculations();

        assertEquals(98906, calculator.getCurrencyBonus());
        assertEquals(197812, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesWithSeasonalBonus(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillisWithBonus, 18993, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 4, 0)
                        .runCalculations();

        assertEquals(59453, calculator.getCurrencyBonus());
        assertEquals(103906, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesForFrozenUser(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillis, 18993, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 4, 4)
                        .runCalculations();

        assertEquals(250, calculator.getCurrencyBonus());
        assertEquals(500, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesForFrozenUserWithPendingWeekly(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillis, 18993, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 7, 4)
                        .runCalculations();

        assertEquals(250, calculator.getCurrencyBonus());
        assertEquals(500, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesForFrozenUserWithSeasonalBonus(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillisWithBonus, 18993, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 4, 4)
                        .runCalculations();

        assertEquals(10250, calculator.getCurrencyBonus());
        assertEquals(5500, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesForUserWithMissedDay(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillis, 18992, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 4, 0)
                        .runCalculations();

        assertEquals(250, calculator.getCurrencyBonus());
        assertEquals(500, calculator.getExperienceBonus());
    }

    @Test
    public void testCorrectBonusesForUserWithMissedDayWithSeasonalBonus(){
        DailyBonusCommand.Helper.Calculator calculator =
                helper.new Calculator(testCurrentTimeMillisWithBonus, 18992, 18994,
                        30, 1.2, 250,
                        500, 30, 37,
                        37, 4, 0)
                        .runCalculations();

        assertEquals(10250, calculator.getCurrencyBonus());
        assertEquals(5500, calculator.getExperienceBonus());
    }

}
