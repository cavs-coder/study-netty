package com.study.netty.util;

import com.study.netty.domain.MsgBody;

/**
 * @author fuguangwei
 * @date 2024-09-18
 */
public class MsgUtil {

    /**
     * 构建protoBuf消息体
     */
    public static MsgBody buildMsg(String channelId, String msgInfo) {
        MsgBody.Builder msg = MsgBody.newBuilder();
        msg.setChannelId(channelId);
        msg.setMsgInfo(msgInfo);
        return msg.build();
    }
}
