package com.freemanan.kubernetes.grey;

import java.util.Date;

/**
 * @author Freeman
 */
public record Grey(
        Long id,
        String name,
        String predicate,
        Integer priority,
        String description,
        String tickets,
        String rules,
        Integer status,
        Date createdAt,
        Date updatedAt) {}
