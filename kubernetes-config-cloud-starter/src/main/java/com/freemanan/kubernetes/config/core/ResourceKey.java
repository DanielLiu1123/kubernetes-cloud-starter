package com.freemanan.kubernetes.config.core;

/**
 * @author Freeman
 */
public record ResourceKey(String type, String name, String namespace, boolean refreshEnabled) {}
