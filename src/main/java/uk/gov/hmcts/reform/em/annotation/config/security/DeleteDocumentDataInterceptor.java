package uk.gov.hmcts.reform.em.annotation.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.em.annotation.rest.errors.UnauthorisedServiceException;

import java.util.List;
import java.util.Objects;

@Component
public class DeleteDocumentDataInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(DeleteDocumentDataInterceptor.class);

    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private static final String BEARER = "Bearer ";

    private final AuthTokenValidator tokenValidator;
    private final List<String> authorisedServices;

    public DeleteDocumentDataInterceptor(@Lazy @Qualifier("authTokenValidator") AuthTokenValidator tokenValidator,
                                         @Value("${delete-document-data.s2s-whitelist}")
                                         List<String> authorisedServices) {
        this.tokenValidator = tokenValidator;
        this.authorisedServices = authorisedServices;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String serviceAuthToken = request.getHeader(SERVICE_AUTHORIZATION);

        if (Objects.isNull(serviceAuthToken) || serviceAuthToken.isBlank()) {
            log.warn("ServiceAuthorization header is missing");
            throw new UnauthorisedServiceException("ServiceAuthorization header is missing");
        }

        String serviceName;
        if (!serviceAuthToken.startsWith(BEARER)) {
            serviceName = tokenValidator.getServiceName(BEARER + serviceAuthToken);
        } else {
            serviceName = tokenValidator.getServiceName(serviceAuthToken);
        }

        if (!authorisedServices.contains(serviceName)) {
            log.error("Service '{}' is not whitelisted to delete document data", serviceName);
            throw new UnauthorisedServiceException(
                "Service " + serviceName + " not in configured list for deleting document data");
        }

        return true;
    }
}