package uk.gov.hmcts.reform.em.annotation.service.dto;

import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * A DTO for the Comment Tag entity.
 */
public class CommentTagDTO {

    private String name;

    @Size(max = 10)
    private String label;

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
