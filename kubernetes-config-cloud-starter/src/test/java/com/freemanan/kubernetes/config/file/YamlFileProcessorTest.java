package com.freemanan.kubernetes.config.file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * {@link YamlFileProcessor} tester.
 */
class YamlFileProcessorTest {

    /**
     * {@link YamlFileProcessor#generate(String, String)}
     */
    @Test
    void generate_whenSingleDocument() {
        String yaml =
                """
                username: admin
                password: "666"
                hobbies:
                  - reading
                  - writing
                  """;
        EnumerablePropertySource<?> ps = new YamlFileProcessor().generate("test_generate", yaml);
        assertThat(ps.getPropertyNames()).hasSize(4);
        assertThat(ps.getProperty("username")).isEqualTo("admin");
        assertThat(ps.getProperty("password")).isEqualTo("666");
        assertThat(ps.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(ps.getProperty("hobbies[1]")).isEqualTo("writing");
    }

    /**
     * {@link YamlFileProcessor#generate(String, String)}
     */
    @Test
    void generate_whenMultipleDocuments() {
        String yaml =
                """
                username: admin
                password: "666"
                hobbies:
                  - reading
                  - writing
                ---
                username: adminn
                password: "6666"
                hobbies:
                  - readingg
                  - writingg
                """;
        EnumerablePropertySource<?> ps = new YamlFileProcessor().generate("test_generate", yaml);
        assertThat(ps.getPropertyNames()).hasSize(4);
        // first document 'win'
        assertThat(ps.getProperty("username")).isEqualTo("admin");
        assertThat(ps.getProperty("password")).isEqualTo("666");
        assertThat(ps.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(ps.getProperty("hobbies[1]")).isEqualTo("writing");
    }
}
