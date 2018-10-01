package uk.gov.hmcts.reform.em.annotation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.exceptions.AuthCheckerException;
import uk.gov.hmcts.reform.auth.checker.core.exceptions.BearerTokenInvalidException;
import uk.gov.hmcts.reform.auth.checker.core.exceptions.BearerTokenMissingException;
import uk.gov.hmcts.reform.auth.checker.core.exceptions.UnauthorisedServiceException;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.service.ServiceRequestAuthorizer;
import uk.gov.hmcts.reform.auth.parser.idam.core.service.token.ServiceTokenInvalidException;
import uk.gov.hmcts.reform.auth.parser.idam.core.service.token.ServiceTokenParsingException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

public class CustomServiceRequestAuthorizer extends ServiceRequestAuthorizer {

    private final Logger log = LoggerFactory.getLogger(CustomServiceRequestAuthorizer.class);

    private final SubjectResolver<Service> serviceResolver;
    private final Function<HttpServletRequest, Collection<String>> authorizedServicesExtractor;

    public CustomServiceRequestAuthorizer(SubjectResolver<Service> serviceResolver, Function<HttpServletRequest, Collection<String>> authorizedServicesExtractor) {
        super(serviceResolver, authorizedServicesExtractor);
        this.serviceResolver = serviceResolver;
        this.authorizedServicesExtractor = authorizedServicesExtractor;
    }

    @Override
    public Service authorise(HttpServletRequest request) throws UnauthorisedServiceException {
        Collection<String> authorizedServices = authorizedServicesExtractor.apply(request).stream().map(String::toLowerCase).collect(toSet());
        if (authorizedServices.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one service defined");
        }

        String bearerToken = request.getHeader(AUTHORISATION);
        if (bearerToken == null) {
            throw new BearerTokenMissingException();
        }

        Service service = getTokenDetails(bearerToken);
        if (!authorizedServices.contains(service.getPrincipal().toLowerCase())) {
            throw new UnauthorisedServiceException();
        }

        return service;
    }

    private Service getTokenDetails(String bearerToken) {
        try {
            return serviceResolver.getTokenDetails(bearerToken);
        } catch (ServiceTokenInvalidException e) {
            log.info("ServiceTokenInvalidException: " + e.getMessage(), e.getCause());
            throw new BearerTokenInvalidException(e);
        } catch (ServiceTokenParsingException e) {
            log.info("ServiceTokenParsingException: " + e.getMessage(), e.getCause());
            throw new AuthCheckerException("Error parsing JWT token", e);
        }
    }

}
