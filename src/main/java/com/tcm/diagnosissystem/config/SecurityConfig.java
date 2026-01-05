package com.tcm.diagnosissystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 极简配置：全部请求放行，只保留 BCrypt 加密器，
 * 供开发阶段排错使用。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())//关闭csrf保护,对浏览器表单攻击无防护
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//会话无状态，接口完全无登录态概念
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())//所有请求放行
            .logout(l -> l.disable())//关闭登出端点
            .formLogin(f -> f.disable());//关闭默认登录页
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }//哈希算法BCrypt加密
}
