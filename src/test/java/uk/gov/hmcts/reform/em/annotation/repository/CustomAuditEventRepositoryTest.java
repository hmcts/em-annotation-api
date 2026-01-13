package uk.gov.hmcts.reform.em.annotation.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.audit.AuditEvent;
import uk.gov.hmcts.reform.em.annotation.config.Constants;
import uk.gov.hmcts.reform.em.annotation.config.audit.AuditEventConverter;
import uk.gov.hmcts.reform.em.annotation.domain.PersistentAuditEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.em.annotation.repository.CustomAuditEventRepository.EVENT_DATA_COLUMN_MAX_LENGTH;

@ExtendWith(MockitoExtension.class)
class CustomAuditEventRepositoryTest {

    @Mock
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Mock
    private AuditEventConverter auditEventConverter;

    @InjectMocks
    private CustomAuditEventRepository customAuditEventRepository;

    @Captor
    private ArgumentCaptor<PersistentAuditEvent> persistentEventCaptor;

    @Test
    @DisplayName("add() should save event when data is valid and user is not anonymous")
    void addAuditEvent() {
        Instant now = Instant.now();
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");

        AuditEvent event = new AuditEvent(now, "test-user", "test-type", data);

        Map<String, String> convertedData = new HashMap<>();
        convertedData.put("test-key", "test-value");

        when(auditEventConverter.convertDataToStrings(data)).thenReturn(convertedData);

        customAuditEventRepository.add(event);

        verify(persistenceAuditEventRepository).save(persistentEventCaptor.capture());
        PersistentAuditEvent capturedEvent = persistentEventCaptor.getValue();

        assertThat(capturedEvent.getPrincipal()).isEqualTo("test-user");
        assertThat(capturedEvent.getAuditEventType()).isEqualTo("test-type");
        assertThat(capturedEvent.getAuditEventDate()).isEqualTo(now);
        assertThat(capturedEvent.getData()).containsEntry("test-key", "test-value");
    }

    @Test
    @DisplayName("add() should truncate data values exceeding max column length")
    void addAuditEventTruncateLargeData() {
        Map<String, Object> data = new HashMap<>();
        String largeString = "a".repeat(EVENT_DATA_COLUMN_MAX_LENGTH + 20);
        data.put("large-key", largeString);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);

        Map<String, String> convertedData = new HashMap<>();
        convertedData.put("large-key", largeString);

        when(auditEventConverter.convertDataToStrings(data)).thenReturn(convertedData);

        customAuditEventRepository.add(event);

        verify(persistenceAuditEventRepository).save(persistentEventCaptor.capture());
        PersistentAuditEvent capturedEvent = persistentEventCaptor.getValue();

        String actualData = capturedEvent.getData().get("large-key");
        assertThat(actualData).hasSize(EVENT_DATA_COLUMN_MAX_LENGTH);
        assertThat(largeString).startsWith(actualData);
    }

    @Test
    @DisplayName("add() should not save event when user is Anonymous")
    void addAuditEventWithAnonymousUser() {
        AuditEvent event = new AuditEvent(Constants.ANONYMOUS_USER, "test-type", Collections.emptyMap());

        customAuditEventRepository.add(event);

        verify(persistenceAuditEventRepository, never()).save(any(PersistentAuditEvent.class));
    }

    @Test
    @DisplayName("add() should not save event when type is AUTHORIZATION_FAILURE")
    void addAuditEventWithAuthorizationFailureType() {
        AuditEvent event = new AuditEvent("test-user", "AUTHORIZATION_FAILURE", Collections.emptyMap());

        customAuditEventRepository.add(event);

        verify(persistenceAuditEventRepository, never()).save(any(PersistentAuditEvent.class));
    }

    @Test
    @DisplayName("add() should handle null values in data map gracefully")
    void addAuditEventWithNullDataValues() {
        Map<String, Object> data = new HashMap<>();
        data.put("null-key", null);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);

        Map<String, String> convertedData = new HashMap<>();
        convertedData.put("null-key", null);

        when(auditEventConverter.convertDataToStrings(anyMap())).thenReturn(convertedData);

        customAuditEventRepository.add(event);

        verify(persistenceAuditEventRepository).save(persistentEventCaptor.capture());
        PersistentAuditEvent capturedEvent = persistentEventCaptor.getValue();

        assertThat(capturedEvent.getData()).containsKey("null-key");
        assertThat(capturedEvent.getData().get("null-key")).isNull();
    }

    @Test
    @DisplayName("find() should retrieve events from repository and convert them")
    void findAuditEvent() {
        String principal = "test-user";
        String type = "test-type";
        Instant after = Instant.now().minusSeconds(3600);

        PersistentAuditEvent persistentEvent = new PersistentAuditEvent();
        persistentEvent.setPrincipal(principal);
        List<PersistentAuditEvent> persistentList = List.of(persistentEvent);

        AuditEvent expectedEvent = new AuditEvent(principal, type, Collections.emptyMap());
        List<AuditEvent> convertedList = List.of(expectedEvent);

        when(persistenceAuditEventRepository
            .findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, after, type))
            .thenReturn(persistentList);
        when(auditEventConverter.convertToAuditEvent(persistentList)).thenReturn(convertedList);

        List<AuditEvent> result = customAuditEventRepository.find(principal, after, type);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedEvent);
        verify(persistenceAuditEventRepository)
            .findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, after, type);
        verify(auditEventConverter).convertToAuditEvent(persistentList);
    }
}