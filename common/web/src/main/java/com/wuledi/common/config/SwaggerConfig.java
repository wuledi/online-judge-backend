package com.wuledi.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 *
 * @author wuledi
 */
@Configuration
@SecurityScheme(
        name = "Bearer Authentication", // 认证方案名称
        type = SecuritySchemeType.HTTP, // 认证类型
        bearerFormat = "JWT", // Bearer Token的格式
        scheme = "bearer" // 认证方案
)
@ConditionalOnClass(OpenAPI.class)
public class SwaggerConfig {
    @Value("${wuledi.swagger.title:Swagger OpenAPI}")
    private String title;
    @Value("${wuledi.swagger.description:Spring Boot 项目接口文档}")
    private String description;
    @Value("${wuledi.swagger.version:v1.0.0}")
    private String version;
    @Value("${wuledi.swagger.contact.name:wuledi}")
    private String contactName;
    @Value("${wuledi.swagger.contact.email:mail@wuledi.com}")
    private String contactEmail;
    @Value("${wuledi.swagger.contact.url:https://wuledi.com}")
    private String contactUrl;
    @Value("${wuledi.swagger.license.name:Apache 2.0}")
    private String licenseName;
    @Value("${wuledi.swagger.license.url:https://www.apache.org/licenses/LICENSE-2.0.html}")
    private String licenseUrl;
    @Value("${wuledi.swagger.termsOfService:https://wuledi.com/terms-of-service}")
    private String termsOfService;
    @Value("${wuledi.swagger.externalDocumentation.description:项目Wiki文档}")
    private String externalDocumentationDescription;
    @Value("${wuledi.swagger.externalDocumentation.url:https://wiki.wuledi.com/docs}")
    private String externalDocumentationUrl;

    @Bean
    public OpenAPI springShopOpenAPI() {

        return new OpenAPI() // 创建一个OpenAPI实例
                .info(getInfo()) // 设置基本信息
                .externalDocs(getExternalDocumentation()); // 设置外部文档信息
    }

    private Info getInfo() {
        return new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact()
                        .name(contactName)
                        .email(contactEmail)
                        .url(contactUrl))
                .license(new License()
                        .name(licenseName)
                        .url(licenseUrl))
                .termsOfService(termsOfService); // 服务条款链接
    }

    private ExternalDocumentation getExternalDocumentation() {
        return new ExternalDocumentation()
                .description(externalDocumentationDescription)
                .url(externalDocumentationUrl);
    }
}
