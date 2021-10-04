package uk.gov.hmcts.reform.em.annotation.authchecker;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserPair;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.ServiceDetails;

public class EmAuthCheckerUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
            throws UsernameNotFoundException {

        Object principal = token.getPrincipal();

        if (principal instanceof Service) {
            return new ServiceDetails(((Service) principal).getPrincipal());
        }

        if (principal instanceof EmUser) {
            EmUser user = (EmUser) principal;
            return new EmUserDetails(user.getPrincipal(), (String) token.getCredentials(), user.getRoles(),
                    user.getForename(), user.getSurname(), user.getEmail());
        }

        ServiceAndUserPair serviceAndUserPair = (ServiceAndUserPair) principal;

        return new EmServiceAndUserDetails(
                serviceAndUserPair.getUser().getPrincipal(),
                (String) token.getCredentials(),
                serviceAndUserPair.getUser().getRoles(),
                serviceAndUserPair.getService().getPrincipal(),
                ((EmUser) serviceAndUserPair.getUser()).getForename(),
                ((EmUser) serviceAndUserPair.getUser()).getSurname(),
                ((EmUser) serviceAndUserPair.getUser()).getEmail()
        );

    }
}
