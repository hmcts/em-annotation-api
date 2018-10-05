package uk.gov.hmcts.reform.em.annotation.config.security;

import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;

import java.util.Collection;

public class EmServiceAndUserDetails extends ServiceAndUserDetails {

    private String forename;
    private String surname;
    private String email;

    public EmServiceAndUserDetails(String username, String token, Collection<String> authorities, String servicename) {
        super(username, token, authorities, servicename);
    }

    public EmServiceAndUserDetails(String username, String token, Collection<String> authorities, String servicename, String forename, String surname, String email) {
        super(username, token, authorities, servicename);
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
