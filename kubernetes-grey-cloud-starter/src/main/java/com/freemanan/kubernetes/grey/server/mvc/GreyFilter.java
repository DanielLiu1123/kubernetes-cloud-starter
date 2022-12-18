package com.freemanan.kubernetes.grey.server.mvc;

import static com.freemanan.kubernetes.grey.common.util.GreyUtil.doGreyInContext;

import com.freemanan.kubernetes.grey.common.GreyConst;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 */
public class GreyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            doGreyInContext(
                    () -> request.getHeader(GreyConst.HEADER_GREY_VERSION),
                    () -> filterChain.doFilter(request, response));
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Should never happen !", e);
        }
    }
}
