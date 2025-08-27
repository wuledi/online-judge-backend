package com.wuledi.codesandbox.constant;

/**
 * Docker 镜像名称常量
 */
public interface DockerImagesConstant {
    String C_IMAGE_NAME = "codesandbox-gcc:1.0"; // C 语言调用镜像名称
    String CPP_IMAGE_NAME = "codesandbox-gpp:1.0"; // C++ 语言调用镜像名称
    String JAVA_IMAGE_NAME = "codesandbox-openjdk21:1.0"; // Java 语言调用镜像名称
    String PYTHON_IMAGE_NAME = "codesandbox-python3:1.0"; // Python 语言调用镜像名称
}
