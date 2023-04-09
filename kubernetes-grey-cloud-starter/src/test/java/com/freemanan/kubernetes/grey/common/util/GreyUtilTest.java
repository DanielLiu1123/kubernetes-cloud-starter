package com.freemanan.kubernetes.grey.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.Test;

/**
 * {@link GreyUtil} tester.
 */
class GreyUtilTest {

    @Test
    void testGetAuthority() {
        assertThat(URI.create("https://example.com:8080/xxx").getAuthority()).isEqualTo("example.com:8080");
        assertThat(URI.create("http://example.com/xx").getAuthority()).isEqualTo("example.com");
        assertThat(URI.create("http://example/").getAuthority()).isEqualTo("example");
    }
}
