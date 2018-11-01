package uk.gov.hmcts.reform.em.annotation.testutil;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.springframework.http.MediaType;

public class TestUtil {

    private String s2sToken;
    private String idamToken;
    private Integer userId;

    public RequestSpecification authRequest() throws Exception {
        return s2sAuthRequest()
            .header("Authorization", "Bearer " + generateIdamToken("test@test.com"));
    }

    public RequestSpecification s2sAuthRequest() throws Exception {
        RestAssured.useRelaxedHTTPSValidation();
        return RestAssured
                .given()
                .header("ServiceAuthorization", "Bearer " + getS2sToken());
    }

    public String generateIdamToken(String username) {
        if (idamToken == null) {
            createUser(username, "password");
            String userId = findUserIdByUserEmail(username).toString();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", userId);
            jsonObject.put("role", "caseworker");

            Response response = RestAssured
                    .given()
                    .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .formParam("id", userId)
                    .formParam("role", "caseworker")
                    .post(Env.getIdamURL() + "/testing-support/lease");

            idamToken = response.getBody().print();
        }
        System.out.println("Authorization: "+idamToken);
        return idamToken;
    }

    private Integer findUserIdByUserEmail(String email) {
        userId = RestAssured
                .get(Env.getIdamURL() + "/users?email=" + email)
                .getBody()
                .jsonPath()
                .get("id");
        return userId;
    }

    public void createUser(String email, String password) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("forename", "test");
        jsonObject.put("surname", "test");

        RestAssured
            .given()
            .header("Content-Type", "application/json")
            .body(jsonObject.toString())
            .post(Env.getIdamURL() + "/testing-support/accounts");

    }


    public String getS2sToken() throws Exception {

        if (s2sToken == null) {
            String otp = String.valueOf(new GoogleAuthenticator().getTotpPassword(Env.getS2SToken()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("microservice", Env.getS2SServiceName());
            jsonObject.put("oneTimePassword", otp);

            Response response = RestAssured
                    .given()
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toString())
                    .post(Env.getS2SURL() + "/lease");
            s2sToken = response.getBody().asString();


        }
        System.out.println("ServiceAuthorization: "+s2sToken);
        return s2sToken;

    }

    public void setS2sToken(String s2sToken) {
        this.s2sToken = s2sToken;
    }

    public String generateIdamToken() {
        return idamToken;
    }

    public void setIdamToken(String idamToken) {
        this.idamToken = idamToken;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
