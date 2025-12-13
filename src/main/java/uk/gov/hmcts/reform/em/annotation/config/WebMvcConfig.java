package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.hmcts.reform.em.annotation.config.security.DeleteDocumentDataInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final DeleteDocumentDataInterceptor deleteDocumentDataInterceptor;

    public WebMvcConfig(DeleteDocumentDataInterceptor deleteDocumentDataInterceptor) {
        this.deleteDocumentDataInterceptor = deleteDocumentDataInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deleteDocumentDataInterceptor)
                .addPathPatterns("/api/documents/*/data");
    }
}