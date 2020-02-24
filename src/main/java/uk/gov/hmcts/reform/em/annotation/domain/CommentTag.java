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
@Table(name = "comment_tag")
public class CommentTag implements Serializable {

    @Id
    @Column(name = "name", length = 35, nullable = false)
    private String name;

    @Size(max = 20)
    @Column(name = "label", length = 20, nullable = false)
    private String label;

    @ManyToOne
    @JsonIgnoreProperties("commentTags")
    @JsonIgnore
    private Comment comment;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
