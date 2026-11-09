package uk.gov.hmcts.reform.em.annotation.config.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import tools.jackson.databind.ObjectMapper;
import uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity;
import uk.gov.hmcts.reform.em.annotation.domain.EntityAuditEvent;
import uk.gov.hmcts.reform.em.annotation.repository.EntityAuditEventRepository;


/**
 * Async Entity Audit Event writer.
 * This is invoked by Hibernate entity listeners to write audit event for entitities
 */
@Component
public class AsyncEntityAuditEventWriter {

    private final Logger log = LoggerFactory.getLogger(AsyncEntityAuditEventWriter.class);

    private final EntityAuditEventRepository auditingEntityRepository;

    private final ObjectMapper objectMapper; //Jackson object mapper

    public AsyncEntityAuditEventWriter(EntityAuditEventRepository auditingEntityRepository, ObjectMapper objectMapper) {
        this.auditingEntityRepository = auditingEntityRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Writes audit events to DB asynchronously in a new thread.
     */
    @Async
    public void writeAuditEvent(AbstractAuditingEntity target, EntityAuditAction action) {
        var logAction = action != null ? action.value() : null;
        log.debug("-------------- Post {} audit  --------------", logAction);
        try {
            EntityAuditEvent auditedEntity = prepareAuditEntity(target, action);
            if (auditedEntity != null) {
                auditingEntityRepository.save(auditedEntity);
            }
        } catch (Exception e) {
            log.error("Exception while persisting audit entity for [{} ID: {}] error: {}",
                target.getClass().getSimpleName(), target.getId(), e.getMessage(), e);
        }
    }

    /**
     * Method to prepare auditing entity.
     *
     * @param entity takes entity
     * @param action takes action
     * @return will return audit event
     */
    private EntityAuditEvent prepareAuditEntity(final AbstractAuditingEntity entity, EntityAuditAction action) {
        EntityAuditEvent auditedEntity = new EntityAuditEvent();
        Class<?> entityClass = entity.getClass(); // Retrieve entity class with reflection
        auditedEntity.setAction(action == null ? null : action.value());
        auditedEntity.setEntityType(entityClass.getName());
        String entityData;
        log.trace("Getting Entity Content");
        try {
            entityData = objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            log.error("Could not serialize audit data for [{} ID: {}] due to [{}]. Root cause: {}",
                ClassUtils.getUserClass(entity).getSimpleName(),
                entity.getId(),
                e.getClass().getSimpleName(),
                NestedExceptionUtils.getMostSpecificCause(e).getClass().getSimpleName(),
                e
            );
            return null;
        }
        auditedEntity.setEntityId(entity.getId());
        auditedEntity.setEntityValueV2(entityData);
        if (EntityAuditAction.CREATE.equals(action)) {
            auditedEntity.setModifiedBy(entity.getCreatedBy());
            auditedEntity.setModifiedDate(entity.getCreatedDate());
            auditedEntity.setCommitVersion(1);
        } else {
            auditedEntity.setModifiedBy(entity.getLastModifiedBy());
            auditedEntity.setModifiedDate(entity.getLastModifiedDate());
            calculateVersion(auditedEntity);
        }
        log.trace("Prepared audit entity for [{} ID: {}] with version {}",
            auditedEntity.getEntityType(), auditedEntity.getEntityId(), auditedEntity.getCommitVersion());
        return auditedEntity;
    }

    private void calculateVersion(EntityAuditEvent auditedEntity) {
        log.trace("Version calculation. for update/remove");
        Integer lastCommitVersion = auditingEntityRepository.findMaxCommitVersion(auditedEntity
            .getEntityType(), auditedEntity.getEntityId());
        log.trace("Last commit version of entity => {}", lastCommitVersion);
        if (lastCommitVersion != null && lastCommitVersion != 0) {
            log.trace("Present. Adding version..");
            auditedEntity.setCommitVersion(lastCommitVersion + 1);
        } else {
            log.trace("No entities.. Adding new version 1");
            auditedEntity.setCommitVersion(1);
        }
    }
}
