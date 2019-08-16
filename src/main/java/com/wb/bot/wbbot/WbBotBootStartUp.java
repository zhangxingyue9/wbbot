package com.wb.bot.wbbot;

import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.core.FocusMonitor;
import com.wb.bot.wbbot.core.HttpProxy;
import com.wb.bot.wbbot.core.LoginClient;
import com.wb.bot.wbbot.core.MessageClient;
import com.wb.bot.wbbot.netty.HttpServer;
import com.wb.bot.wbbot.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class WbBotBootStartUp {

    private static final Logger logger = LoggerFactory.getLogger(WbBotBootStartUp.class);

    private static WbBotBootStartUp BootStartUp = null;

    private final static String QR_PATH = "/opt/";

    private WbBotBootStartUp() {
        //强制通信协议
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    public static WbBotBootStartUp init() {
        if (BootStartUp == null) {
            synchronized (WbBotBootStartUp.class) {
                logger.info("初始化进程");
                BootStartUp = new WbBotBootStartUp();
                BootStartUp.doLogin();
            }
        }
        return BootStartUp;
    }

    private void doLogin()  {
        LoginClient service = new LoginClient();
        try {
            service.login(QR_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(3);
        FocusMonitor monitor = new FocusMonitor();
        ActionResult instance = ActionResult.getInstance();
        MessageClient client = new MessageClient();
        //开启会话客户端
        executor.execute(()->{
            while (true) {
                if (instance.isAlive()) {
                    client.onOpen();
                    new Thread(new HttpProxy()).start();//开启ip代理池
                    monitor.signalAll();
                    break;
                }
                SleepUtils.sleep(5000);
            }
        });
        //开启会话监测
        executor.execute(()->{
            while (true) {
                if (ActionResult.getInstance().isAlive()&&ActionResult.getInstance().isSubscribe()) {
                    client.onMessage();
                    break;
                }
                SleepUtils.sleep(5000);
            }
        });
        executor.execute(WbBotBootStartUp::init);
        //开启动态监听
        monitor.start(10);
        int port = 7000;
        try {
            new HttpServer().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
