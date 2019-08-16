package com.wb.bot.wbbot.beans;


public class Info {

    private int dm_type;
    private int receiver_box_type;
    private Long fromuid;
    private String resource;
    private int media_type;
    private String comment;
    private long time;
    private String dmid;
    private String content;
    private String att_ids;
    private String gid;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAtt_ids() {
        return att_ids;
    }

    public void setAtt_ids(String att_ids) {
        this.att_ids = att_ids;
    }

    public void setDm_type(int dm_type) {
        this.dm_type = dm_type;
    }
    public int getDm_type() {
        return dm_type;
    }

    public void setReceiver_box_type(int receiver_box_type) {
        this.receiver_box_type = receiver_box_type;
    }
    public int getReceiver_box_type() {
        return receiver_box_type;
    }

    public void setFromuid(Long fromuid) {
        this.fromuid = fromuid;
    }
    public Long getFromuid() {
        return fromuid;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
    public String getResource() {
        return resource;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }
    public int getMedia_type() {
        return media_type;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public long getTime() {
        return time;
    }

    public void setDmid(String dmid) {
        this.dmid = dmid;
    }
    public String getDmid() {
        return dmid;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

}