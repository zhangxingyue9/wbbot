package com.wb.bot.wbbot.core;

import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.DateUtils;
import com.wb.bot.wbbot.utils.HttpUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;


public class BaseComponent {

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    private static Map<String, String> requestHeadMap = ApplicationConfig.RequestHeadConfig.RequestHeadMap;

    public static boolean addNews(String content) {
        String code = "code";
        String successCode = "100000";
        ActionResult instance = ActionResult.getInstance();
        Map<String, String> perMap = new HashMap<>(16);
        newsParameterLoad(perMap, content);
        String data = HttpUtils.doPost(config.addNews(DateUtils.getTotalTime().toString()), perMap, instance.getCookieStore(), requestHeadMap, null);
        return  (StringUtils.isNotBlank(data) && successCode.equals(JSONObject.parseObject(data).getString(code)));
    }

    private static void newsParameterLoad(Map<String, String> perMap, String content) {
        perMap.put("location", "v6_content_home");
        perMap.put("text", content);
        perMap.put("appkey", "");
        perMap.put("style_type", "1");
        perMap.put("pic_id", "");
        perMap.put("tid", "");
        perMap.put("pdetail", "");
        perMap.put("mid", "");
        perMap.put("isReEdit", "false");
        perMap.put("rank", "0");
        perMap.put("rankid", "");
        perMap.put("module", "stissue");
        perMap.put("pub_source", "main_");
        perMap.put("pub_type", "dialog");
        perMap.put("isPri", "0");
        perMap.put("_t", "0");
    }

}
