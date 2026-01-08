package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.IdamDetailsRepository;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdamDetailsFilterServiceImplTest {

    @Mock
    private IdamDetailsRepository idamDetailsRepository;

    @InjectMocks
    private IdamDetailsFilterServiceImpl idamDetailsFilterService;

    @Test
    @DisplayName("Save IdamDetails when user ID is valid and does not exist in DB")
    void testSaveIdamDetailsNewUser() {
        UserInfo userInfo = UserInfo.builder()
            .uid("12345")
            .givenName("John")
            .familyName("Doe")
            .sub("john.doe@example.com")
            .build();

        when(idamDetailsRepository.existsById("12345")).thenReturn(false);

        idamDetailsFilterService.saveIdamDetails(userInfo);

        ArgumentCaptor<IdamDetails> captor = ArgumentCaptor.forClass(IdamDetails.class);
        verify(idamDetailsRepository).save(captor.capture());

        IdamDetails savedDetails = captor.getValue();
        assertThat(savedDetails.getId()).isEqualTo("12345");
        assertThat(savedDetails.getForename()).isEqualTo("John");
        assertThat(savedDetails.getSurname()).isEqualTo("Doe");
        assertThat(savedDetails.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Do not save IdamDetails when user already exists")
    void testSaveIdamDetailsExistingUser() {
        UserInfo userInfo = UserInfo.builder()
            .uid("12345")
            .givenName("John")
            .build();

        when(idamDetailsRepository.existsById("12345")).thenReturn(true);

        idamDetailsFilterService.saveIdamDetails(userInfo);

        verify(idamDetailsRepository, never()).save(any(IdamDetails.class));
    }

    @Test
    @DisplayName("Do not save IdamDetails when UserInfo UID is null")
    void testSaveIdamDetailsNullId() {
        UserInfo userInfo = UserInfo.builder()
            .uid(null)
            .givenName("John")
            .build();

        idamDetailsFilterService.saveIdamDetails(userInfo);

        verify(idamDetailsRepository, never()).existsById(anyString());
        verify(idamDetailsRepository, never()).save(any(IdamDetails.class));
    }

    @Test
    @DisplayName("Do not save IdamDetails when UserInfo UID is empty")
    void testSaveIdamDetailsEmptyId() {
        UserInfo userInfo = UserInfo.builder()
            .uid("")
            .givenName("John")
            .build();

        idamDetailsFilterService.saveIdamDetails(userInfo);

        verify(idamDetailsRepository, never()).existsById(anyString());
        verify(idamDetailsRepository, never()).save(any(IdamDetails.class));
    }
}