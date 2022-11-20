package com.freemanan.example;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 */
@Data
@ConfigurationProperties(prefix = "fm.pricing")
public class PricingProperties {
    private double price;
}
