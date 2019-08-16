package com.wb.bot.wbbot.netty;

import com.wb.bot.wbbot.config.ResourcePathMappingConfig;
import com.wb.bot.wbbot.controller.GroupController;
import com.wb.bot.wbbot.controller.MessageController;
import com.wb.bot.wbbot.utils.ReflectUtils;
import com.wb.bot.wbbot.utils.StrUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;


public class ActionDispatcher {

    private static MessageController messageController = new MessageController();

    private static GroupController groupController = new GroupController();

    private static ResourcePathMappingConfig mappingConfig = ConfigFactory.create(ResourcePathMappingConfig.class);


    public static String distribution(String uri, Map<String, List<String>> parameters) throws Exception{
        uri = StrUtils.matche(uri);
        if (StringUtils.equals(uri, mappingConfig.groupCreate())) {
            return (String) ReflectUtils.invoke(groupController, "groupCreate", parameters);
        }
        if (StringUtils.equals(uri, mappingConfig.groupQuery())) {
            return (String) ReflectUtils.invoke(groupController, "groupQuery", parameters);
        }
        if (StringUtils.equals(uri, mappingConfig.groupMessage())) {
            return (String) ReflectUtils.invoke(groupController, "groupMessage", parameters);
        }
        if (StringUtils.equals(uri, mappingConfig.groupJoin())) {
            return (String) ReflectUtils.invoke(groupController, "groupJoin", parameters);
        }
        if (StringUtils.equals(uri, mappingConfig.groupKick())) {
            return (String) ReflectUtils.invoke(groupController, "groupKick", parameters);
        }
        if (StringUtils.equals(uri, mappingConfig.messageSend())) {
            return (String) ReflectUtils.invoke(messageController, "messageSend", parameters);
        }
        return "no path";
    }


}
