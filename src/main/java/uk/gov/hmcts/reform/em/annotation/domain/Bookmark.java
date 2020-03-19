package uk.gov.hmcts.reform.em.annotation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Bookmark.
 */
@Entity
@Table(name = "bookmark")
public class Bookmark extends AbstractAuditingEntity implements Serializable {

    @Id
    private UUID id;

    @Size(min = 1, max = 30)
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "num", nullable = false)
    // pdfjs specific location information
    private int num;

    @Column(name = "x_coordinate", nullable = false)
    private float xCoordinate;

    @Column(name = "y_coordinate", nullable = false)
    private float yCoordinate;

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

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(float xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public float getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
}
