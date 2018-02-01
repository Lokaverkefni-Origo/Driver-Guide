package com.etrausta.driverguide;

import java.util.Date;

/**
 * Created by d on 1/31/2018.
 */

public class ChatMessage {

    private String messageTxt;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageTxt, String messageUser) {
        this.messageTxt = messageTxt;
        this.messageUser = messageUser;
        //current time
        messageTime = new Date().getTime();
    }

    public ChatMessage() {

    }

    public String getMsgTxt() {
        return messageTxt;
    }

    public void setMsgTxt(String messageTxt) {
        this.messageTxt = messageTxt;
    }

    public String getMsgUser() {
        return messageUser;
    }

    public void setMsgUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMsgTime() {
        return messageTime;
    }

    public void setMsgTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
