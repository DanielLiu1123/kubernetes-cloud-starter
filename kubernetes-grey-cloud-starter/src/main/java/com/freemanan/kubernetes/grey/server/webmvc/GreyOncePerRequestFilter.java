package com.freemanan.kubernetes.grey.server.webmvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.thread.Context;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 */
public class GreyOncePerRequestFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(GreyOncePerRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String greyVersion = request.getHeader(GreyConst.HEADER_GREY_VERSION);
        if (!StringUtils.hasText(greyVersion)) {
            filterChain.doFilter(request, response);
            return;
        }
        List<Grey> greys;
        try {
            greys = JsonUtil.toBean(greyVersion, new TypeReference<List<Grey>>() {});
        } catch (Exception e) {
            // Json parse error, don't fail the request, but can't do grey
            log.warn("Grey header JSON parse error, value: {}", greyVersion);
            filterChain.doFilter(request, response);
            return;
        }
        Context.set(new Context(greys));
        try {
            filterChain.doFilter(request, response);
        } finally {
            Context.remove();
        }
    }
}
