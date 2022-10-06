package uk.gov.hmcts.reform.em.annotation.rest.errors;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class ExceptionTranslatorTestController {

    @GetMapping("/test/concurrency-failure")
    public void concurrencyFailure() {
        throw new ConcurrencyFailureException("test concurrency failure");
    }

    @PostMapping("/test/method-argument")
    public void methodArgument(@Valid @RequestBody TestDTO testDTO) {
    }

    @GetMapping("/test/no-such-element-exception")
    public void noSuchElementException() {
        throw new NoSuchElementException("test no such element exception");
    }

    @GetMapping("/test/parameterized-error")
    public void parameterizedError() {
        throw new CustomParameterizedException("test parameterized error", "param0_value", "param1_value");
    }

    @GetMapping("/test/parameterized-error2")
    public void parameterizedError2() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "foo_value");
        params.put("bar", "bar_value");
        throw new CustomParameterizedException("test parameterized error", params);
    }

    @GetMapping("/test/missing-servlet-request-part")
    public void missingServletRequestPartException(@RequestPart String part) {
    }

    @GetMapping("/test/missing-servlet-request-parameter")
    public void missingServletRequestParameterException(@RequestParam String param) {
    }

    @GetMapping("/test/access-denied")
    public void accessdenied() {
        throw new AccessDeniedException("test access denied!");
    }

    @GetMapping("/test/unauthorized")
    public void unauthorized() {
        throw new BadCredentialsException("test authentication failed!");
    }

    @GetMapping("/test/response-status")
    public void exceptionWithReponseStatus() {
        throw new TestResponseStatusException();
    }

    @GetMapping("/test/internal-server-error")
    public void internalServerError() {
        throw new RuntimeException();
    }

    @GetMapping("/test/data-integrity-violation")
    public void dataIntegrityViolation() {
        throw new DataIntegrityViolationException("Data Integrity Violation");
    }

    @GetMapping("/test/psql-key-violation")
    public void PSQLException() throws PSQLException {
        throw new PSQLException("duplicate key value violates unique constraint", PSQLState.CHECK_VIOLATION);
    }

    @GetMapping("/test/psql-exception")
    public void PSQLKeyViolation() throws PSQLException {
        throw new PSQLException("PSQL Exception", PSQLState.CHECK_VIOLATION);
    }

    @GetMapping("/test/retryable-exception")
    public void retryableException() {
        throw new RetryableException(503, "Retryable Exception", Request.HttpMethod.GET, Date.valueOf(LocalDate.now()),
                createFeignRequest());
    }

    @GetMapping("/test/feign-gateway-timeout")
    public void feignGatewayTimeout() {
        throw new FeignException.GatewayTimeout("feign gateway timeout", createFeignRequest(), null, new HashMap<>());
    }


    @GetMapping("/test/feign-bad-gateway")
    public void feignBadGateway() {
        throw new FeignException.BadGateway("feign bad gateway", createFeignRequest(), null, new HashMap<>());
    }

    @NotNull
    private static Request createFeignRequest() {
        return Request.create(Request.HttpMethod.GET, "url",
                new HashMap<>(), null, new RequestTemplate());
    }

    public static class TestDTO {

        @NotNull
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "test response status")
    @SuppressWarnings("serial")
    public static class TestResponseStatusException extends RuntimeException {
    }

}
