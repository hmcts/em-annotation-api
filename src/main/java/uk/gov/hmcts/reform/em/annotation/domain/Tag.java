package uk.gov.hmcts.reform.em.annotation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Comment Tag.
 */
@Entity
@Table(name = "tag")
@IdClass(TagId.class)
public class Tag implements Serializable {

    @Id
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Id
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
}
