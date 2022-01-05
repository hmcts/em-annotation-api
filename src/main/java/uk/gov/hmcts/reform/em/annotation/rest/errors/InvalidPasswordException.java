package uk.gov.hmcts.reform.em.annotation.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

// Delete this class because it is not used?
@SuppressWarnings("squid:S110")
public class InvalidPasswordException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super(ErrorConstants.INVALID_PASSWORD_TYPE, "Incorrect password", Status.BAD_REQUEST);
    }
}
