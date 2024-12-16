package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.rest.TestSecurityConfiguration;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class IdamDetailsFilterServiceImplIntTest extends BaseTest {

    @Autowired
    IdamDetailsFilterService idamDetailsFilterService;
    @Autowired
    IdamDetailsRepository idamDetailsRepository;

    @Test
    void testSaveIdamDetails() {

        assertFalse(idamDetailsRepository.existsById("1"));

        final UserInfo userInfo = UserInfo.builder()
            .uid("1")
            .givenName("John").familyName("Doe")
            .roles(asList("Admin", "CaseWorker")).build();
        idamDetailsFilterService.saveIdamDetails(userInfo);

        assertTrue(idamDetailsRepository.existsById("1"));

    }

    @Test
    @DisplayName("Save IdamDetails with empty User.id")
    void testSaveIdamDetailsWithEmptyUserDetailsId() {

        final int countBeforeSave = idamDetailsRepository.findAll().size();

        final UserInfo userInfo = UserInfo.builder()
            .uid(null)
            .givenName("John").familyName("Doe")
            .roles(asList("Admin", "CaseWorker")).build();
        idamDetailsFilterService.saveIdamDetails(userInfo);

        final int countPostSave = idamDetailsRepository.findAll().size();

        assertEquals(countBeforeSave, countPostSave);


    }

    @Test
    @DisplayName("Save IdamDetails with UserDetails.surname and Retrieve it to ensure surname has been persisted.")
    void testSaveIdamDetailSurname() {

        final String randomId = random(3, false, true);
        final String surname = random(8, true, false);

        final UserInfo userInfo = UserInfo.builder()
            .uid(randomId)
            .givenName("John").familyName(surname)
            .roles(asList("Admin", "CaseWorker")).build();
        idamDetailsFilterService.saveIdamDetails(userInfo);

        assertTrue(idamDetailsRepository.existsById(randomId));

        final Optional<IdamDetails> optUserDetails = idamDetailsRepository.findById(randomId);

        if (optUserDetails.isPresent()) {
            final IdamDetails idamDetails = optUserDetails.get();
            assertThat(idamDetails.getSurname(), is(surname));
        } else {
            fail(" userDetails not found for %s " + randomId);
        }
    }
}
