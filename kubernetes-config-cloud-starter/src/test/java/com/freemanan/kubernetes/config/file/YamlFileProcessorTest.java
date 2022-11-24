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
        String yaml = "username: admin\n" + "password: \"666\"\n" + "hobbies:\n" + "  - reading\n" + "  - writing";
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
        String yaml = "username: admin\n" + "password: \"666\"\n"
                + "hobbies:\n"
                + "  - reading\n"
                + "  - writing\n"
                + "---\n"
                + "username: adminn\n"
                + "password: \"6666\"\n"
                + "hobbies:\n"
                + "  - readingg\n"
                + "  - writingg\n";
        EnumerablePropertySource<?> ps = new YamlFileProcessor().generate("test_generate", yaml);
        assertThat(ps.getPropertyNames()).hasSize(4);
        // first document 'win'
        assertThat(ps.getProperty("username")).isEqualTo("admin");
        assertThat(ps.getProperty("password")).isEqualTo("666");
        assertThat(ps.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(ps.getProperty("hobbies[1]")).isEqualTo("writing");
    }
}