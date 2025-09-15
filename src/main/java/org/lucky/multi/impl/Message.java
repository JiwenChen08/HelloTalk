package org.lucky.multi.impl;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message implements Serializable {

    public static String CL = "cl";
    public static String MS = "ms";

    /**
     * 消息类型 type='cl' 是控制字段
     * 消息类型 type='ms' 是消息
     */
    private String type;
    /**
     * senderId
     */
    private String senderId;
    /**
     * receiverId
     */
    private String receiverId;
    /**
     * the message content
     */
    private String text;
    /**
     * timestamp
     */
    private Long ts;
}
