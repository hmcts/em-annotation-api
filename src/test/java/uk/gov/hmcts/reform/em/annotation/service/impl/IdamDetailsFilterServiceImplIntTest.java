package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.TestSecurityConfiguration;
import uk.gov.hmcts.reform.em.annotation.authchecker.EmServiceAndUserDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.em.annotation.service.IdamDetailsFilterService;

import java.util.Arrays;

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

        Authentication authentication = Mockito.mock(Authentication.class);


        BDDMockito.given(authentication.getPrincipal()).willReturn(
                new EmServiceAndUserDetails("1", "token",
                        Arrays.asList("role_x"),
                        "f_name", "s_name", "a@b.com", "1"));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        idamDetailsFilterService.saveIdamDetails();

        Assert.assertTrue(idamDetailsRepository.existsById("1"));


    }

}