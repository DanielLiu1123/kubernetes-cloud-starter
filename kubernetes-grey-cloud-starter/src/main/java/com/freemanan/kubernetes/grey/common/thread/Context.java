package com.freemanan.kubernetes.grey.common.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.freemanan.kubernetes.grey.common.Grey;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Transfer thread context when switching threads.
 *
 * @author Freeman
 */
@Getter
@AllArgsConstructor
public class Context {
    private static final ThreadLocal<Context> holder = new TransmittableThreadLocal<>();

    private final List<Grey> greys;

    /**
     * Get greys in thread context.
     *
     * @return greys, may be null
     */
    public static List<Grey> greys() {
        Context context = Context.get();
        if (context == null) {
            return null;
        }
        return context.getGreys();
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
