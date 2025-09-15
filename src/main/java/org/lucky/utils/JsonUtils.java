package org.lucky.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lucky.multi.impl.Message;

public class JsonUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper();


    public static <T> T parse(String content, Class<Message> valueTypeRef) {
        try {
            return (T) MAPPER.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
