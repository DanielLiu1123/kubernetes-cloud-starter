package com.freemanan.kubernetes.grey.server.mvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.freemanan.kubernetes.grey.common.Grey;
import com.freemanan.kubernetes.grey.common.GreyConst;
import com.freemanan.kubernetes.grey.common.util.JsonUtil;
import com.freemanan.kubernetes.grey.thread.ThreadContext;
import com.freemanan.kubernetes.grey.thread.ThreadContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 */
public class GreyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(GreyConst.HEADER_GREY_VERSION);
        if (!StringUtils.hasText(header)) {
            filterChain.doFilter(request, response);
            return;
        }
        List<Grey> greys = JsonUtil.toBean(header, new TypeReference<List<Grey>>() {});
        ThreadContext tc = new ThreadContext();
        tc.setGreys(greys);
        ThreadContextHolder.set(tc);
        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadContextHolder.remove();
        }
    }
}
