package uk.gov.hmcts.reform.em.annotation.config;

import uk.gov.hmcts.reform.auth.checker.spring.useronly.UserDetails;

import java.util.Collection;

public class EmUserDetails extends UserDetails {

    private String forename;
    private String surname;
    private String email;

    public EmUserDetails(String username, String token, Collection<String> authorities) {
        super(username, token, authorities);
    }

    public EmUserDetails(String username, String token, Collection<String> authorities, String forename, String surname, String email) {
        super(username, token, authorities);
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
