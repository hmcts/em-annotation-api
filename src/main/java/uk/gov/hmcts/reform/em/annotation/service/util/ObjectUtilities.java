package uk.gov.hmcts.reform.em.annotation.service.util;

import uk.gov.hmcts.reform.em.annotation.util.Identifer;

import java.util.*;

public class ObjectUtilities {
    public static boolean equals(Identifer instance, Object o) {
        if (instance == o) {
            return true;
        }
        if (o == null || instance.getClass() != o.getClass()) {
            return false;
        }

        Identifer annotationSetDTO = (Identifer) o;
        UUID dtoID = annotationSetDTO.getId();
        UUID id = instance.getId();
        if (dtoID == null || id == null) {
            return false;
        }
        return Objects.equals(id, dtoID);
    }
}