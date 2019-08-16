package com.wb.bot.wbbot.utils;

public class DateUtils {
    public static Long getTotalSeconds() {
        return  System.currentTimeMillis() / 1000;
    }
    public static Long getTotalTime() {
        return  System.currentTimeMillis();
    }
}
