package uk.gov.hmcts.reform.em.annotation.rest.errors;

// Delete this class because it is not used?
@SuppressWarnings("squid:S110")
public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "Email is already in use!", "userManagement", "emailexists");
    }
}
