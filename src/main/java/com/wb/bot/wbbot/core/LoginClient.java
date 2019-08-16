package com.wb.bot.wbbot.core;

import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.utils.SleepUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginClient {

    private static final Logger logger = LoggerFactory.getLogger(LoginClient.class);

    public void login(String qrPath){
        if (ActionResult.getInstance().isAlive()) {
            logger.info("wbbot登录了");
            return;
        }
        //调用api获取微博二维码地址
        String qrUrl = LoginComponent.getQrCodeAddress();
        if (StringUtils.isBlank(qrUrl)) {
            logger.error("获取二维码信息失败,线程关闭");
            System.exit(0);
        }
        //获取二维码图片信息
        String qrSrc = null;
        try {
            qrSrc = LoginComponent.getQrImagePath(qrUrl, qrPath);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (StringUtils.isBlank(qrSrc)) {
            logger.error("二维码下载失败,线程关闭");
            System.exit(0);
        }
        logger.info("进行扫码登录 当前二维码位置信息：" + qrSrc+"\n 或使用浏览器打开 http://localhost:7000");
        this.checkLogin();
        //扫码成功 通过alt值 获取ticket值  完成登陆并存储cookie
        this.doLogin();
    }

    private void doLogin() {
        if (StringUtils.isBlank(ActionResult.getInstance().getAlt())) {
            logger.error("alt不存在，请重启线程获取");
            System.exit(0);
        }
        if( LoginComponent.doLogin()&& LoginComponent.doLoginH5()){
            logger.info("登陆成功");
            ActionResult.getInstance().setAlive(true);
        }
    }

    private void checkLogin() {
        //检查次数
        int checkCount = 30;
        for (int count = 0; count <= checkCount; count++) {
            logger.info("进行第{}次扫码检测",count+1);
            if (LoginComponent.checkScanQr()) {
                return;
            }
            //检查等待时间
            int checkWaitingTime = 2000;
            SleepUtils.sleep(checkWaitingTime);
        }
        logger.error("扫码失败，请重启线程获取二维码");
        System.exit(0);
    }
}
