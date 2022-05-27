package uk.gov.hmcts.reform.em.annotation.config.security;

import com.microsoft.applicationinsights.boot.dependencies.apachecommons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

@RunWith(MockitoJUnitRunner.class)
public class SecurityUtilsTest {
    @Mock
    private IdamRepository idamRepository;

    @Mock
    private AuthTokenValidator authTokenValidator;

    private SecurityUtils securityUtils;

    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    protected Authentication authentication;

    @Mock
    protected SecurityContext securityContext;

    @Before
    public void setUp() throws Exception {
        doReturn(authentication).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("Header Name", "Header Value")
                .claim(SecurityUtils.TOKEN_NAME, ACCESS_TOKEN)
                .build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read");
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt, authorities);

        securityUtils = new SecurityUtils(idamRepository);
        FieldUtils.writeField(securityUtils, "authTokenValidator", authTokenValidator, true);
    }

    @Test
    @DisplayName("Null authentication returns null")
    public void testNullUserLogin(){
        doReturn(null).when(securityContext).getAuthentication();
        Assert.assertFalse(securityUtils.getCurrentUserLogin().isPresent());
    }

    @Test
    @DisplayName("User details authentication calls getUsername")
    public void testUserDetailsUserLogin(){
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        securityUtils.getCurrentUserLogin();
        verify(userDetails, times(1)).getUsername();
    }

    @Test
    @DisplayName("String authentication gets current login")
    public void testStringUserLogin(){
        String authenticationString = "Authentication";
        when(authentication.getPrincipal()).thenReturn(authenticationString);
        Optional<String> login = securityUtils.getCurrentUserLogin();
        Assert.assertTrue(login.isPresent());
        Assert.assertEquals(login.get(), authenticationString);
    }

    @Test
    @DisplayName("Jwt authentication calls getUid")
    public void testValidJwtAuthenticationTokenUserLogin(){
        doReturn(jwtAuthenticationToken).when(securityContext).getAuthentication();

        UserInfo userInfo = mock(UserInfo.class);
        when(idamRepository.getUserInfo(any(String.class))).thenReturn(userInfo);

        securityUtils.getCurrentUserLogin();

        verify(userInfo, times(1)).getUid();
    }

    @Test
    @DisplayName("Invalid jwt authentication returns null")
    public void testInvalidJwtAuthenticationTokenUserLogin(){
        Jwt jwt = mock(Jwt.class);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt);
        doReturn(authenticationToken).when(securityContext).getAuthentication();
        Assert.assertFalse(securityUtils.getCurrentUserLogin().isPresent());
    }

    @Test
    @DisplayName("Default Oidc authentication gets current login")
    public void testValidDefaultOidcUserLogin(){
        DefaultOidcUser defaultOidcUser = mock(DefaultOidcUser.class);
        when(authentication.getPrincipal()).thenReturn(defaultOidcUser);

        Map<String, Object> attributes = Map.of("preferred_username", "Username");
        when(defaultOidcUser.getAttributes()).thenReturn(attributes);

        Optional<String> login = securityUtils.getCurrentUserLogin();
        Assert.assertTrue(login.isPresent());
        Assert.assertEquals(login.get(), "Username");
    }

    @Test
    @DisplayName("Invalid default Oidc authentication returns null")
    public void testInvalidDefaultOidcUserLogin(){
        DefaultOidcUser defaultOidcUser = mock(DefaultOidcUser.class);
        when(authentication.getPrincipal()).thenReturn(defaultOidcUser);

        Assert.assertFalse(securityUtils.getCurrentUserLogin().isPresent());
    }

    @Test
    @DisplayName("ServiceName uses authTokenValidator")
    public void testGetServiceName() {
        final String serviceName = "Service Name";
        when(authTokenValidator.getServiceName(any(String.class))).thenReturn(serviceName);

        securityUtils.getServiceName(random(5, true, false));

        verify(authTokenValidator, times(1)).getServiceName(anyString());
    }

}

