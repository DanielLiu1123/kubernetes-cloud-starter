package com.freemanan.kubernetes.grey.common.util;

import static com.freemanan.kubernetes.grey.common.util.GreyUtil.grey;
import static com.freemanan.kubernetes.grey.common.util.GreyUtil.greyUri;
import static com.freemanan.kubernetes.grey.common.util.GreyUtil.host;
import static com.freemanan.kubernetes.grey.common.util.GreyUtil.namespace;
import static com.freemanan.kubernetes.grey.common.util.GreyUtil.port;
import static com.freemanan.kubernetes.grey.common.util.GreyUtil.service;
import static org.assertj.core.api.Assertions.assertThat;

import com.freemanan.kubernetes.commons.K8s;
import com.freemanan.kubernetes.grey.common.Destination;
import com.freemanan.kubernetes.grey.common.Grey;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * {@link GreyUtil} tester.
 */
class GreyUtilTest {

    /**
     * {@link GreyUtil#grey(URI, Grey)}
     */
    @Test
    void testGrey() throws Exception {
        // Setup
        final Grey grey = new Grey();
        final Destination master = new Destination();
        master.setService("example");
        master.setNamespace("com");
        master.setPort(null);
        master.setWeight(null);
        grey.setMaster(master);
        final Destination destination = new Destination();
        destination.setService("service");
        destination.setNamespace("namespace");
        destination.setPort(null);
        destination.setWeight(null);
        grey.setFeatures(Arrays.asList(destination));

        // Run the test
        final URI result = grey(new URI("https://example.com/"), grey);

        // Verify the results
        assertThat(result).isEqualTo(new URI("https://service.namespace/"));
    }

    /**
     * {@link GreyUtil#grey(URI, Grey)}
     */
    @Test
    void testGrey_whenHasWeight() {
        // Setup
        final Grey grey = new Grey();
        final Destination master = new Destination();
        master.setService("example");
        master.setNamespace("com");
        master.setPort(null);
        master.setWeight(null);
        grey.setMaster(master);
        final Destination destination1 = new Destination();
        destination1.setService("service1");
        destination1.setNamespace("namespace1");
        destination1.setPort(null);
        destination1.setWeight(30.0);
        final Destination destination2 = new Destination();
        destination2.setService("service2");
        destination2.setNamespace("namespace2");
        destination2.setPort(null);
        destination2.setWeight(30.0);
        grey.setFeatures(Arrays.asList(destination1, destination2));

        // Run the test
        IntStream.range(0, 20).forEach(value -> {
            final URI result = grey(URI.create("https://example.com/"), grey);
            System.out.println(result);
        });
    }

    /**
     * {@link GreyUtil#greyUri(Destination, URI)}
     */
    @Test
    void testGreyUri() throws Exception {
        Destination destination = new Destination();
        destination.setService("service");
        destination.setNamespace("namespace");
        destination.setPort(null);
        destination.setWeight(null);
        URI result = greyUri(destination, new URI("https://example.com/"));

        assertThat(result).isEqualTo(new URI("https://service.namespace/"));

        destination = new Destination();
        destination.setService("service");
        destination.setNamespace("namespace");
        destination.setPort(8080);
        destination.setWeight(null);
        result = greyUri(destination, new URI("https://example.com:433/"));

        assertThat(result).isEqualTo(new URI("https://service.namespace:8080/"));

        destination = new Destination();
        destination.setService("service");
        destination.setNamespace("namespace");
        destination.setPort(8080);
        destination.setWeight(null);
        result = greyUri(destination, new URI("https://example.com/"));

        assertThat(result).isEqualTo(new URI("https://service.namespace/"));

        destination = new Destination();
        destination.setService("service");
        destination.setNamespace("namespace");
        destination.setPort(null);
        destination.setWeight(null);
        result = greyUri(destination, new URI("https://example.com:8080/"));

        assertThat(result).isEqualTo(new URI("https://service.namespace:8080/"));
    }

    /**
     * {@link GreyUtil#namespace(URI)}
     */
    @Test
    void testNamespace() throws Exception {
        assertThat(namespace(new URI("https://example.com/"))).isEqualTo("com");
        assertThat(namespace(new URI("https://example.com.xx/"))).isEqualTo("com");
        assertThat(namespace(new URI("https://example/"))).isEqualTo(K8s.currentNamespace());
    }

    /**
     * {@link GreyUtil#service(URI)}
     */
    @Test
    void testService() throws Exception {
        assertThat(service(new URI("https://example.com/"))).isEqualTo("example");
        assertThat(service(new URI("https://example.com.xx/"))).isEqualTo("example");
        assertThat(service(new URI("https://example-a.com.xx/"))).isEqualTo("example-a");
        assertThat(service(new URI("https://example-a.com.xx:8080/"))).isEqualTo("example-a");
    }

    /**
     * {@link GreyUtil#host(URI)}
     */
    @Test
    void testHost() throws Exception {
        assertThat(host(new URI("https://example.com:8080/"))).isEqualTo("example.com");
        assertThat(host(new URI("https://example.com"))).isEqualTo("example.com");
        assertThat(host(new URI("https://example.com.xx"))).isEqualTo("example.com.xx");
    }

    /**
     * {@link GreyUtil#port(URI)}
     */
    @Test
    void testPort() throws Exception {
        assertThat(port(new URI("https://example.com:8080/"))).isEqualTo(8080);
        assertThat(port(new URI("http://example.com"))).isEqualTo(null);
        assertThat(port(new URI("https://example.com.xx"))).isEqualTo(null);
    }

    @Test
    void testGetAuthority() {
        assertThat(URI.create("https://example.com:8080/xxx").getAuthority()).isEqualTo("example.com:8080");
        assertThat(URI.create("http://example.com/xx").getAuthority()).isEqualTo("example.com");
        assertThat(URI.create("http://example/").getAuthority()).isEqualTo("example");
    }
}
