package com.freemanan.kubernetes.grey;

import com.freemanan.kubernetes.grey.common.Target;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author Freeman
 */
@Data
public class Grey {
    private Long id;

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

    private Map<String, List<Target>> rules;

    private String name;
    private Integer priority;
    private String description;
    private String tickets;

    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
