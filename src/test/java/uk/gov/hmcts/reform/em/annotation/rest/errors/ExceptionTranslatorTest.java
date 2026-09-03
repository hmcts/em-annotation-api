package uk.gov.hmcts.reform.em.annotation.rest.errors;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(response.getHeaders()).containsKey("X-emannotationapp-error");
        assertThat(response.getHeaders()).containsKey("X-emannotationapp-params");
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
}