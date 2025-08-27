package com.wuledi.security.aop;

import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * 权限校验 AOP
 *
 * @author wuledi
 */
@Aspect
@Component
public class AuthInterceptor {
    @Around("@annotation(authCheck)") // 拦截带有 @AuthCheck 注解的方法
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder // 从 SecurityContextHolder 中获取认证信息
                .getContext()  // 获取安全上下文
                .getAuthentication(); // 获取认证信息
        Object principal = authentication.getPrincipal(); // 认证通过，获取用户信息
        if (!(principal instanceof UserDetailsImpl userDetailsImpl)) { // 未认证，抛出异常
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "未登录");
        }

        // 获取注解要求的角色
        UserRoleEnum mustRole = authCheck.mustRole(); // 获取注解要求的角色
        // 获取用户实际角色
        int userRoleCode = userDetailsImpl.getRole();
        UserRoleEnum userRole = UserRoleEnum.getEnumByValue(userRoleCode);
        if (userRole == null) { // 如果没有指定角色，抛出参数错误异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户角色错误");
        }
        // 校验用户是否被封禁
        if (UserRoleEnum.BAN.equals(userRole)) { // 如果用户被封禁，抛出权限错误异常
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已被封禁");
        }

        // 校验是否具备指定角色: 管理员可以访问所有接口, 普通用户只能访问不带管理员权限的接口
        if (UserRoleEnum.ADMIN.equals(mustRole) && !UserRoleEnum.ADMIN.equals(userRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足");
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}