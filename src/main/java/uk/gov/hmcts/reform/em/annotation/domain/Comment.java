package uk.gov.hmcts.reform.em.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import uk.gov.hmcts.reform.em.annotation.service.util.ObjectUtilities;
import uk.gov.hmcts.reform.em.annotation.util.Identifer;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
public class Comment extends AbstractAuditingEntity implements Serializable, Identifer {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    @Size(max = 5000)
    @Column(name = "content", length = 5000)
    private String content;

    @ManyToOne
    @JsonIgnoreProperties("comments")
    @JsonIgnore
    private Annotation annotation;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public Comment content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Comment annotation(Annotation annotation) {
        this.annotation = annotation;
        return this;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

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
        return "Comment{"
                + " id=" + id
                + ", content='" + content + '\''
                + '}';
    }
}
