package com.example.fanball.user.config.filter;

import com.example.common.config.JwtTokenProvider;
import com.example.common.domain.UserType;
import com.example.fanball.user.sevice.user.UserService;
import lombok.RequiredArgsConstructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/admin/*")
@RequiredArgsConstructor
public class UserFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String uri = httpReq.getRequestURI();
        // Only enforce JWT on admin endpoints
        if (!uri.startsWith("/admin")) {
            chain.doFilter(request, response);
            return;
        }

        String auth = httpReq.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpRes.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = auth.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpRes.getWriter().write("Invalid or expired token");
            return;
        }

        UserType role = jwtTokenProvider.getRole(token);
        if (role != UserType.ADMIN) {
            httpRes.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpRes.getWriter().write("Admin role required");
            return;
        }

        chain.doFilter(request, response);
    }
}
