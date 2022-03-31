package com.example.test;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class modifiedDateToString
{
    public String getCurrentTime(long duration)
    {
        Date currentDate = new Date();

        long second = TimeUnit.MILLISECONDS.toSeconds(currentDate.getTime() - duration);
        long minute = TimeUnit.MILLISECONDS.toMinutes(currentDate.getTime() - duration);
        long hour = TimeUnit.MILLISECONDS.toHours(currentDate.getTime() - duration);
        long day = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - duration);

        if (second == 1)
            return " a second ago";
        else if (second > 1 && second < 60)
            return second + " seconds ago";
        else if (minute == 1)
            return "a minute ago";
        else if (minute > 1 && minute < 60)
            return minute + " minutes ago";
        else if (hour == 1)
            return "an hour ago";
        else if (hour > 1 && hour < 24)
            return hour + " hours ago";
        else if (day == 1)
            return "a day ago";
        else
            return day + " days ago";
    }

}
