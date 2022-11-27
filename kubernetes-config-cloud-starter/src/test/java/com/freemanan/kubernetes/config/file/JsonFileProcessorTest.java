package com.freemanan.kubernetes.config.file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * {@link JsonFileProcessor} tester.
 */
class JsonFileProcessorTest {

    /**
     * {@link JsonFileProcessor#generate(String, String)}
     */
    @Test
    void generate_whenJsonObject() {
        // spotless:off
        String json =
                "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"666\",\n" +
                "  \"hobbies\": [\n" +
                "    \"reading\",\n" +
                "    \"writing\"\n" +
                "  ]\n" +
                "}";
        // spotless:on
        EnumerablePropertySource<?> ps = new JsonFileProcessor().generate("test_generate", json);

        assertThat(ps.getPropertyNames()).hasSize(4);
        assertThat(ps.getProperty("username")).isEqualTo("admin");
        assertThat(ps.getProperty("password")).isEqualTo("666");
        assertThat(ps.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(ps.getProperty("hobbies[1]")).isEqualTo("writing");
    }

    /**
     * {@link JsonFileProcessor#generate(String, String)}
     */
    @Test
    void generate_whenJsonArray() {
        // spotless:off
        String jsonArray =
                "[\n" +
                "  {\n" +
                "    \"username\": \"admin\",\n" +
                "    \"password\": \"666\",\n" +
                "    \"hobbies\": [\n" +
                "      \"reading\",\n" +
                "      \"writing\"\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"username\": \"root\",\n" +
                "    \"password\": \"888\",\n" +
                "    \"hobbies\": [\n" +
                "      \"reading\",\n" +
                "      \"writing\",\n" +
                "      \"coding\"\n" +
                "    ]\n" +
                "  }\n" +
                "]";
        // spotless:on
        EnumerablePropertySource<?> ps = new JsonFileProcessor().generate("test_generate", jsonArray);

        // previous config wins
        assertThat(ps.getPropertyNames()).hasSize(5);
        assertThat(ps.getProperty("username")).isEqualTo("admin");
        assertThat(ps.getProperty("password")).isEqualTo("666");
        assertThat(ps.getProperty("hobbies[0]")).isEqualTo("reading");
        assertThat(ps.getProperty("hobbies[1]")).isEqualTo("writing");
        assertThat(ps.getProperty("hobbies[2]")).isEqualTo("coding");
    }
}
