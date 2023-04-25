package com.train.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Liu
 * @date 2023/4/24 10:07
 * @description 自定义 Swagger 接口文档的配置
 */

@Configuration
@EnableSwagger2WebMvc
@Profile({"dev", "test"})
public class SwaggerConfig {

    @Bean(value = "defaultApi")
    @Order(value = 1)
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.train"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(listParameter());
    }


    /**
     * api 信息
     *
     * @return
     */
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("鱼皮用户中心")
//                .description("鱼皮用户中心接口文档")
//                .termsOfServiceUrl("https://github.com/liyupi")
//                .contact(new Contact("Liu","https://github.com/liyupi","1322423039@qq.com"))
//                .version("1.0")
//                .build();
//    }
    private ApiInfo apiInfo() {
        return new ApiInfo("系统支撑", "系统支撑文档", "1.0", "https://github.com/FrederickGrae", "Liu", null, null);
    }

    // 添加请求头
/*    private List<Parameter> operationParameters() {
        ArrayList<Parameter> headers = new ArrayList<>();
        headers.add(new ParameterBuilder().name("token").description("校验token").modelRef(new ModelRef("string")).parameterType("header").required(true).defaultValue("").build());
        return headers;
    }*/

    private List<Parameter> listParameter() {
        // 头部参数
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("LOGIN_USER")
                .description("令牌认证")
                .modelRef(new ModelRef("string"))
                .defaultValue("")
                .parameterType("header")
                .required(false).build());
        return parameters;
    }
}
