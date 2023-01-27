package com.freemanan.kubernetes.grey.client.feign;

import feign.Feign;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;

/**
 * @author Freeman
 */
public class GreyFeignBuilderCustomizer implements FeignBuilderCustomizer {
    @Override
    public void customize(Feign.Builder builder) {
        builder.addCapability(new GreyCapability());
    }
}
