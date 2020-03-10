package uk.gov.hmcts.reform.em.annotation.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.hmcts.reform.em.annotation.authchecker.EmServiceAndUserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    } else if (authentication instanceof JwtAuthenticationToken) {
                        return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("preferred_username");
                    } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
                        Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
                        if (attributes.containsKey("preferred_username")) {
                            return (String) attributes.get("preferred_username");
                        }
                    }
                    return null;
                });
    }

    public static Optional<EmServiceAndUserDetails> getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof EmServiceAndUserDetails) {
                        EmServiceAndUserDetails springSecurityUser = (EmServiceAndUserDetails) authentication.getPrincipal();
                        return springSecurityUser;
                    }
                    return null;
                });
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

}
