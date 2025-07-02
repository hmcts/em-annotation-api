package uk.gov.hmcts.reform.em.annotation.config.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity;
import uk.gov.hmcts.reform.em.annotation.domain.EntityAuditEvent;
import uk.gov.hmcts.reform.em.annotation.repository.EntityAuditEventRepository;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncEntityAuditEventWriterTest {

    @Mock
    private EntityAuditEventRepository auditingEntityRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AsyncEntityAuditEventWriter asyncEntityAuditEventWriter;

    @Mock
    private AbstractAuditingEntity mockEntity;

    @Test
    void writeAuditEventPersistsWithCorrectActionForUpdate() throws Exception {

        when(objectMapper.writeValueAsString(mockEntity)).thenReturn("{\"id\":\"456\"}");
        when(mockEntity.getLastModifiedBy()).thenReturn("modifier");
        when(mockEntity.getLastModifiedDate()).thenReturn(Instant.now());
        when(auditingEntityRepository.findMaxCommitVersion(any(), any())).thenReturn(2);

        asyncEntityAuditEventWriter.writeAuditEvent(mockEntity, EntityAuditAction.UPDATE);

        verify(auditingEntityRepository).save(any(EntityAuditEvent.class));
    }

    @Test
    void writeAuditEventPersistsWithCorrectActionForUpdateWithoutCommitVersion() throws Exception {

        when(objectMapper.writeValueAsString(mockEntity)).thenReturn("{\"id\":\"456\"}");
        when(mockEntity.getLastModifiedBy()).thenReturn("modifier");
        when(mockEntity.getLastModifiedDate()).thenReturn(Instant.now());

        asyncEntityAuditEventWriter.writeAuditEvent(mockEntity, EntityAuditAction.UPDATE);

        verify(auditingEntityRepository).save(any(EntityAuditEvent.class));
    }

    @Test
    void writeAuditEventPersistsWithCorrectActionForCreate() throws Exception {
        when(objectMapper.writeValueAsString(mockEntity)).thenReturn("{\"id\":\"789\"}");
        when(mockEntity.getCreatedBy()).thenReturn("user");
        when(mockEntity.getCreatedDate()).thenReturn(Instant.now());

        asyncEntityAuditEventWriter.writeAuditEvent(mockEntity, EntityAuditAction.CREATE);

        verify(auditingEntityRepository).save(any(EntityAuditEvent.class));
    }

    @Test
    void writeAuditEventDoesNotPersistWhenSerializationFails() throws Exception {
        when(objectMapper.writeValueAsString(mockEntity)).thenThrow(new RuntimeException("Serialization failed"));

        asyncEntityAuditEventWriter.writeAuditEvent(mockEntity, EntityAuditAction.CREATE);

        verify(auditingEntityRepository, never()).save(any(EntityAuditEvent.class));
    }

    @Test
    void writeAuditEventDoesNotPersistWithIllegalArgumentException() throws Exception {
        when(objectMapper.writeValueAsString(mockEntity))
                .thenThrow(new IllegalArgumentException("IllegalArgumentException example"));

        asyncEntityAuditEventWriter.writeAuditEvent(mockEntity, EntityAuditAction.CREATE);

        verify(auditingEntityRepository, never()).save(any(EntityAuditEvent.class));
    }
}
