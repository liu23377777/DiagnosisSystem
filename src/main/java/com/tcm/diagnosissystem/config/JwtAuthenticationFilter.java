package com.tcm.diagnosissystem.config;

import com.tcm.diagnosissystem.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//自动加入过滤器链,实例化，注入指定依赖
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;//工具类生成，解析和校验JWT

    //提取，校验和用户信息写入
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveTokenFromCookie(request);//提取token,jwt存进cookie，防止XSS读取

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {//重复登录校验
            try {
                if (jwtUtil.validateToken(token)) {//验证签名
                    Long userId = jwtUtil.getUserIdFromToken(token);//校验，解析
                    String role = jwtUtil.getRoleFromToken(token);

                    // 验证成功后，将 userId 和 role 放入 request attribute，后端接口直接取
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", role);

                    // --- Spring Security 上下文设置 ---，构造Authentication
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));//当做principal
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);//放入SecurityContextHolder
                }
            } catch (Exception e) {//判断未认证状态
                log.warn("JWT token validation failed from cookie: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);//继续过滤
    }

    /**
     * 只从 HttpOnly Cookie 中解析 token
     */
    private String resolveTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }//遍历Cookie数组，匹配名为token的Cookie
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
