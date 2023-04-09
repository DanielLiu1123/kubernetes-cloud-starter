package com.freemanan.kubernetes.grey.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Freeman
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Target {
    private String authority;
    private double weight;
}
