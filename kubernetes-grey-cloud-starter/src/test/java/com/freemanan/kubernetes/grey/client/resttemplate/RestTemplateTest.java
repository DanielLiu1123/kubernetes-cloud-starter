package com.freemanan.kubernetes.grey.client.resttemplate;

import static org.assertj.core.api.Assertions.*;

import com.freemanan.kubernetes.grey.client.Util;
import com.freemanan.kubernetes.grey.common.thread.ThreadContextHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(OutputCaptureExtension.class)
class RestTemplateTest {
    @Test
    void testGrey(CapturedOutput output) {
        ThreadContextHolder.set(Util.threadContext());

        RestTemplate restTemplate = new RestTemplateBuilder()
                .customizers(new GreyRestTemplateCustomizer())
                .build();

        assertThatExceptionOfType(ResourceAccessException.class)
                .isThrownBy(() ->
                        restTemplate.getForObject("https://master.default:8080/typicode/demo/posts/1", String.class));

        ThreadContextHolder.remove();

        assertThat(output)
                .contains(
                        "[Grey] origin: https://master.default:8080/typicode/demo/posts/1, new: https://slave.default:8080/typicode/demo/posts/1");
    }
}
