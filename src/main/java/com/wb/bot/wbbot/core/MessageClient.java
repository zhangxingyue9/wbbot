package com.wb.bot.wbbot.core;

import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.beans.*;
import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.DateUtils;
import com.wb.bot.wbbot.utils.HttpUtils;
import com.wb.bot.wbbot.utils.SleepUtils;
import com.wb.bot.wbbot.utils.StrUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageClient {

    private static final Logger logger = LoggerFactory.getLogger(MessageClient.class);

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    private static Map<String, String> requestHeadMap = ApplicationConfig.RequestHeadConfig.RequestHeadMap;

    private static MessageProcessing messageProcessing = new MessageProcessing();

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private static ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    /**
     * 会话建立
     */
    public void onOpen() {
        if (handshake() && subscription()) {
            logger.info("即时会话建立成功");
        } else {
            logger.info("即时会话建立失败");
        }
    }

    /**
     * 消息监听
     */
    public void onMessage() {
        this.connect();
    }

    /**
     * 会话关闭
     */
    public void onClose() {
        ActionResult.getInstance().setSubscribe(false);
        logger.info("会话关闭");
        System.exit(0);
    }

    /**
     * 消息发送（个人）
     *
     * @param uid  用户id
     * @param text 文本
     */
    public synchronized static String sendMessage(String uid, String text) {
        Map<String, String> perMap = new HashMap<>(10);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientid", ActionResult.getInstance().getClientId());
        perMap.put("uid", uid);
        perMap.put("text", text);
        perMap.put("extensions", jsonObject.toJSONString());
        perMap.put("is_encoded", "0");
        perMap.put("decodetime", "1");
        perMap.put("source", "209678993");
        try {
            String data = HttpUtils.doPost(config.sendMessage(), perMap, ActionResult.getInstance().getCookieStore(),
                    requestHeadMap, ActionResult.getInstance().getCookieStore());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 消息发送（群组）
     *
     * @param gid  群组id
     * @param text 文本
     */
    public static void sendGroupMessage(String gid, String text) {
        Map<String, String> perMap = new HashMap<>(10);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientid", ActionResult.getInstance().getClientId());
        jsonObject.put("webchat", 1);
        perMap.put("id", gid);
        perMap.put("content", text);
        perMap.put("annotations", jsonObject.toJSONString());
        perMap.put("is_encoded", "0");
        perMap.put("source", "209678993");
        try {
            String s = HttpUtils.doPost(config.groupSend(), perMap, ActionResult.getInstance().getCookieStore(),
                    requestHeadMap, ActionResult.getInstance().getCookieStore());
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 握手
     */
    private boolean handshake() {
        Advice advice = new Advice();
        advice.setInterval(60000);
        advice.setInterval(0);
        Message message = new Message();
        message.setAdvice(advice);
        message.setChannel(config.handshakeChannel());
        message.setMinimumVersion(config.minimumVersion());
        message.setId(ActionResult.getInstance().getAutoIncrementId().toString());
        message.setSupportedConnectionTypes(config.supportedConnectionTypes());
        String perMessage = StrUtils.combination(message);
        Map<String, String> perMap = new HashMap<>(10);
        perMap.put("jsonp", config.jsonp());
        perMap.put("message", perMessage);
        String data = HttpUtils.doGet(config.handShake(), perMap, null, null,
                ActionResult.getInstance().getCookieStore());
        SleepUtils.sleep(2000);
        data = StrUtils.substring(data, "([", "])");
        JSONObject jsonObject = JSONObject.parseObject(data);
        if (jsonObject.getBoolean("successful")) {
            ActionResult.getInstance().setClientId(jsonObject.getString("clientId"));
            return true;
        }
        return false;
    }

    /**
     * 订阅
     */
    private boolean subscription() {
        Subscription subscription = new Subscription();
        subscription.setChannel(config.subscriptionChannel() + ActionResult.getInstance().getUid());
        subscription.setClientId(ActionResult.getInstance().getClientId());
        subscription.setId(ActionResult.getInstance().getAutoIncrementId().toString());
        subscription.setSubscription(config.subscription());
        String perSubscription = StrUtils.combination(subscription);
        Map<String, String> perMap = new HashMap<>(10);
        perMap.put("jsonp", config.jsonp());
        perMap.put("message", perSubscription);
        SleepUtils.sleep(2000);
        String data = HttpUtils.doGet(config.subscribe(), perMap, ActionResult.getInstance().getCookieStore(),
                requestHeadMap, ActionResult.getInstance().getCookieStore());
        data = StrUtils.substring(data, "([", "])");
        JSONObject jsonObject = JSONObject.parseObject(data);
        if (jsonObject.getBoolean("successful")) {
            ActionResult.getInstance().setSubscribe(true);
            return true;
        }
        return false;
    }

    /**
     * 连接->消息处理
     */
    private void connect() {
        ConnectionBeanJson connection = new ConnectionBeanJson();
        connection.setChannel(config.connectChannel());
        connection.setClientId(ActionResult.getInstance().getClientId());
        connection.setId(ActionResult.getInstance().getAutoIncrementId().toString());
        connection.setConnectionType(config.connectionType());
        String combination = StrUtils.combination(connection);
        Map<String, String> perMap = new HashMap<>(10);
        perMap.put("jsonp", config.jsonp());
        perMap.put("message", combination);
        perMap.put("_", DateUtils.getTotalTime().toString());
        String  data = HttpUtils.doGet(config.connect(), perMap, ActionResult.getInstance().getCookieStore(), requestHeadMap,
                ActionResult.getInstance().getCookieStore());
        if (StringUtils.isBlank(data)) {
            executor.execute(this::connect);
        }
        logger.info("数据" + data);
        executor.execute(this::connect);
        try {
            data = StrUtils.substring(data, config.jsonp() + "(", ")}");
        } catch (Exception e) {
            executor.execute(this::connect);
        }
        String finalData = data;
        singleExecutor.execute(() -> messageProcessing.handle(finalData));
    }

}
