package com.freemanan.kubernetes.grey.client;

import com.freemanan.kubernetes.grey.common.Destination;
import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.thread.ThreadContext;
import java.util.List;

/**
 * @author Freeman
 */
public class Util {

    public static ThreadContext threadContext() {
        ThreadContext tc = new ThreadContext();

        Grey grey = new Grey();

        Destination master = new Destination();
        master.setService("master");
        master.setNamespace("default");
        master.setPort(8080);

        Destination slave = new Destination();
        slave.setService("slave");
        slave.setNamespace("default");
        slave.setPort(8080);

        grey.setMaster(master);
        grey.setFeatures(List.of(slave));

        tc.setGreys(List.of(grey));
        return tc;
    }
}
