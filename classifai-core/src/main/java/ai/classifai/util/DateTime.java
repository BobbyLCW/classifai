package ai.classifai.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static String get()
    {
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }
}
