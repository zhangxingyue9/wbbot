package com.wb.bot.wbbot.beans;

import org.apache.http.HttpHost;
public class HttpProxyBean {

    private String ip;

    private int port;

    private String city;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public HttpHost getHttpHost() {
        return new HttpHost(ip, port);
    }
}
