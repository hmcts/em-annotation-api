package uk.gov.hmcts.reform.em.annotation.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * A DTO for the Metadata entity.
 */
@Data
public class MetadataDto {

    @NotNull(message = "Rotation Angle can not null")
    private Integer rotationAngle;

    @NotNull(message = "DocumentId can not null")
    private UUID documentId;
}
