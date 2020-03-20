package uk.gov.hmcts.reform.em.annotation.service.dto;

import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * A DTO for the Bookmark entity.
 */
public class BookmarkDTO {

    private UUID id;

    @Size(min = 1, max = 30)
    private String name;

    private UUID documentId;

    @Size(max = 50)
    private String createdBy;

    private int num;

    private double xCoordinate;

    private double yCoordinate;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
