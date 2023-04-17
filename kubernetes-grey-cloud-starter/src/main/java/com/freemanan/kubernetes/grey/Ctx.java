package com.freemanan.kubernetes.grey;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public Ctx(Map<String, String> headers) {
        this.headers = headers;
        this.timeMs = System.currentTimeMillis();
        this.timeStr = new SimpleDateFormat(TIME_STR_FORMAT).format(new Date(timeMs));
    }
}
