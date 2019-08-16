package com.wb.bot.wbbot.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.DateUtils;
import com.wb.bot.wbbot.utils.HttpUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GroupManagement {

    private static final Logger logger = LoggerFactory.getLogger(GroupManagement.class);

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    private static Map<String, String> requestHeadMap = ApplicationConfig.RequestHeadConfig.RequestHeadMap;

    /**
     * 创建分组
     * @param members 微博id，微博id
     * return gid
     */
    public static Long groupCreate(String members){
        //数据结果字段
        String result = "result";
        ActionResult instance = ActionResult.getInstance();
        Map<String,String> perMap = new HashMap<>(10);
        perMap.put("members",members);
        perMap.put("source","209678993");
        String data = HttpUtils.doPost(config.groupCreate(), perMap, instance.getCookieStore(), requestHeadMap, null);
        if(StringUtils.isBlank(data)){
            return null;
        }
        JSONObject dataJsonObject = JSON.parseObject(data);
        if(!dataJsonObject.getBoolean(result)){
            return null;
        }
        return dataJsonObject.getLong("id");
    }

    /**
     * 获取群成员
     * @param gid 分组id
     */
    public static JSONArray groupQuery(Long gid){
        //数据结果字段
        String result = "result";
        ActionResult instance = ActionResult.getInstance();
        String data = HttpUtils.doGet(config.groupQuery(gid, DateUtils.getTotalSeconds()), null,
                instance.getCookieStore(), requestHeadMap, null);
        if(StringUtils.isBlank(data)){
            return null;
        }
        JSONObject dataJsonObject = JSON.parseObject(data);
        if(!dataJsonObject.getBoolean(result)){
            return null;
        }
        //获取群成员信息
        JSONArray memberInfo = dataJsonObject.getJSONArray("member_infos");
        logger.info("群成员信息：{}",JSON.toJSONString(memberInfo));
        return memberInfo;
    }


    /**
     * 群消息获取
     */
    public static JSONArray groupMessage(Long gid){
        //数据结果字段
        String result = "result";
        ActionResult instance = ActionResult.getInstance();
        String data = HttpUtils.doGet(config.groupMessages(20,gid, DateUtils.getTotalSeconds()),
                null, instance.getCookieStore(), requestHeadMap, null);
        if(StringUtils.isBlank(data)){
            return null;
        }
        JSONObject dataJsonObject = JSON.parseObject(data);
        if(!dataJsonObject.getBoolean(result)){
            return null;
        }
        //获取群成员信息
        JSONArray messageInfo = dataJsonObject.getJSONArray("messages");
        logger.info("对话信息：{}",JSON.toJSONString(messageInfo));
        return messageInfo;
    }

    /**
     * 群成员加入
     */
    public static boolean groupJoin(Long gid,String uids){
        ActionResult instance = ActionResult.getInstance();
        Map<String,String> perMap = new HashMap<>(10);
        perMap.put("uids",uids);
        perMap.put("id",gid.toString());
        perMap.put("source","209678993");
        String data = HttpUtils.doPost(config.groupJoin(), perMap, instance.getCookieStore(), requestHeadMap, null);
        if(StringUtils.isBlank(data)){
            return false;
        }
        JSONObject dataJsonObject = JSON.parseObject(data);
        return dataJsonObject.getBoolean("result");
    }

    /**
     * 群成员移除
     */
    public static boolean groupKick(Long gid,String uids){
        ActionResult instance = ActionResult.getInstance();
        Map<String,String> perMap = new HashMap<>(10);
        perMap.put("uids",uids);
        perMap.put("id",gid.toString());
        perMap.put("source","209678993");
        String data = HttpUtils.doPost(config.groupKick(), perMap, instance.getCookieStore(), requestHeadMap, null);
        if(StringUtils.isBlank(data)){
            return false;
        }
        JSONObject dataJsonObject = JSON.parseObject(data);
        return dataJsonObject.getBoolean("result");
    }


}
