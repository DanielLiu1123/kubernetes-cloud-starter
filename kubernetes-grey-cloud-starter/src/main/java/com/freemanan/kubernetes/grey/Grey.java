package com.freemanan.kubernetes.grey;

import java.util.Date;
import lombok.Data;

/**
 * @author Freeman
 */
@Data
public class Grey {
    private Long id;
    private String name;
    /**
     * variables:
     * <p> headers ({@code Map<String, String>}): request headers
     * <p> timeMs ({@code long}): current time in milliseconds
     * <p> timeStr ({@code String}): current time in {@code YYYY-MM-DD HH:mm:ss} format
     *
     * <p> header['x-user-id'] == '123'
     * <p> header['x-user-id'] == '123' || header['x-user-name'] == 'freeman'
     * <p>
     */
    private String predicate;

    private Integer priority;
    private String description;
    private String tickets;
    private String rules;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
