package uk.gov.hmcts.reform.em.annotation.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;

import java.util.Map;
import java.util.Optional;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

/**
 * Utility class for Spring Security.
 */
@Service
public class SecurityUtils {

    private final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    public static final String TOKEN_NAME = "tokenName";

    private final IdamRepository idamRepository;

    @Autowired
    public SecurityUtils(final IdamRepository idamRepository){
        this.idamRepository = idamRepository;
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    log.info(" ================== Principal is : "+ authentication.getPrincipal());
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        log.info(" ============= UserDetails Principal is : "+ springSecurityUser.getUsername());
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        log.info(" ================== String Principal is : "+ authentication.getPrincipal());
                        return (String) authentication.getPrincipal();
                    } else if (authentication instanceof JwtAuthenticationToken) {

                        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
                        log.info(" ================== JwtAuthenticationToken is : "+ jwt);
                        if (jwt.containsClaim(TOKEN_NAME) && jwt.getClaim(TOKEN_NAME).equals(ACCESS_TOKEN)) {
                            uk.gov.hmcts.reform.idam.client.models.UserDetails userDetails = idamRepository.getUserDetails(jwt.getTokenValue());
                            return userDetails.getId();
                        }
                    } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
                        log.info(" ================== DefaultOidcUser is : "+ authentication.getPrincipal());
                        Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
                        if (attributes.containsKey("preferred_username")) {
                            return (String) attributes.get("preferred_username");
                        }
                    }
                    return null;
                });
    }

}
