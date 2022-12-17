package com.freemanan.kubernetes.grey.thread;

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
}
