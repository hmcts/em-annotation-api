package uk.gov.hmcts.reform.em.annotation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "jhi_entity_audit_event")

public class EntityAuditEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @NotNull
    @Size(max = 255)
    @Column(name = "entity_type", length = 255, nullable = false)
    private String entityType;

    @NotNull
    @Size(max = 20)
    @Column(name = "action", length = 20, nullable = false)
    private String action;

    @Column(name = "entity_value_v2", columnDefinition = "text")
    private String entityValueV2;

    @Column(name = "commit_version")
    private Integer commitVersion;

    @Size(max = 100)
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @NotNull
    @Column(name = "modified_date", nullable = false)
    private Instant modifiedDate;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityValueV2() {
        return entityValueV2;
    }

    public void setEntityValueV2(String entityValueV2) {
        this.entityValueV2 = entityValueV2;
    }

    public Integer getCommitVersion() {
        return commitVersion;
    }

    public void setCommitVersion(Integer commitVersion) {
        this.commitVersion = commitVersion;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityAuditEvent entityAuditEvent = (EntityAuditEvent) o;
        return Objects.equals(id, entityAuditEvent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EntityAuditEvent{"
                + " id=" + id
                + ", entityId=" + entityId
                + ", entityType='" + entityType + '\''
                + ", action='" + action + '\''
                + ", commitVersion=" + commitVersion
                + ", modifiedBy='" + modifiedBy + '\''
                + ", modifiedDate=" + modifiedDate
                + '}';
    }
}
