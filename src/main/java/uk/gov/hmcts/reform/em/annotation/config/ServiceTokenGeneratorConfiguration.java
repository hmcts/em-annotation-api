package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.auth.checker.core.CachingSubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.spring.AuthCheckerProperties;
import uk.gov.hmcts.reform.auth.parser.idam.core.service.token.ServiceTokenParser;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

@Configuration
@Lazy
@EnableFeignClients(basePackageClasses = {ServiceAuthorisationApi.class, UserAuthorisationApi.class})
public class ServiceTokenGeneratorConfiguration {

    @Bean
    public AuthTokenGenerator annotationAppTokenGenerator(
        @Value("${idam.s2s-auth.totp_secret}") final String secret,
        @Value("${idam.s2s-auth.microservice}") final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }

//    @Bean
//    public SubjectResolver<Service> serviceResolver(final ServiceTokenParser serviceTokenParser, final ServiceAuthorisationApi serviceAuthorisationApi, AuthCheckerProperties properties) {
//        return new CachingSubjectResolver<>(new CustomServiceResolver(serviceTokenParser, serviceAuthorisationApi), properties.getService().getTtlInSeconds(), properties.getService().getMaximumSize());
//    }
//
//    @Bean
//    public SubjectResolver<User> userResolver(final UserTokenParser userTokenParser, final UserAuthorisationApi userAuthorisationApi, AuthCheckerProperties properties) {
//        return new CachingSubjectResolver<>(new CustomUserResolver(userTokenParser, userAuthorisationApi), properties.getService().getTtlInSeconds(), properties.getService().getMaximumSize());
//    }
}
