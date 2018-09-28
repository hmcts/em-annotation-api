package uk.gov.hmcts.reform.em.annotation.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.em.annotation.domain.enumeration.AnnotationType;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO for the Annotation entity.
 */
public class AnnotationDTO extends AbstractAuditingDTO implements Serializable {

    private UUID id;

    @JsonProperty("type")
    private AnnotationType annotationType;

    private Integer page;

    private String color;

    private Long annotationSetId;

    private Set<CommentDTO> comments;

    private Set<RectangleDTO> rectangles;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Long getAnnotationSetId() {
        return annotationSetId;
    }

    public void setAnnotationSetId(Long annotationSetId) {
        this.annotationSetId = annotationSetId;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(Set<CommentDTO> comments) {
        this.comments = comments;
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
        return "AnnotationDTO{" +
            "id=" + id +
            ", annotationType=" + annotationType +
            ", page=" + page +
            ", color='" + color + '\'' +
            ", annotationSetId=" + annotationSetId +
            ", comments=" + comments +
            ", rectangles=" + rectangles +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
