package com.leo.commonswagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Swagger配置类 - 基于SpringDoc OpenAPI
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:Mall System}")
    private String applicationName;

    @Value("${swagger.version:1.0.0}")
    private String version;

    @Value("${swagger.title:Mall System API}")
    private String title;

    @Value("${swagger.description:企业级在线商城微服务系统API文档}")
    private String description;

    @Value("${swagger.contact.name:Miao Zheng}")
    private String contactName;

    @Value("${swagger.contact.email:leozheng0508@mall.com}")
    private String contactEmail;

    @Value("${swagger.contact.url:https://mall.com}")
    private String contactUrl;

    /**
     * OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components()
                        .addSecuritySchemes("Bearer-Token", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("JWT Token")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer-Token"));
    }

    /**
     * API信息
     */
    private Info apiInfo() {
        return new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact()
                        .name(contactName)
                        .email(contactEmail)
                        .url(contactUrl))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    /**
     * 用户端API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户端接口")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * 管理端API分组
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理端接口")
                .pathsToMatch("/admin/api/v1/**")
                .build();
    }

    /**
     * 开放API分组
     */
    @Bean
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder()
                .group("开放接口")
                .pathsToMatch("/open/**")
                .build();
    }

    /**
     * 内部API分组
     */
    @Bean
    public GroupedOpenApi internalApi() {
        return GroupedOpenApi.builder()
                .group("内部接口")
                .pathsToMatch("/internal/**")
                .build();
    }

    /**
     * 所有API
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("所有接口")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                        .resourceChain(false);
            }
        };
    }
}