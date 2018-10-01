package uk.gov.hmcts.reform.em.annotation.config;

import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.service.ServiceResolver;
import uk.gov.hmcts.reform.auth.parser.idam.core.service.token.ServiceTokenParser;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;

public class CustomServiceResolver extends ServiceResolver {

    private final ServiceAuthorisationApi serviceAuthorisationApi;

    public CustomServiceResolver( final ServiceTokenParser serviceTokenParser, ServiceAuthorisationApi serviceAuthorisationApi) {
        super(serviceTokenParser);
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    @Override
    public Service getTokenDetails(String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            bearerToken = "Bearer " + bearerToken;
        }
        String subject = serviceAuthorisationApi.getServiceName(bearerToken);
        return new Service(subject);
    }

}
