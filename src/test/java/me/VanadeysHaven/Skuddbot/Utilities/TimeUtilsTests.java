package me.VanadeysHaven.Skuddbot.Utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for time utils.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.2
 * @since 2.3.2
 */
public class TimeUtilsTests {

    private static long testCurrentTimeMillis = 1641074041290L;
    private static long testTimeRemaining = 7558710;

    /*
    formatTimeRemaining
    formatTime
     */

    @Test
    public void testFormatTimeRemainingReturnsCorrectFormat(){
        assertEquals("0d 2h 5m 58s", TimeUtils.formatTimeRemaining(testTimeRemaining));
    }

    @Test
    public void testFormatTimeReturnsCorrectFormat(){
        assertEquals("01-01-2022 22:54:01 (UTC)", TimeUtils.formatTime(testCurrentTimeMillis));
    }

}
