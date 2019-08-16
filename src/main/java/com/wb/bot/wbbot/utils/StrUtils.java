package com.wb.bot.wbbot.utils;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {

    public static String substring( String s, String s1, String s2) {
        s = (s.substring(s.indexOf(s1) + s1.length(),
                s.lastIndexOf(s2)));
        return s;
    }

    public static <T> String combination( T object) {
        List<T> list = new ArrayList<>();
        list.add(object);
        return JSON.toJSONString(list);
    }

    public static String matche(  String uri) {
        String match = "/([a-zA-Z0-9\\-_%/])+";
        Pattern r = Pattern.compile(match);
        Matcher m = r.matcher(uri);
        if(m.find()) {
            return m.group();
        }
        return null;
    }
}
