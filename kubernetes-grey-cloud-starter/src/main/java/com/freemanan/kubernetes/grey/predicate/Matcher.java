package com.freemanan.kubernetes.grey.predicate;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Freeman
 */
public interface Matcher {

    boolean match(HttpServletRequest request);
}
