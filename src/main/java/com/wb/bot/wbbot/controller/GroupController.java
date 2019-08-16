package com.wb.bot.wbbot.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.core.GroupManagement;

public class GroupController {

    public String groupCreate(String members){
        Long id = GroupManagement.groupCreate(members);
        if(id==null){
            return "创建群组失败";
        }
        return "群组创建成功：id值"+id;
    }


    public  String groupQuery(Long gid){
        JSONArray memberInfoArray = GroupManagement.groupQuery(gid);
        if(memberInfoArray==null){
            return "群成员查询失败";
        }
        return "群成员信息"+ JSONObject.toJSONString(memberInfoArray);
    }


    /**
     * 群消息获取
     */
    public  String groupMessage(Long gid){
        JSONArray messageInfoArray = GroupManagement.groupMessage(gid);
        if(messageInfoArray==null){
            return "群消息获取失败";
        }
        return "群消息："+ JSONObject.toJSONString(messageInfoArray);
    }

    /**
     * 群成员加入
     */
    public  String groupJoin(Long gid,String uids){
        return GroupManagement.groupJoin(gid,uids)?"加入成功":"加入失败";
    }

    /**
     * 群成员移除
     */
    public  String groupKick(Long gid,String uids){
        return GroupManagement.groupKick(gid,uids)?"移除成功":"移除失败";
    }

}
