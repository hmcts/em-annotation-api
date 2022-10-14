package uk.gov.hmcts.reform.em.annotation.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.authorisation.exceptions.ServiceException;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This class is used in preference to ServiceAuthFilter to enable additional logging.
 */
@Component
public class AnnotationServiceAuthFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(AnnotationServiceAuthFilter.class);
    private final AuthTokenValidator authTokenValidator;
    public static final String AUTHORISATION = "ServiceAuthorization";

    @Value("${idam.s2s-authorised.services}")
    private List<String> authorisedServices;


    public AnnotationServiceAuthFilter(AuthTokenValidator authTokenValidator, List<String> authorisedServices) {
        this.authTokenValidator = authTokenValidator;
        this.authorisedServices = authorisedServices;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String bearerToken = extractBearerToken(request);
            String serviceName = authTokenValidator.getServiceName(bearerToken);
            if (!authorisedServices.contains(serviceName)) {
                log.debug("service forbidden {}", serviceName);
                response.setStatus(HttpStatus.FORBIDDEN.value());
            } else {
                log.info("em-anno : Endpoint : {}  for : {} method is accessed by {} ", request.getRequestURI(),
                        request.getMethod(), serviceName);
                filterChain.doFilter(request, response);
            }
        } catch (InvalidTokenException | ServiceException exception) {
            log.warn("Unsuccessful service authentication", exception);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

    }

    private String extractBearerToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORISATION);
        if (token == null) {
            throw new InvalidTokenException("ServiceAuthorization Token is missing");
        }
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }
}
