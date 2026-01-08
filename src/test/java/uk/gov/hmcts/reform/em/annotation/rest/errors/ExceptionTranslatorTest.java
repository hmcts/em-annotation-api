package uk.gov.hmcts.reform.em.annotation.rest.errors;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.util.Collections;
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
        ResponseEntity<Problem> result = translator.process(null, request);
        assertThat(result).isNull();
    }

    @Test
    void processHandlesConstraintViolationProblem() {
        Violation violation = new Violation("field", "error");
        ConstraintViolationProblem problem = new ConstraintViolationProblem(
            Status.BAD_REQUEST,
            Collections.singletonList(violation)
        );
        ResponseEntity<Problem> entity = new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);

        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/test/path");

        ResponseEntity<Problem> result = translator.process(entity, request);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("Constraint Violation");
        assertThat(result.getBody().getParameters())
            .containsEntry("path", "/test/path")
            .containsEntry("message", ErrorConstants.ERR_VALIDATION);
    }

    @Test
    void processHandlesDefaultProblem() {
        Problem problem = Problem.builder()
            .withStatus(Status.BAD_REQUEST)
            .withTitle("Test Problem")
            .build();
        ResponseEntity<Problem> entity = new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);

        when(request.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/test/path");

        ResponseEntity<Problem> result = translator.process(entity, request);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getParameters())
            .containsEntry("path", "/test/path")
            .containsEntry("message", "error.http.400");
    }

    @Test
    void handleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "code");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // request.getNativeRequest stubbing removed as it's not required for this assertion

        ResponseEntity<Problem> response = translator.handleMethodArgumentNotValid(ex, request);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Method argument not valid");
        assertThat(response.getBody().getParameters()).containsKey("fieldErrors");
    }

    @Test
    void handleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("Not found");

        ResponseEntity<Problem> response = translator.handleNoSuchElementException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(Status.NOT_FOUND);
        assertThat(response.getBody().getParameters()).containsEntry("message", ErrorConstants.ENTITY_NOT_FOUND_TYPE);
    }

    @Test
    void handleBadRequestAlertException() {
        BadRequestAlertException ex = new BadRequestAlertException("Error", "entity", "errorKey");

        ResponseEntity<Problem> response = translator.handleBadRequestAlertException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders()).containsKey("X-emannotationapp-error");
        assertThat(response.getHeaders()).containsKey("X-emannotationapp-params");
    }

    @Test
    void handleConcurrencyFailure() {
        ConcurrencyFailureException ex = new ConcurrencyFailureException("Concurrency error");

        ResponseEntity<Problem> response = translator.handleConcurrencyFailure(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters())
            .containsEntry("message", ErrorConstants.ERR_CONCURRENCY_FAILURE);
    }

    @Test
    void handleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<Problem> response = translator.handleAccessDenied(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters()).containsEntry("message", ErrorConstants.ERR_FORBIDDEN);
    }

    @Test
    void handleUnAuthorised() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<Problem> response = translator.handleUnAuthorised(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters()).containsEntry("message", ErrorConstants.ERR_UNAUTHORISED);
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Integrity violation");

        ResponseEntity<Problem> response = translator.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters()).containsEntry("message", ErrorConstants.ERR_DATA_INTEGRITY);
    }

    @Test
    void handleConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException("Constraint violation", null, "constraint");

        ResponseEntity<Problem> response = translator.handleConstraintViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters())
            .containsEntry("message", ErrorConstants.ERR_CONSTRAINT_VIOLATION);
    }

    @Test
    void handleFeignException() {
        Request requestInfo = Request.create(
            Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());
        FeignException ex = new FeignException.ServiceUnavailable("Service Unavailable", requestInfo, null, null);

        ResponseEntity<Problem> response = translator.handleFeignException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters()).containsEntry("message", "Service Unavailable");
    }

    @Test
    void handlePsqlExceptionDuplicateKey() {
        PSQLException ex = new PSQLException(
            "ERROR: duplicate key value violates unique constraint", PSQLState.UNKNOWN_STATE);

        ResponseEntity<Problem> response = translator.handlePsqlException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters()).containsEntry("message", ex.getMessage());
    }

    @Test
    void handlePsqlExceptionGeneric() {
        PSQLException ex = new PSQLException("Connection refused", PSQLState.CONNECTION_FAILURE);

        ResponseEntity<Problem> response = translator.handlePsqlException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getParameters()).containsEntry("message", ex.getMessage());
    }
}