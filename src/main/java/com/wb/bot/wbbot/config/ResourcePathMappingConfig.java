package com.wb.bot.wbbot.config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:mapping.properties")
public interface ResourcePathMappingConfig extends Config {

    @Key("mapping.group.create")
    String groupCreate();

    @Key("mapping.group.message")
    String groupMessage();

    @Key("mapping.group.query")
    String groupQuery();

    @Key("mapping.group.join")
    String groupJoin();

    @Key("mapping.group.kick")
    String groupKick();

    @Key("mapping.message.send")
    String messageSend();
}
