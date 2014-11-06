import java.util.concurrent.TimeUnit;

/*
 * Class for formatting elapsed time.
 */
public class Time {
    public static String formatNanos(long ns) {
        String str = String.format("%02d:%02d:%02d", 
            TimeUnit.NANOSECONDS.toHours(ns),
            TimeUnit.NANOSECONDS.toMinutes(ns) -  
            TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(ns)),
            TimeUnit.NANOSECONDS.toSeconds(ns) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(ns)));  

        return str;
    }
}
