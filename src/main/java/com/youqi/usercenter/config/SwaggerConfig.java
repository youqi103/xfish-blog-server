package com.youqi.usercenter.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 配置Swagger Docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 扫描带有@ApiOperation注解的方法
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // 扫描所有路径
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置Swagger基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户中心API文档")
                .description("用户中心系统的接口文档，包含用户管理、文章管理、评论管理等功能")
                .version("1.0.0")
                .contact(new Contact("YouQi", "https://example.com", "youqi@example.com"))
                .termsOfServiceUrl("https://example.com/terms")
                .license("Apache 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }
}