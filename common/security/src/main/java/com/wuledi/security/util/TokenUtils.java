package com.wuledi.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.wuledi.common.constant.HttpConstant.TOKEN_NAME;

/**
 * Token工具类
 *
 * @author wuledi
 */
@Component
public class TokenUtils {

    /**
     * 获取Token
     *
     * @param request     请求
     * @return Token
     */
    public String getToken(HttpServletRequest request) {

        // 从请求头获取Token并移除Bearer前缀
        String token = Optional.ofNullable(request.getHeader(TOKEN_NAME))
                .map(t -> t.replace("Bearer ", ""))
                .orElse(null);

        // 优先使用请求头Token，失败则尝试参数Token
        if (token == null || token.isBlank() || "undefined".equals(token)) {
            token = request.getParameter(TOKEN_NAME);
        }

        // 参数Token也为空，返回null
        if (token == null || token.isBlank() || "undefined".equals(token)) {
            return null;
        }
        return token;
    }
}
