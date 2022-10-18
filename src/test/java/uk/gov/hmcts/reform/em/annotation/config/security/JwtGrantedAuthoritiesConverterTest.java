package uk.gov.hmcts.reform.em.annotation.config.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.hmcts.reform.em.annotation.repository.IdamRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtGrantedAuthoritiesConverterTest {


    @Mock
    private IdamDetailsFilterService idamDetailsFilterService;

    @Mock
    private IdamRepository idamRepository;

    @InjectMocks
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Test
    void shouldAuthoritiesEmaptyIfNoMatchingToken() {
        Jwt jwt = new Jwt("tokenNotValid", Instant.now(), Instant.now().plusSeconds(120), Map.of("header1", "value1"), Map.of("claim1", "1"));
        var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        verify(idamRepository, never()).getUserInfo(any());
        verify(idamDetailsFilterService, never()).saveIdamDetails(any());
        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldHaveAuthoritiesEmaptyIfNoMatchingToken() {
        Jwt jwt = new Jwt("tokenName", Instant.now(), Instant.now().plusSeconds(120), Map.of("header1", "value1"), Map.of("tokenName", "access_token"));
        var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        verify(idamRepository, times(1)).getUserInfo(any());
        verify(idamDetailsFilterService, times(1)).saveIdamDetails(any());
        assertThat(authorities).isEmpty();
    }

}