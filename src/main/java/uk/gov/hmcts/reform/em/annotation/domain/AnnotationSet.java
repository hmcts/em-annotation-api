package uk.gov.hmcts.reform.em.annotation.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A AnnotationSet.
 */
@Entity
@Table(name = "annotation_set", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"created_by", "document_id"})
})
public class AnnotationSet extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    @Column(name = "document_id")
    private String documentId;

    @OneToMany(mappedBy = "annotationSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Annotation> annotations = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public AnnotationSet documentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public AnnotationSet annotations(Set<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public AnnotationSet addAnnotations(Annotation annotation) {
        this.annotations.add(annotation);
        annotation.setAnnotationSet(this);
        return this;
    }

    public AnnotationSet removeAnnotations(Annotation annotation) {
        this.annotations.remove(annotation);
        annotation.setAnnotationSet(null);
        return this;
    }

    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
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
        AnnotationSet annotationSet = (AnnotationSet) o;
        if (annotationSet.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), annotationSet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AnnotationSet{"
                + " id=" + id
                + ", documentId='" + documentId + '\''
                + '}';
    }
}
