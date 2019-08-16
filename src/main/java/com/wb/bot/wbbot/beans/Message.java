package com.wb.bot.wbbot.beans;

import java.util.List;

public class Message {
    private String version;
    private String minimumVersion;
    private String channel;
    private List<String> supportedConnectionTypes;
    private Advice advice;
    private String id;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setMinimumVersion(String minimumVersion) {
        this.minimumVersion = minimumVersion;
    }

    public String getMinimumVersion() {
        return minimumVersion;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setSupportedConnectionTypes(List<String> supportedConnectionTypes) {
        this.supportedConnectionTypes = supportedConnectionTypes;
    }

    public List<String> getSupportedConnectionTypes() {
        return supportedConnectionTypes;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public Advice getAdvice() {
        return advice;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
