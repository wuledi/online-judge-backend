package com.wuledi.security.annotation;

import com.wuledi.security.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 */
@Target(ElementType.METHOD) // 注解作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留注解信息
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return 角色标识
     */
    UserRoleEnum  mustRole() default UserRoleEnum.NOT_LOGIN;

}

