package uk.gov.hmcts.reform.em.annotation.service.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A DTO for the Metadata entity.
 */
@Data
public class MetadataDto {

    @NotNull
    private Integer rotationAngle;

    @NotNull
    private UUID documentId;
}
