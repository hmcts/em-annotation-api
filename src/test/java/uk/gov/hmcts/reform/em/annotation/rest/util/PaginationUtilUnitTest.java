package uk.gov.hmcts.reform.em.annotation.rest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Tests based on parsing algorithm in app/components/util/pagination-util.service.js.
 *
 * @see PaginationUtil
 */
class PaginationUtilUnitTest {

    @Test
    void generatePaginationHttpHeadersTest() {
        String baseUrl = "/api/_search/example";
        List<String> content = new ArrayList<>();
        Page<String> page = new PageImpl<>(content, PageRequest.of(6, 50), 400L);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, baseUrl);
        List<String> strHeaders = headers.get(HttpHeaders.LINK);
        Assertions.assertNotNull(strHeaders);
        Assertions.assertEquals(1, strHeaders.size());
        String headerData = strHeaders.get(0);
        Assertions.assertEquals(4, headerData.split(",").length);
        String expectedData = "</api/_search/example?page=7&size=50>; rel=\"next\","
                + "</api/_search/example?page=5&size=50>; rel=\"prev\","
                + "</api/_search/example?page=7&size=50>; rel=\"last\","
                + "</api/_search/example?page=0&size=50>; rel=\"first\"";
        Assertions.assertEquals(expectedData, headerData);
        final List<String> xTotalCount = headers.get("X-Total-Count");
        Assertions.assertNotNull(xTotalCount);
        assertThat(xTotalCount.size()).isEqualTo(1);
        assertThat(Long.valueOf(xTotalCount.get(0))).isEqualTo(400L);
    }

}
