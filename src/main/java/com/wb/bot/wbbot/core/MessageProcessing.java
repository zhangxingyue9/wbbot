package com.wb.bot.wbbot.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.beans.Info;
import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.DateUtils;
import com.wb.bot.wbbot.utils.HttpUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageProcessing {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessing.class);

    private static CopyOnWriteArrayList<String> dmIds = new CopyOnWriteArrayList<>();

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    private static Map<String, String> requestHeadMap = ApplicationConfig.RequestHeadConfig.RequestHeadMap;

    void handle(String dataJson) {
        String uid = ActionResult.getInstance().getUid();
        JSONArray messageArray = JSONArray.parseArray(dataJson);
        if (messageArray.size() > 0) {
            for (int i = 0; i < messageArray.size(); i++) {
                JSONObject jsonObject = messageArray.getJSONObject(i);
                if (jsonObject.containsKey("data")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String pushDid = data.getString("push_did");
                    if (dmIds.contains(pushDid)) {
                        return;
                    }
                    dmIds.add(pushDid);
                    process(data, uid);
                }
            }
        }
    }

    private void process(JSONObject data, String uid) {
        if (data.containsKey("info")) {
            Info info = data.getObject("info", Info.class);
            String fromUser = HttpUtils.doGet(config.newUserInfo(info.getFromuid(), DateUtils.getTotalSeconds()), null,
                    ActionResult.getInstance().getCookieStore(), requestHeadMap, ActionResult.getInstance().getCookieStore());
            String userNick = JSONObject.parseObject(fromUser).getString("screen_name");
            String type = data.getString("type");
            if ("msg".equals(type)) {
                logger.info("接收到{}发送的内容：" + info.getContent(), userNick);
                if (StringUtils.equals("发微博", info.getContent())) {
                    boolean b = BaseComponent.addNews("统治地球计划开始中，此微博已被渗透，当前时间戳" + DateUtils.getTotalTime());
                    MessageClient.sendMessage(info.getFromuid().toString(), "微博发送" + (b ? "成功" : "失败"));
                    //6336710565
                } else if (StringUtils.equals("拉我入伙", info.getContent())) {
                    Long gid = GroupManagement.groupCreate(info.getFromuid() + ",6336710565");
                    MessageClient.sendMessage(info.getFromuid().toString(), "入伙" + (gid != null ? "成功" : "失败"));
                } else {
                    MessageClient.sendMessage(info.getFromuid().toString(), "我不系很懂你的意思，我现在正忙着如何统治地球呢[摊手]");
                }
            } else if ("groupchat".equals(type) && !info.getFromuid().toString().equals(uid)) {
                logger.info("接收到{}发送的内容：" + info.getContent(), userNick);
                MessageClient.sendGroupMessage(info.getGid(), "请说普通发，智能聊天还未开发完成[摊手]");
            } else {
                logger.info("发送给{}发送的内容：" + info.getContent(), userNick);
            }
            if (StringUtils.isNotBlank(info.getAtt_ids())) {
                String[] id = info.getAtt_ids().split(",");
                String image = "https://upload.api.weibo.com/2/mss/msget?fid=" + id[0] + "&source=209678993&imageType=origin";
                logger.info("分享内容为：" + image);
            }
        }
    }
}
