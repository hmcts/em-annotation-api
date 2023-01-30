package uk.gov.hmcts.reform.em.annotation.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;

@Configuration
public class EmSecurityConfiguration {

    @Bean
    public AuthTokenValidator authTokenValidator(final ServiceAuthorisationApi serviceAuthorisationApi) {
        return new ServiceAuthTokenValidator(serviceAuthorisationApi);
    }
}
