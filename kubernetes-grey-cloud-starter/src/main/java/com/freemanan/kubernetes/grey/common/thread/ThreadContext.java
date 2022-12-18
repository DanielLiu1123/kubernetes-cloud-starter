package com.freemanan.kubernetes.grey.common.thread;

import com.freemanan.kubernetes.grey.common.Grey;
import java.util.List;

/**
 * Transfer thread context when switching threads.
 *
 * @author Freeman
 */
public class ThreadContext {
    private List<Grey> greys;

    public List<Grey> getGreys() {
        return greys;
    }

    public void setGreys(List<Grey> greys) {
        this.greys = greys;
    }

    /**
     * Get greys in thread context.
     *
     * @return greys, may be null
     */
    public static List<Grey> greys() {
        ThreadContext threadContext = ThreadContextHolder.get();
        if (threadContext == null) {
            return null;
        }
        return threadContext.getGreys();
    }
}
