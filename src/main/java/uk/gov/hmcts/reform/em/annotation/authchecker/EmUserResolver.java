package uk.gov.hmcts.reform.em.annotation.authchecker;

import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.parser.idam.core.user.token.UserTokenParser;

public class EmUserResolver implements SubjectResolver<User> {

    final UserTokenParser<EmUserTokenDetails> emUserTokenParser;

    public EmUserResolver(UserTokenParser<EmUserTokenDetails> emUserTokenParser) {
        this.emUserTokenParser = emUserTokenParser;
    }

    @Override
    public User getTokenDetails(String bearerToken) {
        EmUserTokenDetails emUserTokenDetails = emUserTokenParser.parse(bearerToken);
        return new EmUser(
                emUserTokenDetails.getId(),
                emUserTokenDetails.getRoles(),
                emUserTokenDetails.getForename(),
                emUserTokenDetails.getSurname(),
                emUserTokenDetails.getEmail()
        );
    }
}
