package uk.gov.hmcts.reform.em.annotation.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationTest {

    @Mock
    private ServiceAuthFilter serviceAuthFilter;

    @Mock
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Mock
    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(securityConfiguration, "issuerUri", "");
        ReflectionTestUtils.setField(securityConfiguration, "issuerOverride", "");
    }

    @Test
    void jwtDecoderSetsValidator() {
        NimbusJwtDecoder nimbusJwtDecoder = mock(NimbusJwtDecoder.class);
        try (MockedStatic<JwtDecoders> jwtDecoderMockedStatic = Mockito.mockStatic(JwtDecoders.class)) {
            jwtDecoderMockedStatic.when(() ->
                    JwtDecoders.fromOidcIssuerLocation(anyString())).thenReturn(nimbusJwtDecoder);
            assertEquals(nimbusJwtDecoder, securityConfiguration.jwtDecoder());
            verify(nimbusJwtDecoder, times(1)).setJwtValidator(any());
        }
    }
}
