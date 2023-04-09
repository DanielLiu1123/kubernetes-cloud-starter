package com.freemanan.kubernetes.grey.client;

import com.freemanan.kubernetes.grey.common.Target;
import com.freemanan.kubernetes.grey.common.thread.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Freeman
 */
public class Util {

    public static Context threadContext() {
        Map<String, List<Target>> greys = new HashMap<>();
        greys.put("master.default:8080", List.of(new Target("slave.default:8080", 100)));

        return new Context(greys);
    }
}
