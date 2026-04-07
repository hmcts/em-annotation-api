package uk.gov.hmcts.reform.em.annotation.service.dto;

import jakarta.validation.constraints.Size;import uk.gov.hmcts.reform.em.annotation.service.util.ObjectUtilities;import uk.gov.hmcts.reform.em.annotation.util.Identifer;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the Comment entity.
 */
public class CommentDTO extends AbstractAuditingDTO implements Serializable, Identifer {

    private UUID id;

    @Size(max = 5000)
    private String content;

    private UUID annotationId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(UUID annotationId) {
        this.annotationId = annotationId;
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
        return "CommentDTO{"
                + " id=" + id
                + ", content='" + content + '\''
                + ", annotationId=" + annotationId
                + '}';
    }
}
