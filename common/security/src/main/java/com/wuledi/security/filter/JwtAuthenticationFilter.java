package com.wuledi.security.filter;

import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.security.util.JwtUtils;
import com.wuledi.security.util.TokenUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * JWT认证过滤器
 *
 * @author wuledi
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 继承OncePerRequestFilter，确保每个请求只执行一次
    @Resource
    private JwtUtils jwtUtils;    // 注入JWT工具类

    @Resource
    private TokenUtils tokenUtils;


    @Resource
    private RedisTemplate<String, String> redisTemplate; // 注入 RedisTemplate


    /**
     * 过滤器逻辑
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 从请求头获取Token
        String token = tokenUtils.getToken(request);

        // 无Token时放行
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从Redis中获取相关信息，token已注销,直接返回401
        String logoutToken = redisTemplate.opsForValue().get(token);
        if (logoutToken != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 响应状态码设为401
            response.getWriter().write("Token invalid"); // 返回错误信息
            return;
        }

        // 验证token有效性,无效直接返回401
        boolean isValid = jwtUtils.verifyToken(token);
        if (!isValid) { // 无效
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 返回 401 未授权
            response.getWriter().write("Token invalid");
            return;
        }

        Jws<Claims> claimsJws = jwtUtils.parseToken(token); // 解析Token
        Claims payload = claimsJws.getPayload(); // 获取负载数据
        String username = payload.get("username").toString();


        // 如果用户名存在且未认证，则加载用户并设置到 SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 根据用户名加载用户信息
            Long id = ((Number) payload.get("id")).longValue(); // 存放的是id.0
            String password = payload.get("password").toString();
            Integer role = ((Number) payload.get("role")).intValue(); // 存放的是role.0

            UserDetails userDetails = new UserDetailsImpl(id, username, password, role);
            // 创建认证对象，设置用户信息
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication); // 设置认证对象到 SecurityContext

        }

        filterChain.doFilter(request, response);
    }
}