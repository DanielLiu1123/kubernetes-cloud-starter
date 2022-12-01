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
        String json =
                """
                {
                  "username": "admin",
                  "password": "666",
                  "hobbies": [
                    "reading",
                    "writing"
                  ]
                }
                """;
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
        String jsonArray =
                """
                [
                  {
                    "username": "admin",
                    "password": "666",
                    "hobbies": [
                      "reading",
                      "writing"
                    ]
                  },
                  {
                    "username": "root",
                    "password": "888",
                    "hobbies": [
                      "reading",
                      "writing",
                      "coding"
                    ]
                  }
                ]
                """;
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
