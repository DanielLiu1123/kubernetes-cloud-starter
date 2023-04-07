package com.freemanan.kubernetes.grey.client;

import com.freemanan.kubernetes.grey.common.Destination;
import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.thread.Context;
import java.util.List;

/**
 * @author Freeman
 */
public class Util {

    public static Context threadContext() {
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

        return new Context(List.of(grey));
    }
}
