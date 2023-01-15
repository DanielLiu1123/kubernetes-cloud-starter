package com.freemanan.kubernetes.grey.client.feign;

import feign.Capability;
import feign.Client;

/**
 * @author Freeman
 */
public class GreyCapability implements Capability {

    @Override
    public Client enrich(Client client) {
        return new GreyClient(client);
    }
}
