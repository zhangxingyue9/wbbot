package com.wb.bot.wbbot.beans;

public class ConnectionBeanJson {

    private String channel;
    private String connectionType;
    private String id;
    private String clientId;
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public String getChannel() {
        return channel;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }
    public String getConnectionType() {
        return connectionType;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getClientId() {
        return clientId;
    }

}
