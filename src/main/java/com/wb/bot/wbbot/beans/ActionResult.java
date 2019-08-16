package com.wb.bot.wbbot.beans;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;


public class ActionResult {

    private static ActionResult instance;
    //登陆状态码
    private boolean isAlive = false;
    /**
     * 二维码id值
     */
    private String qrId = null;
    /**
     * sso登陆密匙
     */
    private String ticket = null;
    /**
     * 获取密匙标识码
     */
    private String alt = null;
    /**
     * 用户uid
     */
    private String uid = null;
    /**
     * 用户昵称
     */
    private String nick = null;
    /**
     * 用户uniqueid
     */
    private String uniqueid = null;
    /**
     * cookie
     */
    private CookieStore cookieStore;
    /**
     * h5cookie
     */
    private CookieStore cookieStoreH5;
    /**
     * 消息客户端id
     */
    private String clientId;
    /**
     * 自增id用来接收发送消息 get方法中进行自增操作
     */
    private int autoIncrementId = 1;
    /**
     * 二维码地址
     */
    private String qrSrc = null;

    private boolean isSubscribe;

    private ActionResult() {
        cookieStore = new BasicCookieStore();
        cookieStoreH5 = new BasicCookieStore();
    }

    public static ActionResult getInstance() {
        if (instance == null) {
            synchronized (ActionResult.class) {
                instance = new ActionResult();
            }
        }
        return instance;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public static void setInstance(ActionResult instance) {
        ActionResult.instance = instance;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public synchronized Integer getAutoIncrementId() {
        autoIncrementId++;
        return autoIncrementId;
    }

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(boolean subscribe) {
        isSubscribe = subscribe;
    }


    public CookieStore getCookieStoreH5() {
        return cookieStoreH5;
    }

    public String getQrSrc() {
        return qrSrc;
    }

    public void setQrSrc(String qrSrc) {
        this.qrSrc = qrSrc;
    }
}
