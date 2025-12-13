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

    @BeforeEach
    void setUp() {
        interceptor = new DeleteDocumentDataInterceptor(tokenValidator, List.of(WHITELISTED_SERVICE));
    }

    @Test
    void shouldReturnTrueWhenServiceIsWhitelisted() {
        when(request.getHeader("ServiceAuthorization")).thenReturn(TOKEN);
        when(tokenValidator.getServiceName(TOKEN)).thenReturn(WHITELISTED_SERVICE);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenServiceIsNotWhitelisted() {
        when(request.getHeader("ServiceAuthorization")).thenReturn(TOKEN);
        when(tokenValidator.getServiceName(TOKEN)).thenReturn(UNAUTHORIZED_SERVICE);

        Object handler = new Object();
        assertThrows(UnauthorisedServiceException.class, () ->
            interceptor.preHandle(request, response, handler)
        );
    }
}