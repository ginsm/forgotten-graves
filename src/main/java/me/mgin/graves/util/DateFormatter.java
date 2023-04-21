package me.mgin.graves.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static String formatDate(long mstime) {
        DateFormat df = new SimpleDateFormat("HH:mm (z) 'on' MM/dd/yy");
        return df.format(new Date(mstime));
    }
}
