package com.freemanan.kubernetes.grey.common.util;

import static com.freemanan.kubernetes.grey.common.util.GreyUtil.getMatchedRule;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.freemanan.kubernetes.grey.common.Target;
import java.net.URI;
import java.util.List;
import java.util.Map;
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

    /**
     * {@link GreyUtil#getMatchedRule(String, Map, boolean, String)}
     */
    @Test
    void testGetMatchedRule() {
        assertThat(getMatchedRule("todo", Map.of("todo", List.of(mock(Target.class))), true, ".ns"))
                .isNotEmpty();
        assertThat(getMatchedRule("todo", Map.of("todo.ns", List.of(mock(Target.class))), true, ".ns"))
                .isNotEmpty();
        assertThat(getMatchedRule("todo.ns", Map.of("todo", List.of(mock(Target.class))), true, ".ns"))
                .isNotEmpty();
        assertThat(getMatchedRule("todo:8080", Map.of("todo.ns:8080", List.of(mock(Target.class))), true, ".ns"))
                .isNotEmpty();
        assertThat(getMatchedRule("todo.ns:8080", Map.of("todo:8080", List.of(mock(Target.class))), true, ".ns"))
                .isNotEmpty();
        assertThat(getMatchedRule("todo.ns:8081", Map.of("todo:8080", List.of(mock(Target.class))), true, ".ns"))
                .isEmpty();
        assertThat(getMatchedRule("todo.ns", Map.of("todo:8080", List.of(mock(Target.class))), true, ".ns"))
                .isEmpty();
        assertThat(getMatchedRule("todo", Map.of("todo.nss", List.of(mock(Target.class))), true, ".ns"))
                .isEmpty();
    }

    @Test
    void testURI() {
        URI uri = URI.create("http://example.com:80/xxx");
        System.out.println(uri.getAuthority());
        System.out.println(uri.getHost());
        System.out.println(uri.getPort());
    }
}
