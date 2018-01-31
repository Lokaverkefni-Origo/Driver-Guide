package com.etrausta.driverguide;

import java.util.Date;

/**
 * Created by d on 1/31/2018.
 */

public class ChatMessage {

    private String msgTxt;
    private String msgUser;
    private long msgTime;

    public ChatMessage(String msgTxt, String msgUser, long msgTime) {
        this.msgTxt = msgTxt;
        this.msgUser = msgUser;
        //current time
        msgTime = new Date().getTime();
    }

    public ChatMessage(String s, String email) {

    }

    public String getMsgTxt() {
        return msgTxt;
    }

    public void setMsgTxt(String msgTxt) {
        this.msgTxt = msgTxt;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
