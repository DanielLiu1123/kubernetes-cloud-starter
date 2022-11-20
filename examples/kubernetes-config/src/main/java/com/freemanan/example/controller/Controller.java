package com.freemanan.example.controller;

import com.freemanan.example.PricingProperties;
import com.freemanan.kubernetes.config.KubernetesConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Freeman
 */
@RestController
public class Controller {

    @Autowired
    private PricingProperties pricingProperties;

    @Autowired
    private KubernetesConfigProperties kubernetesConfigProperties;

    @GetMapping("/price")
    public Object price() {
        return pricingProperties.getPrice();
    }

    @GetMapping("/namespace")
    public Object namespace() {
        return kubernetesConfigProperties.getDefaultNamespace();
    }
}
