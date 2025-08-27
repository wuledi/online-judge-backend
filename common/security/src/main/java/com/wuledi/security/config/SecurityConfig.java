package com.wuledi.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuledi.security.filter.JwtAuthenticationFilter;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全配置类
 *
 * @author wuledi
 */
@Configuration
public class SecurityConfig {
    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter; // JWT认证过滤器

    /**
     * 配置密码加密器
     *
     * @return 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() { // 获取密码加密器实例
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 配置认证管理器
     *
     * @param authenticationConfiguration 认证配置
     * @return 认证管理器
     * @throws Exception 异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration // 获取认证配置实例
                .getAuthenticationManager(); // 获取认证管理器实例
    }

    /**
     * 配置安全规则
     *
     * @param http http
     * @return 安全规则
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                                // 先排除登录和注册请求，之后再进行角色验证
//                         .requestMatchers(HttpMethod.POST,"/api/user/login", "/api/user/register").permitAll() // 允许登录和注册请求
//                        .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER") // USER和ADMIN角色可以访问/user路径
                                // Swagger 相关路径需要 ADMIN 角色
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs").hasRole("ADMIN")
                                // 允许所有 OPTIONS 请求
                                .anyRequest().permitAll() // 其他路径不需要认证
                )
                .formLogin(form -> form
                        .permitAll() // 允许所有用户访问登录页面
                        .loginProcessingUrl("/login") // 设置登录请求路径
                        .successHandler((request, response, authentication) -> {
                            Object principal = authentication.getPrincipal(); // 获取认证信息
                            response.setContentType("application/json;charset=UTF-8"); // 设置响应类型
                            response.setStatus(HttpServletResponse.SC_OK); // 设置响应状态码
                            Map<String, Object> result = new HashMap<>(); // 实例化Map集合
                            result.put("status", HttpServletResponse.SC_OK); // 设置状态码
                            result.put("message", "用户登录成功"); // 设置消息
                            result.put("principal", principal); // 设置认证信息
                            ObjectMapper mapper = new ObjectMapper(); // 实例化ObjectMapper对象
                            response.getWriter().println(mapper.writeValueAsString(result)); // 将Map集合转换为JSON字符串并写入响应
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=UTF-8"); // 设置响应类型
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置响应状态码
                            Map<String, Object> result = new HashMap<>(); // 实例化Map集合
                            result.put("status", HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码
                            result.put("principal", null); // 设置认证信息
                            switch (exception) {
                                case LockedException lockedException -> result.put("message", "账户被锁定，登录失败！");
                                case BadCredentialsException badCredentialsException ->
                                        result.put("message", "账户名或密码输入错误，登录失败！");
                                case DisabledException disabledException ->
                                        result.put("message", "账户被禁用，登录失败！");
                                case CredentialsExpiredException credentialsExpiredException ->
                                        result.put("message", "密码已过期，登录失败！");
                                case null, default -> result.put("message", "未知原因，导致登录失败！");
                            }
                            ObjectMapper mapper = new ObjectMapper(); // 实例化ObjectMapper对象
                            response.getWriter().println(mapper.writeValueAsString(result)); // 将Map集合转换为JSON字符串并写入响应
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 设置注销URL
                        .clearAuthentication(true) // 清除认证信息
                        .invalidateHttpSession(true) // 使HttpSession失效
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8"); // 设置响应类型
                            response.setStatus(HttpServletResponse.SC_OK); // 设置响应状态码
                            Map<String, Object> result = new HashMap<>(); // 实例化Map集合
                            // 生成 JWT Token（假设存在 JwtTokenUtil 工具类）
                            result.put("status", HttpServletResponse.SC_OK); // 设置状态码
                            result.put("message", "用户注销成功"); // 设置注销成功信息
                            result.put("principal", null); // 设置认证信息
                            ObjectMapper mapper = new ObjectMapper(); // 实例化ObjectMapper对象
                            response.getWriter().println(mapper.writeValueAsString(result)); // 将Map集合转换为JSON字符串并写入响应
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 添加JWT认证过滤器
                .csrf(AbstractHttpConfigurer::disable); // 禁用CSRF防护，CSRF是跨站请求伪造，用于防止恶意请求。

        return http.build();
    }
}