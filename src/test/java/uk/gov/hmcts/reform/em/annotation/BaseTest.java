package uk.gov.hmcts.reform.em.annotation;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.em.annotation.config.security.JwtGrantedAuthoritiesConverter;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {

    @MockBean
    private ServiceAuthFilter serviceAuthFilter;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @MockBean
    private IdamApi idamApi;

    @MockBean
    private IdamClient idamClient;

    @MockBean
    private IdamRepository idamRepository;

    private static final String ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsIm"
            + "p0aSI6ImQzNWRmMTRkLTA5ZjYtNDhmZi04YTkzLTdjNmYwMzM5MzE1OSIsImlhdCI6MTU0M"
            + "Tk3MTU4MywiZXhwIjoxNTQxOTc1MTgzfQ.QaQOarmV8xEUYV7yvWzX3cUE_4W1luMcWCwpr"
            + "oqqUrg";

    protected MockMvc restLogoutMockMvc;

    @Before
    public void setupMocks() {

        Map<String, Object> claims = new HashMap<>();
        claims.put("groups", "ROLE_USER");
        claims.put("sub", 123);

        OidcIdToken idToken = new OidcIdToken(ID_TOKEN, Instant.now(),
                Instant.now().plusSeconds(60), claims);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken(idToken));

        SecurityContextHolderAwareRequestFilter authInjector = new SecurityContextHolderAwareRequestFilter();
        this.restLogoutMockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    private OAuth2AuthenticationToken authenticationToken(OidcIdToken idToken) {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        OidcUser user = new DefaultOidcUser(authorities, idToken);

        return new OAuth2AuthenticationToken(user, authorities, "oidc");

    }
}
