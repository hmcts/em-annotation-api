package uk.gov.hmcts.reform.em.annotation.rest.errors;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Path;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionTranslatorTest {

    @Mock
    private NativeWebRequest request;

    @Mock
    private HttpServletRequest httpServletRequest;

    private ExceptionTranslator translator;

    @BeforeEach
    void setUp() {
        translator = new ExceptionTranslator();
    }

    @Test
    void processNullEntityReturnsNull() {
        ResponseEntity<ProblemDetail> result = translator.process(null, request);
        assertThat(result).isNull();
    }

    @Test
    void processNullBodyReturnsEntityUnchanged() {
        ResponseEntity<ProblemDetail> entity = ResponseEntity.badRequest().build();
        ResponseEntity<ProblemDetail> result = translator.process(entity, request);
        assertThat(result).isSameAs(entity);
    }

    @Test
    void processAddsDefaultMessageAndPath() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Test problem");
        problemDetail.setTitle("Test Problem");
        ResponseEntity<ProblemDetail> entity = ResponseEntity.badRequest().body(problemDetail);

        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/test/path");

        ResponseEntity<ProblemDetail> result = translator.process(entity, request);

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("Test Problem");
        assertThat(result.getBody().getProperties())
            .containsEntry("path", "/test/path")
            .containsEntry("message", "error.http.400");
    }

    @Test
    void processAddsInternalServerErrorMessage() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseEntity<ProblemDetail> entity = ResponseEntity.internalServerError().body(problemDetail);

        ResponseEntity<ProblemDetail> result = translator.process(entity, request);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getProperties()).containsEntry("message", "error.http.500");
    }

    @Test
    void processPreservesExistingMessage() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("message", "existing.message");
        ResponseEntity<ProblemDetail> entity = ResponseEntity.badRequest().body(problemDetail);

        ResponseEntity<ProblemDetail> result = translator.process(entity, request);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getProperties()).containsEntry("message", "existing.message");
    }

    @Test
    void processWhenNativeRequestIsNullDoesNotSetPath() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        ResponseEntity<ProblemDetail> entity = ResponseEntity.badRequest().body(problemDetail);

        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(null);

        ResponseEntity<ProblemDetail> result = translator.process(entity, request);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getProperties()).doesNotContainKey("path");
    }

    @Test
    void handleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
            "object",
            "field",
            null,
            false,
            new String[]{"NotNull"},
            null,
            "must not be null"
        );

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Object> response = translator.handleMethodArgumentNotValid(
            ex,
            HttpHeaders.EMPTY,
            HttpStatus.BAD_REQUEST,
            request
        );

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getTitle()).isEqualTo("Method argument not valid");
        assertThat(body.getType()).isEqualTo(ErrorConstants.CONSTRAINT_VIOLATION_TYPE);
        assertThat(body.getProperties())
            .containsEntry("message", ErrorConstants.ERR_VALIDATION)
            .containsKey("fieldErrors");

        assertThat(body.getProperties().get("fieldErrors"))
            .asList()
            .singleElement()
            .satisfies(error -> assertThat(error)
                .extracting("objectName", "field", "message")
                .containsExactly("object", "field", "NotNull"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleJakartaConstraintViolationExceptionWithNestedAndSimplePaths() {
        jakarta.validation.ConstraintViolation<Object> violation1 = mock(jakarta.validation.ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("user.address.street");
        when(violation1.getPropertyPath()).thenReturn(path1);
        doReturn(TestBindingTarget.class).when(violation1).getRootBeanClass();
        when(violation1.getMessage()).thenReturn("must not be blank");

        jakarta.validation.ConstraintViolation<Object> violation2 = mock(jakarta.validation.ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("field");
        when(violation2.getPropertyPath()).thenReturn(path2);
        doReturn(TestBindingTarget.class).when(violation2).getRootBeanClass();
        when(violation2.getMessage()).thenReturn("must not be null");

        jakarta.validation.ConstraintViolationException ex =
            new jakarta.validation.ConstraintViolationException(Set.of(violation1, violation2));

        ResponseEntity<Object> response = translator.handleConstraintViolationException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getTitle()).isEqualTo("Constraint violation");
        assertThat(body.getType()).isEqualTo(ErrorConstants.CONSTRAINT_VIOLATION_TYPE);
        assertThat(body.getProperties())
            .containsEntry("message", ErrorConstants.ERR_VALIDATION)
            .containsKey("fieldErrors");

        @SuppressWarnings("unchecked")
        List<FieldErrorVM> fieldErrors = (List<FieldErrorVM>) body.getProperties().get("fieldErrors");
        assertThat(fieldErrors).hasSize(2);
        assertThat(fieldErrors)
            .extracting(FieldErrorVM::getField)
            .containsExactlyInAnyOrder("street", "field");
    }

    @Test
    void handleHttpRequestMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex =
            new HttpRequestMethodNotSupportedException("PATCH", List.of("GET", "POST"));

        ResponseEntity<Object> response = translator.handleHttpRequestMethodNotSupported(
            ex,
            HttpHeaders.EMPTY,
            HttpStatus.METHOD_NOT_ALLOWED,
            request
        );

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(body.getProperties())
            .containsEntry("message", "error.http.405")
            .containsEntry("detail", ex.getMessage());
    }

    @Test
    void handleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex =
            new MissingServletRequestParameterException("param1", "String");

        ResponseEntity<Object> response = translator.handleMissingServletRequestParameter(
            ex,
            HttpHeaders.EMPTY,
            HttpStatus.BAD_REQUEST,
            request
        );

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.BAD_REQUEST);
    }

    @Test
    void handleMissingServletRequestPart() {
        MissingServletRequestPartException ex = new MissingServletRequestPartException("part1");

        ResponseEntity<Object> response = translator.handleMissingServletRequestPart(
            ex,
            HttpHeaders.EMPTY,
            HttpStatus.BAD_REQUEST,
            request
        );

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.BAD_REQUEST);
    }

    @Test
    void handleBindException() {
        BindException ex = new BindException(new TestBindingTarget(), "testBindingTarget");

        ResponseEntity<Object> response = translator.handleBindException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.BAD_REQUEST);
    }

    @Test
    void handleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Not found");

        ResponseEntity<Object> response = translator.handleNoSuchElementException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.ENTITY_NOT_FOUND_TYPE);
    }

    @Test
    void handleBadRequestAlertException() {
        BadRequestAlertException ex = new BadRequestAlertException("Error", "entity", "errorKey");

        ResponseEntity<Object> response = translator.handleBadRequestAlertException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().containsHeader("X-emannotationapp-error")).isTrue();
        assertThat(response.getHeaders().containsHeader("X-emannotationapp-params")).isTrue();
    }

    @Test
    void handleConcurrencyFailure() {
        ConcurrencyFailureException ex = new ConcurrencyFailureException("Concurrency error");

        ResponseEntity<Object> response = translator.handleConcurrencyFailure(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.ERR_CONCURRENCY_FAILURE);
    }

    @Test
    void handleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<Object> response = translator.handleAccessDenied(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.ERR_FORBIDDEN);
    }

    @Test
    void handleUnAuthorised() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/test/unauthorized");

        ResponseEntity<Object> response = translator.handleUnAuthorised(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(body.getProperties())
            .containsEntry("message", ErrorConstants.ERR_UNAUTHORISED)
            .containsEntry("path", "/test/unauthorized");
    }

    @Test
    void handleUnAuthorisedWhenNativeRequestIsNull() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(null);

        ResponseEntity<Object> response = translator.handleUnAuthorised(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(body.getProperties())
            .containsEntry("message", ErrorConstants.ERR_UNAUTHORISED)
            .doesNotContainKey("path");
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Integrity violation");

        ResponseEntity<Object> response = translator.handleDataIntegrityViolation(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.ERR_DATA_INTEGRITY);
    }

    @Test
    void handleConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException(
            "Constraint violation",
            null,
            "constraint"
        );

        ResponseEntity<Object> response = translator.handleConstraintViolation(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(body.getProperties()).containsEntry("message", ErrorConstants.ERR_CONSTRAINT_VIOLATION);
    }

    @Test
    void handleEmptyResponseWithServletWebRequest() {
        EmptyResponseException ex = new EmptyResponseException("Empty response occurred");
        ServletWebRequest servletWebRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/empty");

        ResponseEntity<Object> response = translator.handleEmptyResponse(ex, servletWebRequest);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(body.getDetail()).isEqualTo("Empty response occurred");
        assertThat(body.getProperties()).containsEntry("path", "/api/empty");
    }

    @Test
    void handleEmptyResponseWithNonServletWebRequest() {
        EmptyResponseException ex = new EmptyResponseException("Empty response occurred");
        WebRequest nonServletRequest = mock(WebRequest.class);

        ResponseEntity<Object> response = translator.handleEmptyResponse(ex, nonServletRequest);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(body.getProperties()).isNull();
    }

    @Test
    void handleRetryableException() {
        Request requestInfo = Request.create(
            Request.HttpMethod.GET,
            "url",
            Collections.emptyMap(),
            null,
            new RequestTemplate()
        );
        RetryableException ex = new RetryableException(
            503,
            "Service retryable failure",
            Request.HttpMethod.GET,
            new Date(),
            requestInfo
        );

        ResponseEntity<Object> response = translator.handleRetryableException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(body.getProperties()).containsEntry("message", ex.getMessage());
        assertThat(body.getDetail()).isEqualTo(ex.getMessage());
    }

    @Test
    void handleFeignException() {
        Request requestInfo = Request.create(
            Request.HttpMethod.GET,
            "url",
            new HashMap<>(),
            null,
            new RequestTemplate()
        );
        FeignException ex = new FeignException.ServiceUnavailable(
            "Service Unavailable",
            requestInfo,
            null,
            null
        );

        ResponseEntity<Object> response = translator.handleFeignException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(body.getProperties()).containsEntry("message", "Service Unavailable");
    }

    @Test
    void handleFeignExceptionWithUnresolvedStatusDefaultsTo500() {
        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(999);
        when(ex.getMessage()).thenReturn("Unknown feign error");

        ResponseEntity<Object> response = translator.handleFeignException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(body.getProperties()).containsEntry("message", "Unknown feign error");
    }

    @Test
    void handlePsqlExceptionDuplicateKey() {
        PSQLException ex = new PSQLException(
            "ERROR: duplicate key value violates unique constraint",
            PSQLState.UNKNOWN_STATE
        );

        ResponseEntity<Object> response = translator.handlePsqlException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(body.getProperties()).containsEntry("message", ex.getMessage());
    }

    @Test
    void handlePsqlExceptionGeneric() {
        PSQLException ex = new PSQLException("Connection refused", PSQLState.CONNECTION_FAILURE);

        ResponseEntity<Object> response = translator.handlePsqlException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(body.getProperties()).containsEntry("message", ex.getMessage());
    }

    @Test
    void handleCustomParameterizedExceptionWithParams() {
        CustomParameterizedException ex =
            new CustomParameterizedException("Custom error", Map.of("paramKey", "paramValue"));

        ResponseEntity<Object> response = translator.handleCustomParameterizedException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getProperties())
            .containsEntry("message", "Custom error")
            .containsEntry("params", Map.of("paramKey", "paramValue"));
    }

    @Test
    void handleCustomParameterizedExceptionWithoutParams() {
        CustomParameterizedException ex = mock(CustomParameterizedException.class);
        when(ex.getMessage()).thenReturn("Custom error without params");
        when(ex.getParamMap()).thenReturn(null);

        ResponseEntity<Object> response = translator.handleCustomParameterizedException(ex, request);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(body.getProperties())
            .containsEntry("message", "Custom error without params")
            .doesNotContainKey("params");
    }

    @Test
    void handleUnexpectedRuntimeWithoutResponseStatusAnnotation() {
        RuntimeException ex = new RuntimeException("Generic runtime failure");
        ServletWebRequest servletWebRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/test-runtime");

        ResponseEntity<Object> response = translator.handleUnexpectedRuntime(ex, servletWebRequest);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
        assertThat(body.getDetail()).isEqualTo("An unexpected internal server error occurred.");
        assertThat(body.getProperties())
            .containsEntry("message", "error.http.500")
            .containsEntry("path", "/api/test-runtime");
    }

    @Test
    void handleUnexpectedRuntimeWithResponseStatusAndReason() {
        AnnotatedWithReasonException ex = new AnnotatedWithReasonException("Custom reason exception");
        ServletWebRequest servletWebRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/annotated");

        ResponseEntity<Object> response = translator.handleUnexpectedRuntime(ex, servletWebRequest);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        assertThat(body.getTitle()).isEqualTo("Payment is required");
        assertThat(body.getDetail()).isEqualTo("Custom reason exception");
        assertThat(body.getProperties())
            .containsEntry("message", "error.http.402")
            .containsEntry("path", "/api/annotated");
    }

    @Test
    void handleUnexpectedRuntimeWithResponseStatusWithoutReason() {
        AnnotatedWithoutReasonException ex = new AnnotatedWithoutReasonException("No reason specified");
        WebRequest nonServletRequest = mock(WebRequest.class);

        ResponseEntity<Object> response = translator.handleUnexpectedRuntime(ex, nonServletRequest);

        ProblemDetail body = bodyOf(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(body.getDetail()).isEqualTo("No reason specified");
        assertThat(body.getProperties())
            .containsEntry("message", "error.http.406")
            .doesNotContainKey("path");
    }

    private ProblemDetail bodyOf(ResponseEntity<Object> response) {
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isInstanceOf(ProblemDetail.class);

        return (ProblemDetail) response.getBody();
    }

    @Setter
    @Getter
    private static final class TestBindingTarget {
        private String field;
    }

    @ResponseStatus(value = HttpStatus.PAYMENT_REQUIRED, reason = "Payment is required")
    private static class AnnotatedWithReasonException extends RuntimeException {
        AnnotatedWithReasonException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    private static class AnnotatedWithoutReasonException extends RuntimeException {
        AnnotatedWithoutReasonException(String message) {
            super(message);
        }
    }
}
