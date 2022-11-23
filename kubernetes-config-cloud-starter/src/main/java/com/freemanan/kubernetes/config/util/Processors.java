package com.freemanan.kubernetes.config.util;

import com.freemanan.kubernetes.config.file.FileProcessor;
import com.freemanan.kubernetes.config.file.JsonFileProcessor;
import com.freemanan.kubernetes.config.file.PropertiesFileProcessor;
import com.freemanan.kubernetes.config.file.YamlFileProcessor;
import java.util.Arrays;
import java.util.List;

/**
 * @author Freeman
 */
public final class Processors {

    private Processors() {
        throw new UnsupportedOperationException("No Processors instances for you!");
    }

    private static final List<FileProcessor> processors =
            Arrays.asList(new YamlFileProcessor(), new PropertiesFileProcessor(), new JsonFileProcessor());

    public static List<FileProcessor> fileProcessors() {
        return processors;
    }
}
