package uk.gov.hmcts.reform.em.annotation;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
public class IdamConsumerTest {

    private static final String IDAM_DETAILS_URL = "/details";
    private static final String IDAM_OPENID_TOKEN_URL = "/o/token";
    private static String ACCESS_TOKEN = "111";

    @Value("${idam.client.id}")
    String client_id;

    @Value("${idam.client.secret}")
    String client_secret;

    @Value("${idam.client.redirect_uri}")
    String redirect_uri;

    @Pact(provider = "Idam_api", consumer = "Annotation_api")
    public RequestResponsePact executeGetUserDetailsAndGet200(PactDslWithProvider builder) {

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);

        Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String[] rolesArray = new String[1];
        rolesArray[0] = "citizen";

        params.put("email", "emCaseOfficer@email.net");
        params.put("forename", "emCaseOfficer");
        params.put("surname", "Jar");
        params.put("password", "Password123");
        params.put("roles", rolesArray);

        return builder
            .given("a user exists", params)
            .uponReceiving("Provider returns user details to Annotation API")
            .path(IDAM_DETAILS_URL)
            .method(HttpMethod.POST.toString())
            .headers(headers)
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createUserDetailsResponse())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeGetUserDetailsAndGet200")
    public void should_get_user_details_with_access_token(MockServer mockServer) throws JSONException {

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);

        String actualResponseBody =
                SerenityRest
                .given()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .when()
                .post(mockServer.getUrl() + IDAM_DETAILS_URL)
                .then()
                .statusCode(200)
                .and()
                .extract()
                .body()
                .asString();

        JSONObject response = new JSONObject(actualResponseBody);

        assertThat(actualResponseBody).isNotNull();
        assertThat(response).hasNoNullFieldsOrProperties();
        assertThat(response.getString("_id")).isNotBlank();
        assertThat(response.getString("givenName")).isNotBlank();
        assertThat(response.getString("sn")).isNotBlank();

        JSONArray rolesArr = new JSONArray(response.getString("roles"));

        assertThat(rolesArr).isNotNull();
        assertThat(rolesArr.length()).isNotZero();
        assertThat(rolesArr.get(0).toString()).isNotBlank();

    }

    @Pact(provider = "Idam_api", consumer = "Annotation_api")
    public RequestResponsePact executeGetIdamAccessTokenAndGet200(PactDslWithProvider builder) throws JSONException {

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        params.put("username", "emCaseOfficer@email.net");
        params.put("password", "Password123");
        params.put("client_id", client_id);
        params.put("redirect_uri", redirect_uri);
        params.put("scope", "openid roles profile");

        return builder
            .given("I have an obtained authorization_code as a user and a client", params)
            .uponReceiving("Provider takes user/pwd and returns Auth code to Annotation API")
            .path(IDAM_OPENID_TOKEN_URL)
            .method(HttpMethod.POST.toString())
            .headers(headers)
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAuthCodeToReturn())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeGetIdamAccessTokenAndGet200")
    public void should_post_to_token_endpoint_and_receive_access_token_with_200_response(MockServer mockServer)
        throws JSONException {

        String actualResponseBody =

            SerenityRest
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all(true)
                .when()
                .post(mockServer.getUrl() + IDAM_OPENID_TOKEN_URL)
                .then()
                .statusCode(200)
                .and()
                .extract()
                .asString();

        JSONObject response = new JSONObject(actualResponseBody);

        ACCESS_TOKEN = response.getString("code");

        assertThat(response).isNotNull();
        assertThat(response.getString("code")).isNotBlank();

    }



    @Pact(provider = "Idam_api", consumer = "Annotation_api")
    public RequestResponsePact userEstablishSessionAndReceivesCookie(PactDslWithProvider builder) {

        Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        params.put("username", "emCaseOfficer@email.net");
        params.put("password", "Password123");

        return builder
            .given("I establish a session as a user", params)
            .uponReceiving("Provider receives request to establish a session for user and returns a cookie")
            .path(IDAM_OPENID_TOKEN_URL)
            .method(HttpMethod.POST.toString())
            .willRespondWith()
            .body(createCookieToReturn())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "userEstablishSessionAndReceivesCookie")
    public void should_user_establish_session_And_get_cookie(MockServer mockServer) throws JSONException {

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);

        String actualResponseBody =
            SerenityRest
                .given()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(mockServer.getUrl() + IDAM_OPENID_TOKEN_URL)
                .then()
                .statusCode(200)
                .and()
                .extract()
                .body()
                .asString();

        JSONObject response = new JSONObject(actualResponseBody);

        assertThat(actualResponseBody).isNotBlank();
        assertThat(response.getString("cookie")).isNotBlank();
    }

    @Pact(provider = "Idam_api", consumer = "Annotation_api")
    public RequestResponsePact executeGetIdamAccessTokenAndGet200WithTimeBoundToken(PactDslWithProvider builder) throws JSONException {

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);


        Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        params.put("redirect_uri", redirect_uri);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);
        params.put("scope", "openid roles profile");
        params.put("username", "emCaseOfficer@fake.hmcts.net");
        params.put("password", "Password123");

        return builder
            .given("I have obtained an access_token as a user", params)
            .uponReceiving("Provider responds back with time bound token to Annotation API")
            .path(IDAM_OPENID_TOKEN_URL)
            .method(HttpMethod.POST.toString())
            .headers(headers)
            .willRespondWith()
            .body(createAuthResponse())
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeGetIdamAccessTokenAndGet200WithTimeBoundToken")
    public void should_post_to_token_endpoint_and_receive_Complete_access_token_with_200_response(MockServer mockServer)
        throws JSONException {

        String actualResponseBody =

            SerenityRest
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all(true)
                .when()
                .post(mockServer.getUrl() + IDAM_OPENID_TOKEN_URL)
                .then()
                .statusCode(200)
                .and()
                .extract()
                .asString();

        JSONObject response = new JSONObject(actualResponseBody);

        ACCESS_TOKEN = response.getString("access_token");

        assertThat(response).isNotNull();
        assertThat(response.getString("access_token")).isNotBlank();
        assertThat(response.getString("refresh_token")).isNotBlank();
        assertThat(response.getString("id_token")).isNotBlank();
        assertThat(response.getString("scope")).isNotBlank();
        assertThat(response.getString("token_type")).isEqualTo("Bearer");
        assertThat(response.getString("expires_in")).isNotBlank();
    }



    private PactDslJsonBody createAuthResponse() {

        return new PactDslJsonBody()
            .stringType("access_token", "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FI")
            .stringType("refresh_token", "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92V")
            .stringType("scope", "openid roles profile")
            .stringType("id_token", "eyJ0eXAiOiJKV1QiLCJraWQiOiJiL082T3ZWdjEre")
            .stringType("token_type", "Bearer")
            .stringType("expires_in","28798");
    }

    private PactDslJsonBody createAuthCodeToReturn() {

        return new PactDslJsonBody()
            .stringType("code", "4mTQBb9rwVUUfW3quFPLICL9BQA");
    }

    private PactDslJsonBody createCookieToReturn() {

        return new PactDslJsonBody()
            .stringType("cookie", "Idam.Session=4mTQBb9rwVUUfW3quFPLICL9BQA.*AAJTSQACMDIAAlNLABw4bmdTUnV2RHJBMUJzNDZRZGxLTFNqQWk1MkE9AAR0eXBlAANDVFMAAlMxAAIwMQ");
    }

    private PactDslJsonBody createUserDetailsResponse() {
        PactDslJsonArray array = new PactDslJsonArray().stringValue("citizen");

        return new PactDslJsonBody()
            .stringType("_id", "1234-2345-3456-4567")
            .stringType("accountStatus", "active")
            .stringType("givenName", "emCaseOfficer")
            .stringType("sn", "Jar")
            .stringType("userName", "emCaseOfficer@email.net")
            .stringType("mail", "emCaseOfficer@email.net")
            .stringType("roles", array.toString());
    }
}
