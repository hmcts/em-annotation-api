package uk.gov.hmcts.reform.em.annotation.service.impl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.annotation.service.util.StringUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class StringUtilitiesTest {

    @Test
    void convertValidLog() {
        String dangerousLogStr = "this %0d is \r an %0a apple \n .";
        String safeLogStr = "this  is  an  apple  .";
        Assertions.assertNotEquals(dangerousLogStr, safeLogStr);
        Assertions.assertEquals(safeLogStr, StringUtilities.convertValidLogString(dangerousLogStr));
    }

    @Test
    void convertValidLogEmptyList() {
        Assertions.assertNotNull(StringUtilities.convertValidLogString(new ArrayList<>()));
    }

    @Test
    void convertValidLogNonEmptyList() {

        String dangerousLogStr = "this %0d is \r an %0a apple \n .";
        String dangerousLogStr2 = "this %0d is \r an %0a mango \n .";
        String safeLogStr = "this  is  an  apple  .";
        List<String> initialList = Arrays.asList(dangerousLogStr, dangerousLogStr2);

        List<String> sanitisedList = StringUtilities.convertValidLogString(initialList);

        Assertions.assertEquals(initialList.size(), sanitisedList.size());
        Assertions.assertEquals(safeLogStr, sanitisedList.get(0));
    }

    @Test
    void convertValidLogEmptyListUUID() {
        Assertions.assertNotNull(StringUtilities.convertValidLogUUID(new ArrayList<>()));
    }
}
