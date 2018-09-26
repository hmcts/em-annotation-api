package uk.gov.hmcts.reform.em.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.gov.hmcts.reform.em.annotation.domain.enumeration.AnnotationType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Annotation.
 */
@Entity
@Table(name = "annotation")

public class Annotation extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "annotation_type")
    private AnnotationType annotationType;

    @Column(name = "page")
    private Integer page;

    @Column(name = "x", precision=10, scale=3)
    private Double x;

    @Column(name = "y", precision=10, scale=3)
    private Double y;

    @Column(name = "width", precision=10, scale=3)
    private Double width;

    @Column(name = "height", precision=10, scale=3)
    private Double height;

    @OneToMany(mappedBy = "annotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "annotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rectangle> rectangles = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("annotations")
    private AnnotationSet annotationSet;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public Annotation annotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
        return this;
    }

    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public Integer getPage() {
        return page;
    }

    public Annotation page(Integer page) {
        this.page = page;
        return this;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Double getX() {
        return x;
    }

    public Annotation x(Double x) {
        this.x = x;
        return this;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public Annotation y(Double y) {
        this.y = y;
        return this;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public Annotation width(Double width) {
        this.width = width;
        return this;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public Annotation height(Double height) {
        this.height = height;
        return this;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Annotation comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Annotation addComments(Comment comment) {
        this.comments.add(comment);
        comment.setAnnotation(this);
        return this;
    }

    public Annotation removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setAnnotation(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Rectangle> getRectangles() {
        return rectangles;
    }

    public Annotation rectangles(Set<Rectangle> rectangles) {
        this.rectangles = rectangles;
        return this;
    }

    public Annotation addRectangles(Rectangle rectangle) {
        this.rectangles.add(rectangle);
        rectangle.setAnnotation(this);
        return this;
    }

    public Annotation removeRectangles(Rectangle rectangle) {
        this.rectangles.remove(rectangle);
        rectangle.setAnnotation(null);
        return this;
    }

    public void setRectangles(Set<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    public AnnotationSet getAnnotationSet() {
        return annotationSet;
    }

    public Annotation annotationSet(AnnotationSet annotationSet) {
        this.annotationSet = annotationSet;
        return this;
    }

    public void setAnnotationSet(AnnotationSet annotationSet) {
        this.annotationSet = annotationSet;
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
        Annotation annotation = (Annotation) o;
        if (annotation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), annotation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Annotation{" +
            "id=" + getId() +
            ", annotationType='" + getAnnotationType() + "'" +
            ", page=" + getPage() +
            ", x=" + getX() +
            ", y=" + getY() +
            ", width=" + getWidth() +
            ", height=" + getHeight() +
            "}";
    }
}
