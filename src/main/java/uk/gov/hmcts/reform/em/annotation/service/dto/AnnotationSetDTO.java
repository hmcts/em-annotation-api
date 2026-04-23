package uk.gov.hmcts.reform.em.annotation.service.dto;

import uk.gov.hmcts.reform.em.annotation.service.util.ObjectUtilities;
import uk.gov.hmcts.reform.em.annotation.util.Identifer;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO for the AnnotationSet entity.
 */
public class AnnotationSetDTO extends AbstractAuditingDTO implements Serializable, Identifer {

    private UUID id;

    private String documentId;

    private Set<AnnotationDTO> annotations;

    public Set<AnnotationDTO> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<AnnotationDTO> annotations) {
        this.annotations = annotations;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object o) {
        return ObjectUtilities.equals(this, o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnnotationSetDTO{"
                + " id=" + id
                + ", documentId='" + documentId + '\''
                + '}';
    }
}
