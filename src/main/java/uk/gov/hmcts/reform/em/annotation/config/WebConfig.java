package uk.gov.hmcts.reform.em.annotation.config;
 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.hmcts.reform.em.annotation.handlers.IdamHeaderInterceptor;
import uk.gov.hmcts.reform.em.annotation.handlers.IdamHeaderInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
 
    @Autowired
    IdamHeaderInterceptor idamHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) { 
        registry.addInterceptor(idamHeaderInterceptor).addPathPatterns("/continuous-online-hearings/**");
    } 
} 