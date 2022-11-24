package com.freemanan.kubernetes.config.file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * {@link PropertiesFileProcessor} tester.
 */
class PropertiesFileProcessorTest {

    /**
     * {@link PropertiesFileProcessor#generate(String, String)}
     */
    @Test
    void generate() {
        String properties = "username=admin\n" + "password=666\n" + "hobbies[0]=reading\n" + "hobbies[1]=writing";
        EnumerablePropertySource<?> ps = new PropertiesFileProcessor().generate("test_generate", properties);

        assertThat(ps.getPropertyNames()).hasSize(4);
        assertThat(ps.getProperty("username")).isEqualTo("admin");
        assertThat(ps.getProperty("password")).isEqualTo("666");
        assertThat(ps.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(ps.getProperty("hobbies[1]")).isEqualTo("writing");
    }
}
