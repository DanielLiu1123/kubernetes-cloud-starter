package com.freemanan.kubernetes.grey.common.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.freemanan.kubernetes.grey.common.Target;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Transfer thread context when switching threads.
 *
 * @author Freeman
 */
@Getter
@AllArgsConstructor
public final class Context {
    private static final ThreadLocal<Context> holder = new TransmittableThreadLocal<>();

    private final Map<String, List<Target>> greys;

    /**
     * Get greys in thread context.
     *
     * @return greys, never null
     */
    public static Map<String, List<Target>> greys() {
        Context context = Context.get();
        if (context == null) {
            return Collections.emptyMap();
        }
        Map<String, List<Target>> result = context.getGreys();
        return result != null ? result : Collections.emptyMap();
    }

    public static void set(Context context) {
        holder.set(context);
    }

    public static Context get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
