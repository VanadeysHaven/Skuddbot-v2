package me.VanadeysHaven.Skuddbot.Utilities;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * Various utilities regarding time.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public class TimeUtils {

    public static String formatTimeRemaining(long millisRemaining){
        long time = millisRemaining / 1000;
        long secondsRemaining = time % 60;
        time = (time - secondsRemaining) / 60;
        long minutesRemaining = time % 60;
        time = (time - minutesRemaining) / 60;
        long hoursRemaining = time % 24;
        long daysRemaining = (time - hoursRemaining) / 24;

        return MessageFormat.format("{0}d {1}h {2}m {3}s", daysRemaining, hoursRemaining, minutesRemaining, secondsRemaining);
    }

    public static String formatTime(long timeInMillis){
        String pattern = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        TimeZone tz = TimeZone.getTimeZone("Etc/UTC");
        sdf.setTimeZone(tz);

        return sdf.format(new Date(timeInMillis)) + " (UTC)";
    }

    public static String formatTime() {
        return formatTime(System.currentTimeMillis());
    }
}
