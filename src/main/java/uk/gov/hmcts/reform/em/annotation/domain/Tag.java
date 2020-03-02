package uk.gov.hmcts.reform.em.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Comment Tag.
 */
@Entity
@Table(name = "tag")
public class Tag implements Serializable {

    @Id
    private UUID id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Size(max = 20)
    @Column(name = "label", length = 20, nullable = false)
    private String label;

    @Column(name = "color", length = 20)
    private String color;

    @ManyToOne
    @JsonIgnoreProperties("tags")
    @JsonIgnore
    private Annotation annotation;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Tag annotation(Annotation annotation) {
        this.annotation = annotation;
        return this;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
}
