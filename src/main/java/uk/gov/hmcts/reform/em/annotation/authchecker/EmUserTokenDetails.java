package uk.gov.hmcts.reform.em.annotation.authchecker;

import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenDetails;

import java.util.Set;

public class EmUserTokenDetails extends UserTokenDetails {

    private String forename;
    private String surname;
    private String email;

    public EmUserTokenDetails() {
        super(null, null);
    }

    public EmUserTokenDetails(String id, Set<String> roles) {
        super(id, roles);
    }

    public EmUserTokenDetails(String id, Set<String> roles, String forename, String surname, String email) {
        super(id, roles);
        this.forename = forename;
        this.surname = surname;
        this.email = email;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
