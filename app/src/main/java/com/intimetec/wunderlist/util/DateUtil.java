package com.intimetec.wunderlist.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    public static final DateFormat timeFormatHourMinute = new SimpleDateFormat("hh:mm");


    public static String getDateValue(Date date) {
        return dateFormat.format(date);
    }

    public static String getTimeValue(Date date) {
        return timeFormat.format(date);
    }

    public static String getTime(Date date) {
        return timeFormatHourMinute.format(date);
    }
}
