package uk.gov.hmcts.reform.em.annotation.service.impl.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.annotation.service.util.StringUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class StringUtilitiesTest {

    @Test
    void convertValidLog() {
        String dangerousLogStr = "this %0d is \r an %0a apple \n .";
        String safeLogStr = "this  is  an  apple  .";
        assertNotEquals(dangerousLogStr, safeLogStr);
        assertEquals(safeLogStr, StringUtilities.convertValidLogString(dangerousLogStr));
    }

    @Test
    void convertValidLogEmptyList() {
        assertNotNull(StringUtilities.convertValidLogString(new ArrayList<>()));
    }

    @Test
    void convertValidLogNonEmptyList() {

        String dangerousLogStr = "this %0d is \r an %0a apple \n .";
        String dangerousLogStr2 = "this %0d is \r an %0a mango \n .";
        String safeLogStr = "this  is  an  apple  .";
        List<String> initialList = Arrays.asList(dangerousLogStr, dangerousLogStr2);

        List<String> sanitisedList = StringUtilities.convertValidLogString(initialList);

        assertEquals(initialList.size(), sanitisedList.size());
        assertEquals(safeLogStr, sanitisedList.get(0));
    }

    @Test
    void convertValidLogEmptyListUUID() {
        assertNotNull(StringUtilities.convertValidLogUUID(new ArrayList<>()));
    }
}
