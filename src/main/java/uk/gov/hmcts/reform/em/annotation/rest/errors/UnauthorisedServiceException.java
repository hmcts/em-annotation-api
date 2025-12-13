package uk.gov.hmcts.reform.em.annotation.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UnauthorisedServiceException extends RuntimeException {
    public UnauthorisedServiceException(String message) {
        super(message);
    }
}