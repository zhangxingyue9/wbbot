package com.wb.bot.wbbot.beans;

public class RemarkUser {
    /**
     * 用户uid
     */
    private String uid;
    /**
     * 备注
     */
    private String remark;
    /**
     * 简写
     */
    private String jp;
    /**
     * 全拼
     */
    private String qp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getJp() {
        return jp;
    }

    public void setJp(String jp) {
        this.jp = jp;
    }

    public String getQp() {
        return qp;
    }

    public void setQp(String qp) {
        this.qp = qp;
    }
}
