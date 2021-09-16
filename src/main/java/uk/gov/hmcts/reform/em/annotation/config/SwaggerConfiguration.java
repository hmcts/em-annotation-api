package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
@ComponentScan("uk.gov.hmcts.reform.em.annotation.rest")
public class SwaggerConfiguration {

    private static final String API_VERSION = "0.0.1";

    private static final String MODEL_REF_TYPE = "string";
    private static final String PARAMETER_TYPE = "header";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api(.*)"))
                .build()
                .apiInfo(apiInfo())
                .globalOperationParameters(Arrays.asList(
                    headerAuthorization(),
                    headerServiceAuthorization()));
    }

    private Parameter headerAuthorization() {
        return new ParameterBuilder()
            .name("Authorization")
            .description("Keyword `Bearer` followed by a valid IDAM user token")
            .modelRef(new ModelRef(MODEL_REF_TYPE))
            .parameterType(PARAMETER_TYPE)
            .required(true)
            .build();
    }

    private Parameter headerServiceAuthorization() {
        return new ParameterBuilder()
            .name("ServiceAuthorization")
            .description("Valid Service-to-Service JWT token for a whitelisted micro-service")
            .modelRef(new ModelRef(MODEL_REF_TYPE))
            .parameterType(PARAMETER_TYPE)
            .required(true)
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("EM Annotation API")
                .description("API to store and retrieve annotations for DM Store")
                .version(API_VERSION)
                .build();
    }
}
