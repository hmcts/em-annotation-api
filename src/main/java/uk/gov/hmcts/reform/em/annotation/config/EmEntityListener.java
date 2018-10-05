package uk.gov.hmcts.reform.em.annotation.config;

import org.springframework.data.domain.Auditable;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class EmEntityListener {

    @PrePersist
    public void touchForCreate(Object target) {

        Assert.notNull(target, "Entity must not be null!");

        if (target instanceof AbstractAuditingEntity) {
            AbstractAuditingEntity entity = (AbstractAuditingEntity)target;
            SecurityUtils.getCurrentUserDetails().ifPresent(emServiceAndUserDetails -> {
                entity.setCreatedByForename(emServiceAndUserDetails.getForename());
                entity.setCreatedBySurname(emServiceAndUserDetails.getSurname());
                entity.setCreatedByEmail(emServiceAndUserDetails.getEmail());
            });
        }
    }

    /**
     * Sets modification and creation date and auditor on the target object in case it implements {@link Auditable} on
     * update events.
     *
     * @param target
     */
    @PreUpdate
    public void touchForUpdate(Object target) {

        Assert.notNull(target, "Entity must not be null!");

        if (target instanceof AbstractAuditingEntity) {
            AbstractAuditingEntity entity = (AbstractAuditingEntity)target;
            SecurityUtils.getCurrentUserDetails().ifPresent( emServiceAndUserDetails -> {
                entity.setLastModifiedByForename(emServiceAndUserDetails.getForename());
                entity.setLastModifiedBySurname(emServiceAndUserDetails.getSurname());
                entity.setLastModifiedByEmail(emServiceAndUserDetails.getEmail());
            });
        }
    }

}
