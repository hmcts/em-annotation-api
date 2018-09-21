package uk.gov.hmcts.reform.em.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Rectangle.
 */
@Entity
@Table(name = "rectangle")

public class Rectangle extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "x")
    private Integer x;

    @Column(name = "y")
    private Integer y;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @ManyToOne
    @JsonIgnoreProperties("rectangles")
    private Annotation annotation;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public Rectangle x(Integer x) {
        this.x = x;
        return this;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public Rectangle y(Integer y) {
        this.y = y;
        return this;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public Rectangle width(Integer width) {
        this.width = width;
        return this;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public Rectangle height(Integer height) {
        this.height = height;
        return this;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Rectangle annotation(Annotation annotation) {
        this.annotation = annotation;
        return this;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rectangle rectangle = (Rectangle) o;
        if (rectangle.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), rectangle.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Rectangle{" +
            "id=" + getId() +
            ", x=" + getX() +
            ", y=" + getY() +
            ", width=" + getWidth() +
            ", height=" + getHeight() +
            "}";
    }
}
