package com.wb.bot.wbbot.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wb.bot.wbbot.beans.ActionResult;
import com.wb.bot.wbbot.config.ApplicationConfig;
import com.wb.bot.wbbot.utils.DateUtils;
import com.wb.bot.wbbot.utils.HttpUtils;
import com.wb.bot.wbbot.utils.StrUtils;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class LoginComponent {

    private static final Logger logger = LoggerFactory.getLogger(LoginComponent.class);

    private static ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);

    private static Map<String, String> requestHeadMap = ApplicationConfig.RequestHeadConfig.RequestHeadMap;

    /**
     * 获取二维码
     * @return 拼接好的二维码地址
     */
    static String getQrCodeAddress() {
        //状态值
        String retCode = "retcode";
        Long successRetCode = 20000000L;
        //获取当前时间戳
        long totalSeconds = DateUtils.getTotalSeconds();
        String data = HttpUtils.doGet(config.loginQr("STK_" + totalSeconds), null, null,
                null, null);
        if (data != null) {
            JSONObject jsonObject = JSON.parseObject(StrUtils.substring(data, "(", ");"));
            if (jsonObject.containsKey(retCode) && jsonObject.getLong(retCode) .equals(successRetCode) ) {
                //保存二维码id值 用来检验是否扫码
                ActionResult.getInstance().setQrId(jsonObject.getJSONObject("data").get("qrid").toString());
                return "https:" + jsonObject.getJSONObject("data").get("image").toString();
            }
        }
        return data;
    }

    /**
     * 二维码保存
     * @param qrUrl 二维码地址
     * @param qrPath 二维码保存路径
     */
    static String getQrImagePath(String qrUrl, String qrPath) throws Exception {
        //判断当前目录是否存在
        File f = new File(qrPath);
        if(!f.exists()){
           if(!f.mkdirs()){
               throw new Exception("创建目录失败");
           }
        }
        //随机生成二维码文件保存地址
        String qrSrc = qrPath + UUID.randomUUID() + ".jpg";
        if (HttpUtils.downloadFile(qrUrl, qrSrc)) {
            ActionResult.getInstance().setQrSrc(qrSrc);
            return qrSrc;
        }
        return null;
    }

    /**
     * 二维码扫码检测
     */
    static  boolean checkScanQr() {
        //状态值
        String retCode = "retcode";
        Long successRetCode = 20000000L;
        String qrId = ActionResult.getInstance().getQrId();
        String totalSeconds = DateUtils.getTotalSeconds().toString();
        if (StringUtils.isNotBlank(qrId)) {
            String data = HttpUtils.doGet(config.checkScan(qrId,totalSeconds), null,
                    null, null, null);
            if (StringUtils.isBlank(data)) {
                return false;
            }
            JSONObject jsonObject = JSON.parseObject(StrUtils.substring(data, "(", ");"));
            if (jsonObject.getLong(retCode) .equals(successRetCode)) {
                ActionResult.getInstance().setAlive(true);
                System.out.println("扫码完毕，开始解析alt值");
                ActionResult.getInstance().setAlt(jsonObject.getJSONObject("data").getString("alt"));
                return ActionResult.getInstance().isAlive();
            }
            logger.info(jsonObject.get("msg").toString());
        }
        return false;
    }

    /**
     * pc登陆	jQuery112407155642677625089_1564105077524([{
     */
    static boolean doLogin() {
        Long totalSeconds = DateUtils.getTotalSeconds();
        ActionResult instance = ActionResult.getInstance();
        if (instance.getAlt() == null) {
            return false;
        }
        String url = config.secretLogin(URLEncoder.encode(instance.getAlt()),totalSeconds.toString());
        String data = HttpUtils.doGet(url, null, null, null,
                ActionResult.getInstance().getCookieStore());
        if (StringUtils.isBlank(data)) {
            return false;
        }
        data = StrUtils.substring(data, "(", ");");

        return sso(data);
    }

    /**
     * sso登陆
     */
    private static boolean sso(String data) {
        //状态值
        String retCode = "retcode";
        int successRetCode = 0;
        String result = "result";
        JSONObject jsonObject = JSON.parseObject(data);
        ActionResult instance = ActionResult.getInstance();
        //0为模拟登陆成功
        if (jsonObject.containsKey(retCode) && jsonObject.getInteger(retCode) == successRetCode) {
            instance.setUid(jsonObject.getString("uid"));
            instance.setNick(jsonObject.getString("nick"));
            JSONArray domainUrlList = jsonObject.getJSONArray("crossDomainUrlList");
            //获取最后一个角标的url地址 此地址为sso登陆地址 保存cookie
            List<String> list = domainUrlList.toJavaList(String.class);
            String ssoUrl = list.get(list.size() - 1);
            String loginData = HttpUtils.doGet(ssoUrl, null, null, null,
                    instance.getCookieStore());
            loginData = StrUtils.substring(loginData, "(", ");");
            JSONObject ssoData = JSONObject.parseObject(loginData);
            if (ssoData.getBoolean(result)) {
                instance.setUniqueid(ssoData.getJSONObject("userinfo").getString("uniqueid"));
                return true;
            }
        }
        return false;
    }

    /**
     * h5登陆
     */
    static boolean doLoginH5() {
        CookieStore cookieStore = ActionResult.getInstance().getCookieStore();
        CookieStore cookieStoreH5 = ActionResult.getInstance().getCookieStoreH5();

        String data = HttpUtils.doGet(config.secretLoginH5(), null,
                cookieStore, requestHeadMap, cookieStoreH5);
        if (StringUtils.isBlank(data)) {
            return false;
        }
        data = StrUtils.substring(data, "location.replace(\"", "\");");
        logger.info("H5登陆信息{}", data);
        HttpUtils.doGet(data, null, cookieStore, requestHeadMap, cookieStoreH5);
        HttpUtils.doGet("https://m.weibo.cn/", null, cookieStoreH5, null, cookieStoreH5);
        return true;
    }
}
