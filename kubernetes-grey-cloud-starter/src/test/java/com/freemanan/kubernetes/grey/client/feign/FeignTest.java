package com.freemanan.kubernetes.grey.client.feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.freemanan.kubernetes.grey.client.Util;
import com.freemanan.kubernetes.grey.common.thread.Context;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Freeman
 * @since 2023/1/10
 */
@ExtendWith(OutputCaptureExtension.class)
public class FeignTest {

    @Test
    void testGrey(CapturedOutput output) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Config.class)
                .properties("logging.level.com.freemanan.kubernetes.grey=debug")
                .run();
        Context.set(Util.threadContext());

        FeignApi postApi = ctx.getBean(FeignApi.class);
        assertThatExceptionOfType(RetryableException.class).isThrownBy(() -> postApi.getPost(1));

        Context.remove();
        ctx.close();

        assertThat(output)
                .contains(
                        "[Grey] origin: https://master.default:8080/typicode/demo/posts/1, new: https://slave.default:8080/typicode/demo/posts/1");
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableFeignClients
    static class Config {}

    @FeignClient(value = "feign", url = "https://master.default:8080")
    interface FeignApi {

        @GetMapping("/typicode/demo/posts/{id}")
        String getPost(@PathVariable("id") int id);
    }
}
