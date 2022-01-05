package uk.gov.hmcts.reform.em.annotation.rest.errors;

// Delete this class because it is not used?
@SuppressWarnings("squid:S110")
public class LoginAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Login name already used!", "userManagement", "userexists");
    }
}
