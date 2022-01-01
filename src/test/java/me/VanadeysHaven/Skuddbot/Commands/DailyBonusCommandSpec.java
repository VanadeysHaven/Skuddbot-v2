package me.VanadeysHaven.Skuddbot.Commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DailyBonusCommandSpec {

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

}
