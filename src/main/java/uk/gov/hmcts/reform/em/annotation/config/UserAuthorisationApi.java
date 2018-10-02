package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenDetails;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@FeignClient(name = "idam-user-auth", url = "${idam.user-auth.url}")
public interface UserAuthorisationApi {
    @GetMapping(value = "/details")
    UserTokenDetails getUserDetails(@RequestHeader(AUTHORIZATION) final String authHeader);
}
