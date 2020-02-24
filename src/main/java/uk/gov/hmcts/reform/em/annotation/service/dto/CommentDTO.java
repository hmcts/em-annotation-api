package uk.gov.hmcts.reform.em.annotation.service.dto;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO for the Comment entity.
 */
public class CommentDTO extends AbstractAuditingDTO implements Serializable {

    private UUID id;

    @Size(max = 5000)
    private String content;

    private UUID annotationId;

    private Set<CommentTagDTO> commentTags = new HashSet<>();

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

    public Set<CommentTagDTO> getCommentTags() {
        return commentTags;
    }

    public void setCommentTags(Set<CommentTagDTO> commentTags) {
        this.commentTags = commentTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommentDTO commentDTO = (CommentDTO) o;
        if (commentDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), commentDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", annotation=" + getAnnotationId() +
            "}";
    }
}
