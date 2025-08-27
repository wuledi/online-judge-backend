package com.wuledi.sdk.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * 代码沙箱SDK异常
 */
@Slf4j
public class WulediCodeSandboxException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public WulediCodeSandboxException(String message, Throwable cause) {
        super(message, cause);
        log.error("CodeSandboxException occurred: {}", message, cause);
    }
}