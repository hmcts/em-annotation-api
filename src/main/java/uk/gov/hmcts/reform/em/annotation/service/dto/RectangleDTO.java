package uk.gov.hmcts.reform.em.annotation.service.dto;

import uk.gov.hmcts.reform.em.annotation.service.util.ObjectUtilities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the Rectangle entity.
 */
@SuppressWarnings({"MemberName","ParameterName"})
public class RectangleDTO extends AbstractAuditingDTO implements Serializable {

    private UUID id;

    private Double x;

    private Double y;

    private Double width;

    private Double height;

    private UUID annotationId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
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
        return "RectangleDTO{"
                + " id=" + id
                + ", x=" + x
                + ", y=" + y
                + ", width=" + width
                + ", height=" + height
                + ", annotationId=" + annotationId
                + '}';
    }
}
