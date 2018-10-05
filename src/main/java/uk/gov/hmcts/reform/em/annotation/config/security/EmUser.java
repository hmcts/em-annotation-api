package uk.gov.hmcts.reform.em.annotation.config.security;

import uk.gov.hmcts.reform.auth.checker.core.user.User;

import java.util.Set;

public class EmUser extends User {

    private String forename;
    private String surname;
    private String email;

    public EmUser(String principleId, Set<String> roles, String forename, String surname, String email) {
        super(principleId, roles);
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
