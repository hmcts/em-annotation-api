package uk.gov.hmcts.reform.em.annotation.config.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {
    @Mock
    private IdamRepository idamRepository;

    private SecurityUtils securityUtils;

    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    protected Authentication authentication;

    @Mock
    protected SecurityContext securityContext;


    @BeforeEach
    public void setUp() throws Exception {
        SecurityContextHolder.setContext(securityContext);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("Header Name", "Header Value")
                .claim(SecurityUtils.TOKEN_NAME, ACCESS_TOKEN)
                .build();
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        securityUtils = new SecurityUtils(idamRepository);

    }

    @Test
    @DisplayName("Null authentication returns null")
    void testNullUserLogin() {
        doReturn(null).when(securityContext).getAuthentication();
        Assertions.assertFalse(securityUtils.getCurrentUserLogin().isPresent());
    }

    @Test
    @DisplayName("User details authentication calls getUsername")
    void testUserDetailsUserLogin() {
        doReturn(authentication).when(securityContext).getAuthentication();
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        securityUtils.getCurrentUserLogin();
        verify(userDetails, times(1)).getUsername();
    }

    @Test
    @DisplayName("String authentication gets current login")
    void testStringUserLogin() {
        doReturn(authentication).when(securityContext).getAuthentication();
        String authenticationString = "Authentication";
        when(authentication.getPrincipal()).thenReturn(authenticationString);
        Optional<String> login = securityUtils.getCurrentUserLogin();
        Assertions.assertTrue(login.isPresent());
        Assertions.assertEquals(login.get(), authenticationString);
    }

    @Test
    @DisplayName("Jwt authentication calls getUid")
    void testValidJwtAuthenticationTokenUserLogin() {
        doReturn(jwtAuthenticationToken).when(securityContext).getAuthentication();

        UserInfo userInfo = mock(UserInfo.class);
        when(idamRepository.getUserInfo(any(String.class))).thenReturn(userInfo);

        securityUtils.getCurrentUserLogin();

        verify(userInfo, times(1)).getUid();
    }

    @Test
    @DisplayName("Invalid jwt authentication returns null")
    void testInvalidJwtAuthenticationTokenUserLogin() {
        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt);
        doReturn(authenticationToken).when(securityContext).getAuthentication();
        Assertions.assertFalse(securityUtils.getCurrentUserLogin().isPresent());
    }

    @Test
    @DisplayName("Default Oidc authentication gets current login")
    void testValidDefaultOidcUserLogin() {
        doReturn(authentication).when(securityContext).getAuthentication();
        DefaultOidcUser defaultOidcUser = mock(DefaultOidcUser.class);
        when(authentication.getPrincipal()).thenReturn(defaultOidcUser);

        Map<String, Object> attributes = Map.of("preferred_username", "Username");
        when(defaultOidcUser.getAttributes()).thenReturn(attributes);

        Optional<String> login = securityUtils.getCurrentUserLogin();
        Assertions.assertTrue(login.isPresent());
        Assertions.assertEquals("Username", login.get());
    }

    @Test
    @DisplayName("Invalid default Oidc authentication returns null")
    void testInvalidDefaultOidcUserLogin() {
        doReturn(authentication).when(securityContext).getAuthentication();
        DefaultOidcUser defaultOidcUser = mock(DefaultOidcUser.class);
        when(authentication.getPrincipal()).thenReturn(defaultOidcUser);

        Assertions.assertFalse(securityUtils.getCurrentUserLogin().isPresent());
    }


}

