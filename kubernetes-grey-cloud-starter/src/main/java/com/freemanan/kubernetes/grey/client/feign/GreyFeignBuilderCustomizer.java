package com.freemanan.kubernetes.grey.client.feign;

import feign.Feign;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;

/**
 * @author Freeman
 * @since 2023/1/12
 */
public class GreyFeignBuilderCustomizer implements FeignBuilderCustomizer {
    @Override
    public void customize(Feign.Builder builder) {
        builder.addCapability(new GreyCapability());
    }
}
