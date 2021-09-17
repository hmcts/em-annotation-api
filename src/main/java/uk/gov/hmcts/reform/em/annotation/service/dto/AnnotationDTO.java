package uk.gov.hmcts.reform.em.annotation.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO for the Annotation entity.
 */
public class AnnotationDTO extends AbstractAuditingDTO implements Serializable {

    private UUID id;

    @JsonProperty("type")
    private String annotationType;

    private Integer page;

    private String color;

    private UUID annotationSetId;

    private Set<CommentDTO> comments = new HashSet<>();

    private Set<TagDTO> tags = new HashSet<>();

    private Set<RectangleDTO> rectangles = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public UUID getAnnotationSetId() {
        return annotationSetId;
    }

    public void setAnnotationSetId(UUID annotationSetId) {
        this.annotationSetId = annotationSetId;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDTO> comments) {
        this.comments = comments;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public Set<RectangleDTO> getRectangles() {
        return rectangles;
    }

    public void setRectangles(Set<RectangleDTO> rectangles) {
        this.rectangles = rectangles;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AnnotationDTO annotationDTO = (AnnotationDTO) o;
        if (annotationDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), annotationDTO.getId());
    }

    @Override
    public String toString() {
        return "AnnotationDTO{"
                + " id=" + id
                + ", annotationType='" + annotationType + '\''
                + ", page=" + page
                + ", color='" + color + '\''
                + ", annotationSetId=" + annotationSetId
                + ", comments=" + comments
                + ", tags=" + tags
                + ", rectangles=" + rectangles
                + ", documentId='" + documentId + '\''
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
