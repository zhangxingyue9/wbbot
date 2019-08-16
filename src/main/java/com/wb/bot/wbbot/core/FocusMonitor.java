package com.wb.bot.wbbot.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.DateUtils;
import com.wb.bot.wbbot.utils.HttpUtils;
import com.wb.bot.wbbot.utils.SleepUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FocusMonitor {

    /**
     * 起始监控事件
     */
    private static long startTime;

    /**
     * 微博动态id集合
     */
    private static CopyOnWriteArrayList<String> ids = new CopyOnWriteArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(FocusMonitor.class);

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    private static Map<String, String> requestHeadMap = ApplicationConfig.RequestHeadConfig.RequestHeadMap;

    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    static {
        startTime = DateUtils.getTotalTime();
    }

    /**
     * 评论
     *
     * @param id 动态id
     */
    private void comment(String id, String content) {

        String token = getToken();
        if (StringUtils.isNotBlank(token)) {
            Map<String, String> perMap = new HashMap<>();
            perMap.put("id", id);
            perMap.put("mid", id);
            perMap.put("content", content);
            perMap.put("st", token);
            String data = HttpUtils.doPost(config.comment(), perMap,
                    ActionResult.getInstance().getCookieStoreH5(), requestHeadMap, null);
            logger.info("comment：{}", data);
        }
    }

    /**
     * 点赞
     *
     * @param id 动态id
     */
    private void liked(String id) {
        String token = getToken();
        logger.info("token值：{}", token);
        if (StringUtils.isNotBlank(token)) {
            Map<String, String> perMap = new HashMap<>();
            perMap.put("id", id);
            perMap.put("attitude", "heart");
            perMap.put("st", token);
            String data = HttpUtils.doPost(config.liked(), perMap,
                    ActionResult.getInstance().getCookieStoreH5(), requestHeadMap, null);
            logger.info("liked：{}", data);
        }
    }

    /**
     * 动态查询
     *
     * @param focusUid 关注的用户id
     * @return 返回最新动态id
     */
    public String news(String focusUid, HttpHost httpHost) {
        String data;
        JSONObject jsonObject;
        SimpleDateFormat sf1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
        Map<String, String> perMap = new HashMap<>();
        perMap.put("uid", focusUid);
        try {
            data = HttpUtils.doGet(config.news(), perMap, null, requestHeadMap, null, httpHost,3000);
        } catch (Exception e) {
            return null;
        }
        if (StringUtils.isBlank(data)) {
            return data;
        }
        try {
            jsonObject = JSONObject.parseObject(data);
        } catch (Exception e) {
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("statuses");
        List<Map> news = jsonArray.toJavaList(Map.class);
        if (news.size() != 0) {
            return sortNews(news, sf1);
        }
        return null;
    }

    /**
     * 动态消息排序
     *
     * @param news 动态消息结果集
     * @param sf1  转换时间
     * @return 返回最新id
     */
    private String sortNews(List<Map> news, SimpleDateFormat sf1) {
        //创建微博时间字段
        String createdAt = "created_at";
        Stream<Map> sorted = news.stream().sorted((o1, o2) -> {
            String o1at = (String) o1.get(createdAt);
            String o2at = (String) o2.get(createdAt);
            try {
                if (sf1.parse(o1at).getTime() > sf1.parse(o2at).getTime() ) {
                    return -1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 1;
        });
        Optional<Map> optionalMap = sorted.findFirst();
        if (optionalMap.isPresent()) {
            Map map = optionalMap.get();
            try {
                String id = (String) map.get("id");
                if(sf1.parse((String) map.get(createdAt)).getTime()>startTime){
                    synchronized (this) {
                        if (!ids.contains(id)) {
                            ids.add(id);
                            return id;
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取用户token
     */
    public String getToken() {
        String dataJson = HttpUtils.doGet(config.userConfig(), null,
                ActionResult.getInstance().getCookieStoreH5(), requestHeadMap, null);
        JSONObject data = JSONObject.parseObject(dataJson);
        if (data.getInteger("ok") == 1 && data.getJSONObject("data").getBoolean("login")) {
            return data.getJSONObject("data").getString("st");
        }
        return null;
    }

    /**
     * 监听动态
     */
    private void monitor() {
        Random rand = new Random();
        while (true) {
            if (ActionResult.getInstance().isAlive()) {
                List<HttpHost> proxyList = HttpProxy.proxyList;
                if (proxyList == null || proxyList.size() < 10) {
                    SleepUtils.sleep(5000);
                } else {
                    //要监控的微博用户id
                    String id = news(config.focusUid(), proxyList.get(rand.nextInt(proxyList.size())));
                    if (StringUtils.isNotBlank(id)) {
                        logger.info(Thread.currentThread().getName() + "————————发现新动态：" + id);
                        liked(id);
                        comment(id, config.focusContent());
                    }
                }
            } else {
                logger.info("等待登陆：{}", Thread.currentThread().getName());
                try {
                    lock.lock();
                    condition.await();
                } catch (InterruptedException e) {
                    logger.error("登陆超时，{}线程销毁", Thread.currentThread().getName());
                    break;
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 线程启动项
     *
     * @param num 线程数量
     */
    public void start(int num) {
        for (int i = 0; i < num; i++) {
            executor.execute(this::monitor);
        }
    }

    /**
     * 锁唤醒
     */
    public void signalAll() {
        try {
            lock.lock();
            condition.signalAll();
            logger.info("当前已唤醒所有监控线程");
        } finally {
            lock.unlock();
        }
    }
}
