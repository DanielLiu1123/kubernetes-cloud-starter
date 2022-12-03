package com.freemanan.example.controller;

import com.freemanan.example.PricingProperties;
import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Freeman
 */
@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private PricingProperties pricingProperties;

    @Autowired
    private KubernetesConfigProperties kubernetesConfigProperties;

    @GetMapping("/price")
    public Object price() {
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
        return pricingProperties.getPrice();
    }

    @GetMapping("/namespace")
    public Object namespace() {
        return kubernetesConfigProperties.getNamespace();
    }
}
