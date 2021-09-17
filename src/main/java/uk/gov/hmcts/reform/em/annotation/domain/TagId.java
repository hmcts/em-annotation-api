package uk.gov.hmcts.reform.em.annotation.domain;

import java.io.Serializable;
import java.util.Objects;

public class TagId implements Serializable {

    private String name;
    private String createdBy;

    public TagId() {}

    public TagId(String name, String createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TagId tagId = (TagId) o;
        return Objects.equals(name, tagId.name) && Objects.equals(createdBy, tagId.createdBy);
    }

    @Override
    public String toString() {
        return "TagId{"
                + " name='" + name + '\''
                + ", createdBy='" + createdBy + '\''
                + '}';
    }
}
