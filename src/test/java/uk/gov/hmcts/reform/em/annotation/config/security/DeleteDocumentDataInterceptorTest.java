package uk.gov.hmcts.reform.em.annotation.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.em.annotation.rest.errors.UnauthorisedServiceException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteDocumentDataInterceptorTest {

    @Mock
    private AuthTokenValidator tokenValidator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private DeleteDocumentDataInterceptor interceptor;

    private static final String WHITELISTED_SERVICE = "whitelisted_service";
    private static final String UNAUTHORIZED_SERVICE = "bad_service";
    private static final String TOKEN = "Bearer eyJhbG...";
    private static final String SERVICE_HEADER = "ServiceAuthorization";

    @BeforeEach
    void setUp() {
        interceptor = new DeleteDocumentDataInterceptor(tokenValidator, List.of(WHITELISTED_SERVICE));
    }

    @Test
    void shouldReturnTrueWhenServiceIsWhitelisted() {
        when(request.getHeader(SERVICE_HEADER)).thenReturn(TOKEN);
        when(tokenValidator.getServiceName(TOKEN)).thenReturn(WHITELISTED_SERVICE);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenServiceIsNotWhitelisted() {
        when(request.getHeader(SERVICE_HEADER)).thenReturn(TOKEN);
        when(tokenValidator.getServiceName(TOKEN)).thenReturn(UNAUTHORIZED_SERVICE);

        Object handler = new Object();
        assertThrows(UnauthorisedServiceException.class, () ->
            interceptor.preHandle(request, response, handler)
        );
    }

    @Test
    void shouldThrowExceptionWhenHeaderIsMissing() {
        when(request.getHeader(SERVICE_HEADER)).thenReturn(null);

        Object handler = new Object();
        UnauthorisedServiceException exception = assertThrows(UnauthorisedServiceException.class, () ->
            interceptor.preHandle(request, response, handler)
        );

        assertThat(exception.getMessage()).isEqualTo("ServiceAuthorization header is missing");
        verify(tokenValidator, never()).getServiceName(anyString());
    }

    @Test
    void shouldThrowExceptionWhenHeaderIsBlank() {
        when(request.getHeader(SERVICE_HEADER)).thenReturn("   ");

        Object handler = new Object();
        UnauthorisedServiceException exception = assertThrows(UnauthorisedServiceException.class, () ->
            interceptor.preHandle(request, response, handler)
        );

        assertThat(exception.getMessage()).isEqualTo("ServiceAuthorization header is missing");
        verify(tokenValidator, never()).getServiceName(anyString());
    }

    @Test
    void shouldHandleTokenWithoutBearerPrefix() {
        String rawToken = "rawTokenString";
        when(request.getHeader(SERVICE_HEADER)).thenReturn(rawToken);

        when(tokenValidator.getServiceName("Bearer " + rawToken)).thenReturn(WHITELISTED_SERVICE);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(tokenValidator).getServiceName("Bearer " + rawToken);
    }
}