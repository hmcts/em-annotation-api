package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EmErrorController emErrorController;

    @Test
    void handleErrorThrowsExceptionWhenAttributeIsPresent() {
        RuntimeException exception = new RuntimeException("Test Exception");
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(exception);

        assertThatThrownBy(() -> emErrorController.handleError(request))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test Exception");
    }

    @Test
    void handleErrorDoesNothingWhenAttributeIsAbsent() {
        when(request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)).thenReturn(null);

        assertThatCode(() -> emErrorController.handleError(request))
            .doesNotThrowAnyException();
    }
}