package com.freemanan.kubernetes.grey.client.webclient;

import static org.assertj.core.api.Assertions.*;

import com.freemanan.kubernetes.grey.client.Util;
import com.freemanan.kubernetes.grey.common.thread.Context;
import com.freemanan.kubernetes.grey.common.thread.ReactorHookRegistrant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 * @author Freeman
 */
@ExtendWith(OutputCaptureExtension.class)
public class WebClientTest {

    @Test
    void testGrey_whenSync(CapturedOutput output) {
        Context.set(Util.threadContext());

        WebClient webClient =
                WebClient.builder().filter(new GreyExchangeFilterFunction()).build();

        assertThatExceptionOfType(WebClientRequestException.class).isThrownBy(() -> webClient
                .get()
                .uri("https://master.default:8080/typicode/demo/posts/1")
                .retrieve()
                .bodyToMono(String.class)
                .block());

        Context.remove();

        assertThat(output)
                .contains(
                        "[Grey] origin: https://master.default:8080/typicode/demo/posts/1, new: https://slave.default:8080/typicode/demo/posts/1");
    }

    @Test
    void testGrey_whenAsync(CapturedOutput output) throws InterruptedException {
        ReactorHookRegistrant.registerThreadLocalSupport(Context.class, Context::get, Context::set, Context::remove);

        Context.set(Util.threadContext());

        WebClient webClient =
                WebClient.builder().filter(new GreyExchangeFilterFunction()).build();
        webClient
                .get()
                .uri("https://master.default:8080/typicode/demo/posts/1")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> {
                    assertThat(e).isInstanceOf(WebClientRequestException.class);
                })
                .subscribe();

        Thread.sleep(1000);

        Context.remove();

        assertThat(output)
                .contains(
                        "[Grey] origin: https://master.default:8080/typicode/demo/posts/1, new: https://slave.default:8080/typicode/demo/posts/1");
    }
}
