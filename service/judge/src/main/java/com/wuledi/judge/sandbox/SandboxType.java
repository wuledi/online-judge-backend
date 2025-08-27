package com.wuledi.judge.sandbox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SandboxType {
    /**
     * 沙箱类型标识符（如：local、remote等）
     */
    String value();
}