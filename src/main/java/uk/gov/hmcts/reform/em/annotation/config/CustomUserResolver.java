package uk.gov.hmcts.reform.em.annotation.config;

import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.core.user.UserResolver;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenDetails;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

public class CustomUserResolver extends UserResolver {

    private final UserAuthorisationApi userAuthorisationApi;

    public CustomUserResolver(final UserTokenParser userTokenParser, UserAuthorisationApi userAuthorisationApi) {
        super(userTokenParser);
        this.userAuthorisationApi = userAuthorisationApi;
    }

    @Override
    public User getTokenDetails(String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            bearerToken = "Bearer " + bearerToken;
        }
        UserTokenDetails details = userAuthorisationApi.getUserDetails(bearerToken);
        return new User(details.getId(), details.getRoles());
    }

}
