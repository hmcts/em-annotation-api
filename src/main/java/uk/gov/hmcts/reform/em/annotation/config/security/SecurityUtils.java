package uk.gov.hmcts.reform.em.annotation.config.security;

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

    public static final String TOKEN_NAME = "tokenName";

    private final IdamRepository idamRepository;

    @Autowired
    public SecurityUtils(final IdamRepository idamRepository) {
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
                    if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                        return userDetails.getUsername();
                    } else if (authentication.getPrincipal() instanceof String getPrincipalString) {
                        return getPrincipalString;
                    } else if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                        Jwt jwt = jwtAuthenticationToken.getToken();
                        if (Boolean.TRUE.equals(jwt.hasClaim(TOKEN_NAME))
                                && jwt.getClaim(TOKEN_NAME).equals(ACCESS_TOKEN)) {
                            uk.gov.hmcts.reform.idam.client.models.UserInfo userInfo =
                                    idamRepository.getUserInfo(jwt.getTokenValue());
                            return userInfo.getUid();
                        }
                    } else if (authentication.getPrincipal() instanceof DefaultOidcUser defaultOidcUser) {
                        Map<String, Object> attributes = defaultOidcUser.getAttributes();
                        if (attributes.containsKey("preferred_username")) {
                            return (String) attributes.get("preferred_username");
                        }
                    }
                    return null;
                });
    }

}
