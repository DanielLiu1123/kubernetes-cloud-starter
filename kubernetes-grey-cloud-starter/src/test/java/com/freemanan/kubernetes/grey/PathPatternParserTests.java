package com.freemanan.kubernetes.grey;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @author Freeman
 */
public class PathPatternParserTests {

    @Test
    void testPathPatternParser() {
        PathPatternParser parser = new PathPatternParser();
        PathPattern pattern = parser.parse("/v*/user/**");
        System.out.println(pattern.matches(PathContainer.parsePath("/v10/user/")));
    }
}
