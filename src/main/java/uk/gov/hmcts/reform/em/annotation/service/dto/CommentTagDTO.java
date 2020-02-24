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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommentTagDTO commentTagDTO = (CommentTagDTO) o;
        if (commentTagDTO.getName() == null || getName() == null) {
            return false;
        }
        return Objects.equals(getName(), commentTagDTO.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "name=" + getName() + "'" +
                ", label=" + getLabel() + "'" +
                "}";
    }
}
