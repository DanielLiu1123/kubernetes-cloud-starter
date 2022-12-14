package com.freemanan.kubernetes.grey.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Freeman
 */
public class JsonUtil {
    private static final ObjectMapper om = new ObjectMapper()
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static String toJson(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(String jsonstring, Class<T> clazz) {
        try {
            return om.readValue(jsonstring, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(String jsonstring, TypeReference<T> typeReference) {
        try {
            return om.readValue(jsonstring, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
