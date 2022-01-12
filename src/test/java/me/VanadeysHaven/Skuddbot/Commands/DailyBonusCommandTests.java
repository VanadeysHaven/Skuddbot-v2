package me.VanadeysHaven.Skuddbot.Commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DailyBonusCommandTests {

    private static long testCurrentTimeMillis = 1641074041290L;
    private static DailyBonusCommand.Helper helper = new DailyBonusCommand.Helper();

    /*
    getDay
    getMonth
    getCurrentDay
    canClaim
    getDaysMissed
    hasDaysMissed
    getTimeUntilNextClaim
    formatTimeUntilNextClaim
     */

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
//        helper.canClaim()
    }

}
