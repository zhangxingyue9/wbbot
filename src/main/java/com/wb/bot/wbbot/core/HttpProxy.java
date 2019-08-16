package com.wb.bot.wbbot.core;

import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.HttpUtils;
import com.wb.bot.wbbot.utils.SleepUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpProxy implements Runnable {

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    static List<HttpHost> proxyList = new CopyOnWriteArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(HttpProxy.class);

    private static final String SPIDER_URL = "http://www.66ip.cn/nmtq.php?getnum=300&isp=0&anonymoustype=3&start=&ports=&export=&ipaddress=&area=1&proxytype=2&api=66ip";


    private static AtomicInteger count = new AtomicInteger(0);

    /**
     * 获取代理ip
     */
    private static List<HttpHost> getHttpHostList() {
        List<HttpHost> httpHosts = new CopyOnWriteArrayList<>();
        ExecutorService checkHttpHostExecutor = Executors.newCachedThreadPool();
        try {
            String data = Jsoup.connect(SPIDER_URL)
                    .header("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Mobile Safari/537.36")
                    .header("Host","www.66ip.cn")
                    .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                    .header("Cookie","__jsluid_h=6e30bd64a60190047aab491354ed10d2; Hm_lvt_1761fabf3c988e7f04bec51acd4073f4=1565846385;" +
                            " Hm_lpvt_1761fabf3c988e7f04bec51acd4073f4=1565846907; __jsl_clearance=1565847583.458|0|%2B7NhDgFs5JEslPvoAfpqVbjKEHM%3D")
                    .execute().body();
            documentParse(data, httpHosts, checkHttpHostExecutor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("检测IP数量：{}", count);
        checkHttpHostExecutor.shutdown();
        while (true) {
            if (checkHttpHostExecutor.isTerminated()) {
                logger.info("子线程执行完毕,收集有效ip：{}", httpHosts.size());
                break;
            }
            logger.info("当前收集有效ip数量：{}", httpHosts.size());
            SleepUtils.sleep(10000);
        }
        count.set(0);
        return httpHosts;
    }

    /**
     * 数据解析
     */
    private static void documentParse(String data, List<HttpHost> httpHosts, ExecutorService checkHttpHostExecutor) {
        String[] ips = data.split("<br />\r\n\t\t");
        String splitString = ":";
        for (int i =1;i<ips.length-1;i++) {
            HttpHost host;
            try {
                String[] split = ips[i].split(splitString);
                host = new HttpHost(split[0], Integer.parseInt(split[1]));
                count.incrementAndGet();
            } catch (Exception e) {
                logger.error("解析失败 文本：{}", ips[i]);
                continue;
            }
            HttpHost finalHost = host;
            checkHttpHostExecutor.execute(() -> checkHttpHost(finalHost, httpHosts));
        }
    }

    /**
     * ip检测
     */
    private static void checkHttpHost(HttpHost host, List<HttpHost> httpHosts) {
        Map<String, String> map = new HashMap<>(2);
        map.put("uid", "5353865587");
        String data = HttpUtils.doGet(config.news(), map, null, null, null, host, 2000);
        if (data != null) {
            httpHosts.add(host);
        }
    }

    @Override
    public void run() {
        while (true) {
            List<HttpHost> httpHostList = getHttpHostList();
            if (httpHostList == null || httpHostList.size() == 0) {
                logger.info("ip池数据无法获取最新的代理ip");
                break;
            }
            if (httpHostList.size() < 10) {
                continue;
            }
            proxyList = httpHostList;
            SleepUtils.sleep(120000);
        }
    }

}
