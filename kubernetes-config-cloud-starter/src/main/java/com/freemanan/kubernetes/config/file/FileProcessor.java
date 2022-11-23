package com.freemanan.kubernetes.config.file;

import org.springframework.core.env.EnumerablePropertySource;

/**
 * @author Freeman
 */
public interface FileProcessor {

    /**
     * Whether the fileName is supported by the processor
     *
     * @param fileName file name
     * @return true if hit
     */
    boolean hit(String fileName);

    /**
     * Generate property source from file content.
     *
     * @param name    property source name
     * @param content file content
     * @return property source
     */
    EnumerablePropertySource<?> generate(String name, String content);
}
