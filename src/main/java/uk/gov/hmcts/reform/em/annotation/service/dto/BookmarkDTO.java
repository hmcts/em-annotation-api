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

    private int num;

    private int xCoordinate;

    private int yCoordinate;

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

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
