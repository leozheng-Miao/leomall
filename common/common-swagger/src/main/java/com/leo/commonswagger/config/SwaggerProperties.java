package com.leo.commonswagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Swagger配置属性
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 标题
     */
    private String title = "Mall System API";

    /**
     * 描述
     */
    private String description = "企业级在线商城微服务系统API文档";

    /**
     * 版本
     */
    private String version = "1.0.0";

    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl;

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 许可证
     */
    private License license = new License();

    /**
     * 全局参数
     */
    private List<GlobalParameter> globalParameters;

    @Data
    public static class Contact {
        private String name = "Miao Zheng";
        private String email = "admin@mall.com";
        private String url = "https://mall.com";
    }

    @Data
    public static class License {
        private String name = "Apache 2.0";
        private String url = "https://www.apache.org/licenses/LICENSE-2.0";
    }

    @Data
    public static class GlobalParameter {
        private String name;
        private String description;
        private String defaultValue;
        private boolean required;
        private String parameterType = "header";
    }
}