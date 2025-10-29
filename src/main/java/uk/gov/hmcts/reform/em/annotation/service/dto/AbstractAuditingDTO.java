package uk.gov.hmcts.reform.em.annotation.service.dto;

import org.springframework.data.annotation.ReadOnlyProperty;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;

import java.io.Serializable;
import java.time.Instant;

/**
 * Base abstract class for DTO which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
public abstract class AbstractAuditingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ReadOnlyProperty
    private String createdBy;

    @ReadOnlyProperty
    private IdamDetails createdByDetails;
    @ReadOnlyProperty
    private IdamDetails lastModifiedByDetails;

    @ReadOnlyProperty
    private Instant createdDate = Instant.now();

    private String lastModifiedBy;

    private Instant lastModifiedDate = Instant.now();

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public IdamDetails getCreatedByDetails() {
        return createdByDetails;
    }

    public void setCreatedByDetails(IdamDetails createdByDetails) {
        this.createdByDetails = createdByDetails;
    }

    public IdamDetails getLastModifiedByDetails() {
        return lastModifiedByDetails;
    }

    public void setLastModifiedByDetails(IdamDetails lastModifiedByDetails) {
        this.lastModifiedByDetails = lastModifiedByDetails;
    }
}
