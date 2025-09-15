package org.lucky.utils;

import org.lucky.multi.impl.Message;

public class MessageUtils {


    public static Message ctrlShutdown(String senderId){
        return ctrlMsg("shutdown", senderId, null);
    }

    public static String ctrlMsgStr(String text, String senderId, String receiverId) {
        return JsonUtils.toJson(ctrlMsg(text, senderId, receiverId));
    }

    public static Message ctrlMsg(String text, String senderId, String receiverId) {
        return Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .text(text)
                .ts(System.currentTimeMillis())
                .type(Message.CL)
                .build();
    }

    public static String normalMsgStr(String text, String senderId, String receiverId) {
        return JsonUtils.toJson(normalMsg(text, senderId, receiverId));
    }

    public static Message normalMsg(String text, String senderId, String receiverId) {
        return Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .text(text)
                .ts(System.currentTimeMillis())
                .type(Message.MS)
                .build();
    }

}
