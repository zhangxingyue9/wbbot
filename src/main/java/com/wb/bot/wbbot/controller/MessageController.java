package com.wb.bot.wbbot.controller;

import com.wb.bot.wbbot.core.MessageClient;

public class MessageController {

    public String messageSend(String uid ,String text){
        return MessageClient.sendMessage(uid,text);
    }

}
