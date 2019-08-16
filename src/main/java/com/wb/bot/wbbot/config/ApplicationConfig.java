package com.wb.bot.wbbot.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.aeonbits.owner.Config.DisableableFeature.PARAMETER_FORMATTING;


@Sources("classpath:application.properties")
public interface ApplicationConfig extends Config {

    @Key("weibo.api.hand_shake")
    String handShake();

    @Key("weibo.api.subscribe")
    String subscribe();

    @Key("weibo.api.login_qr")
    String loginQr(String str);

    @Key("weibo.api.check_scan")
    String checkScan(String id,String totalSeconds);

    @Key("weibo.api.secret_login")
    String secretLogin(String alt,String totalSeconds);

    @Key("weibo.api.secret_login_h5")
    @Config.DisableFeature(PARAMETER_FORMATTING)
    String secretLoginH5();

    @Key("weibo.api.user_info")
    String userInfo(Long userId);

    @Key("weibo.api.new_user_info")
    String newUserInfo(Long userId,Long time);

    @Key("weibo.api.news")
    String news();

    @Key("weibo.api.user_config")
    String userConfig();

    @Key("weibo.api.liked")
    String liked();

    @Key("weibo.api.group.send")
    String groupSend();

    @Key("weibo.api.group.kick")
    String groupKick();

    @Key("weibo.api.group.join")
    String groupJoin();

    @Key("weibo.api.comment")
    String comment();

    @Key("weibo.api.connect")
    String connect();

    @Key("weibo.api.send_message")
    String sendMessage();

    @Key("weibo.api.add")
    String addNews(String time);

    @Key("weibo.parameter.jsonp")
    String jsonp();

    @Key("weibo.parameter.subscription")
    String subscription();

    @Key("weibo.parameter.handshake_channel")
    String handshakeChannel();

    @Key("weibo.parameter.subscription_channel")
    String subscriptionChannel();

    @Key("weibo.parameter.connect_channel")
    String connectChannel();

    @Key("weibo.parameter.minimum_version")
    String minimumVersion();

    @Key("weibo.parameter.connection_type")
    String connectionType();

    @Key("weibo.head.referer.name")
    String refererName();

    @Key("weibo.head.referer.value")
    String refererValue();

    @Key("weibo.head.agent.name")
    String agentName();

    @Key("weibo.head.agent.value")
    String agentValue();

    @Key("weibo.api.group.create")
    String groupCreate();

    @Key("weibo.api.group.query")
    String groupQuery(Long id,Long time);

    @Key("weibo.api.group.message")
    String groupMessages(int count,Long id,Long time);

    @Key("weibo.channel.supported_connection_types")
    @Separator(";")
    List<String> supportedConnectionTypes();

    @Key("weibo.focus.uid")
    String focusUid();

    @Key("weibo.focus.content")
    String focusContent();


    class RequestHeadConfig {
       public static Map<String, String> RequestHeadMap;
        static {
            RequestHeadMap = new HashMap<>();
            ApplicationConfig config = ConfigFactory.create(ApplicationConfig.class);
            RequestHeadMap.put(config.refererName(), config.refererValue());
            RequestHeadMap.put(config.agentName(), config.agentValue());
        }
    }
}
