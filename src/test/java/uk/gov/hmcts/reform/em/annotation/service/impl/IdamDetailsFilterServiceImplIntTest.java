package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.TestSecurityConfiguration;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
public class IdamDetailsFilterServiceImplIntTest extends BaseTest {

    @Autowired
    IdamDetailsFilterService idamDetailsFilterService;

    @Autowired
    IdamDetailsRepository idamDetailsRepository;

    @Test
    public void testSaveIdamDetails() {

        Assert.assertFalse(idamDetailsRepository.existsById("1"));

        UserDetails userDetails = UserDetails.builder()
                .id("1")
                .forename("FN")
                .email("user@idam.com")
                .build();
        idamDetailsFilterService.saveIdamDetails(userDetails);

        Assert.assertTrue(idamDetailsRepository.existsById("1"));


    }

}