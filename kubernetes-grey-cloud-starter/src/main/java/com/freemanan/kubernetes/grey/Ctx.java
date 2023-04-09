package com.freemanan.kubernetes.grey;

import java.util.Map;
import lombok.Data;

/**
 * @author Freeman
 * @since 2023/4/9
 */
@Data
public class Ctx {
    public static final String TIME_STR_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final Map<String, String> headers;
    private final long timeMs;
    private final String timeStr;
}
