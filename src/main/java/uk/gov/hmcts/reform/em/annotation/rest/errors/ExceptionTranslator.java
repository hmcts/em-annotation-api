package uk.gov.hmcts.reform.em.annotation.rest.errors;

import feign.FeignException;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controller advice to translate server-side exceptions to client-friendly JSON structures.
 * The error response follows RFC 7807.
 */
@RestControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);

    private static final String MESSAGE_FIELD = "message";
    private static final String FIELD_ERRORS = "fieldErrors";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
            .map(error -> new FieldErrorVM(error.getObjectName(), error.getField(), error.getCode()))
            .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "");
        problemDetail.setType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE);
        problemDetail.setTitle("Method argument not valid");
        problemDetail.setProperty(FIELD_ERRORS, fieldErrors);
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_VALIDATION);

        return new ResponseEntity<>(problemDetail, headers, status);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
        jakarta.validation.ConstraintViolationException ex,
        WebRequest request) {

        List<FieldErrorVM> fieldErrors = ex.getConstraintViolations().stream()
            .map(violation -> {
                String fieldPath = violation.getPropertyPath().toString();
                String fieldName = fieldPath.contains(".")
                    ? fieldPath.substring(fieldPath.lastIndexOf('.') + 1)
                    : fieldPath;

                return new FieldErrorVM(violation.getRootBeanClass().getSimpleName(),
                    fieldName,
                    violation.getMessage());
            })
            .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "");
        problemDetail.setType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE);
        problemDetail.setTitle("Constraint violation");
        problemDetail.setProperty(FIELD_ERRORS, fieldErrors);
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_VALIDATION);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "");
        problemDetail.setProperty(MESSAGE_FIELD, "error.http.405");
        problemDetail.setProperty("detail", ex.getMessage());

        return new ResponseEntity<>(problemDetail, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.BAD_REQUEST);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.BAD_REQUEST);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex,
                                                               NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ENTITY_NOT_FOUND_TYPE);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<Object> handleBadRequestAlertException(BadRequestAlertException ex,
                                                                 NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "");
        problemDetail.setProperty(MESSAGE_FIELD, "error." + ex.getErrorKey());

        HttpHeaders headers = HeaderUtil.createFailureAlert(ex.getEntityName(),
            ex.getErrorKey(),
            ex.getMessage());

        return new ResponseEntity<>(problemDetail, headers, problemDetail.getStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex, NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.BAD_REQUEST);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Object> handleConcurrencyFailure(ConcurrencyFailureException ex,
                                                           NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_CONCURRENCY_FAILURE);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_FORBIDDEN);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleUnAuthorised(BadCredentialsException ex, NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_UNAUTHORISED);

        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        if (nativeRequest != null) {
            problemDetail.setProperty("path", nativeRequest.getRequestURI());
        }

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                               NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_DATA_INTEGRITY);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
                                                            NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "");
        problemDetail.setProperty(MESSAGE_FIELD, ErrorConstants.ERR_CONSTRAINT_VIOLATION);

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(EmptyResponseException.class)
    public ResponseEntity<Object> handleEmptyResponse(EmptyResponseException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NO_CONTENT,
            ex.getMessage());
        addPathProperty(problemDetail, request);

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<Object> handleRetryableException(RetryableException ex,
                                                           NativeWebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
            ex.getMessage());
        problemDetail.setProperty(MESSAGE_FIELD, ex.getMessage());

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex, NativeWebRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setProperty(MESSAGE_FIELD, ex.getMessage());

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<Object> handlePsqlException(PSQLException ex, NativeWebRequest request) {
        LOG.info("Em-Annotation SQL exception: {}", ex.getMessage());

        HttpStatus status = StringUtils.contains(ex.getMessage(),
            "duplicate key value violates unique constraint")
            ? HttpStatus.CONFLICT
            : HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setProperty(MESSAGE_FIELD, ex.getMessage());

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(CustomParameterizedException.class)
    public ResponseEntity<Object> handleCustomParameterizedException(CustomParameterizedException ex,
                                                                     WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
            ex.getMessage());
        problemDetail.setProperty(MESSAGE_FIELD, ex.getMessage());

        if (ex.getParamMap() != null) {
            problemDetail.setProperty("params", ex.getParamMap());
        }

        return new ResponseEntity<>(problemDetail, new HttpHeaders(), problemDetail.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleUnexpectedRuntime(RuntimeException ex, WebRequest request) {
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(),
            ResponseStatus.class);

        if (responseStatus != null) {
            HttpStatus libraryStatus = responseStatus.value();
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(libraryStatus,
                ex.getMessage());

            if (StringUtils.isNotBlank(responseStatus.reason())) {
                problemDetail.setTitle(responseStatus.reason());
            }

            addCommonProperties(problemDetail, request);
            return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected internal server error occurred.");
        problemDetail.setTitle("Internal Server Error");

        addCommonProperties(problemDetail, request);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    public ResponseEntity<ProblemDetail> process(ResponseEntity<ProblemDetail> entity,
                                                 NativeWebRequest request) {
        if (entity == null) {
            return null;
        }

        ProblemDetail problemDetail = entity.getBody();
        if (problemDetail == null) {
            return entity;
        }

        Map<String, Object> properties = problemDetail.getProperties();
        if (properties == null || !properties.containsKey(MESSAGE_FIELD)) {
            if (entity.getStatusCode().value() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                problemDetail.setProperty(MESSAGE_FIELD, "error.http.500");
            } else {
                problemDetail.setProperty(MESSAGE_FIELD,
                    "error.http." + entity.getStatusCode().value());
            }
        }

        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        if (nativeRequest != null) {
            problemDetail.setProperty("path", nativeRequest.getRequestURI());
        }

        return new ResponseEntity<>(problemDetail, entity.getHeaders(), entity.getStatusCode());
    }

    private void addCommonProperties(ProblemDetail problemDetail, WebRequest request) {
        problemDetail.setProperty(MESSAGE_FIELD, "error.http." + problemDetail.getStatus());
        addPathProperty(problemDetail, request);
    }

    private void addPathProperty(ProblemDetail problemDetail, WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            problemDetail.setProperty("path", servletRequest.getRequest().getRequestURI());
        }
    }
}