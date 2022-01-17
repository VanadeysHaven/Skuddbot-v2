package me.VanadeysHaven.Skuddbot.Commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DailyBonusCommandTests {

    private final static long testCurrentTimeMillis = 1641074041290L;
    private final static DailyBonusCommand.Helper helper = new DailyBonusCommand.Helper();

    @Test
    public void testGetDayReturnsCorrectDay(){
        assertEquals(1L, helper.getDay(testCurrentTimeMillis));
    }

    @Test
    public void testGetMonthReturnsCorrectMonth(){
        assertEquals(1, helper.getMonth(testCurrentTimeMillis));
    }

    @Test
    public void testGetCurrentDayReturnsCorrectDay(){
        assertEquals(18993, helper.getCurrentDay(testCurrentTimeMillis));
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
        assertEquals(7558710, helper.getTimeUntilNextClaim(testCurrentTimeMillis));
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
                        35, 6, 3)
                        .initializeMultipliers().checkDaysMissed().countUp();

        assertTrue(calculator.isPenaltyApplied());
        assertFalse(calculator.isWeeklyApplied());
        assertEquals(35, calculator.getCurrentStreak());
        assertEquals(35, calculator.getLongestStreak());
        assertFalse(calculator.isNewLongest());
        assertEquals(6, calculator.getWeeklyCounter());
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

}
