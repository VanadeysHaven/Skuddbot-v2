package me.VanadeysHaven.Skuddbot.Commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DailyBonusCommandTests {

    private static long testCurrentTimeMillis = 1641074041290L;
    private static DailyBonusCommand.Helper helper = new DailyBonusCommand.Helper();

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

}
