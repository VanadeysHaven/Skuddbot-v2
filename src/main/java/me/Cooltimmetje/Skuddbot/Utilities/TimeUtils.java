package me.Cooltimmetje.Skuddbot.Utilities;

import java.text.MessageFormat;

/**
 * Various utilities regarding time.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class TimeUtils {

    public static String formatTime(long millisRemaining){
        long time = millisRemaining / 1000;
        long secondsRemaining = time % 60;
        time = (time - secondsRemaining) / 60;
        long minutesRemaining = time % 60;
        time = (time - minutesRemaining) / 60;
        long hoursRemaining = time % 24;
        long daysRemaining = (time - hoursRemaining) / 24;

        return MessageFormat.format("{0}d {1}h {2}m {3}s", daysRemaining, hoursRemaining, minutesRemaining, secondsRemaining);
    }

}
