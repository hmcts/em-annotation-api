package uk.gov.hmcts.reform.em.annotation.repository;

import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.config.Constants;
import uk.gov.hmcts.reform.em.annotation.config.audit.AuditEventConverter;
import uk.gov.hmcts.reform.em.annotation.domain.PersistentAuditEvent;
import uk.gov.hmcts.reform.em.annotation.rest.TestSecurityConfiguration;
import uk.gov.hmcts.reform.em.annotation.service.AuditEventService;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.em.annotation.repository.CustomAuditEventRepository.EVENT_DATA_COLUMN_MAX_LENGTH;

/**
 * Test class for the CustomAuditEventRepository class.
 *
 * @see CustomAuditEventRepository
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
@Transactional
public class CustomAuditEventRepositoryIntTest {

    @Autowired
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @Autowired
    private AuditEventConverter auditEventConverter;

    private CustomAuditEventRepository customAuditEventRepository;

    private PersistentAuditEvent testUserEvent;

    private PersistentAuditEvent testOtherUserEvent;

    private PersistentAuditEvent testOldUserEvent;

    @Before
    public void setup() {
        customAuditEventRepository = new CustomAuditEventRepository(persistenceAuditEventRepository, auditEventConverter);
        persistenceAuditEventRepository.deleteAll();

        testUserEvent = new PersistentAuditEvent();
        testUserEvent.setPrincipal("test-user");
        testUserEvent.setAuditEventType("test-type");
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        testUserEvent.setAuditEventDate(oneHourAgo);
        Map<String, String> data = new HashMap<>();
        data.put("test-key", "test-value");
        testUserEvent.setData(data);

        testOldUserEvent = new PersistentAuditEvent();
        testOldUserEvent.setPrincipal("test-user");
        testOldUserEvent.setAuditEventType("test-type");
        testOldUserEvent.setAuditEventDate(oneHourAgo.minusSeconds(10000));

        testOtherUserEvent = new PersistentAuditEvent();
        testOtherUserEvent.setPrincipal("other-test-user");
        testOtherUserEvent.setAuditEventType("test-type");
        testOtherUserEvent.setAuditEventDate(oneHourAgo);
    }

    @Test
    public void addAuditEvent() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        PersistentAuditEvent persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(persistentAuditEvent.getAuditEventType()).isEqualTo(event.getType());
        assertThat(persistentAuditEvent.getData()).containsKey("test-key");
        assertThat(persistentAuditEvent.getData()).containsEntry("test-key", "test-value");
        assertThat(persistentAuditEvent.getAuditEventDate()).isEqualTo(event.getTimestamp());
    }

    @Test
    public void findAuditEvent() {
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<AuditEvent> auditEvents = customAuditEventRepository.find(event.getPrincipal(),
                oneHourAgo, event.getType());

        AuditEvent auditEvent = auditEvents.get(0);
        assertThat(auditEvents.size()).isEqualTo(1);
        assertThat(auditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(auditEvent.getType()).isEqualTo(event.getType());
        assertThat(auditEvent.getData()).isEqualTo(event.getData());
    }

    @Test
    public void addAuditEventTruncateLargeData() {
        Map<String, Object> data = new HashMap<>();
        StringBuilder largeData = new StringBuilder();
        largeData.append("a".repeat(EVENT_DATA_COLUMN_MAX_LENGTH + 10));
        data.put("test-key", largeData);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        PersistentAuditEvent persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(persistentAuditEvent.getAuditEventType()).isEqualTo(event.getType());
        assertThat(persistentAuditEvent.getData()).containsKey("test-key");
        String actualData = persistentAuditEvent.getData().get("test-key");
        assertThat(actualData).hasSize(EVENT_DATA_COLUMN_MAX_LENGTH).isSubstringOf(largeData);
        assertThat(persistentAuditEvent.getAuditEventDate()).isEqualTo(event.getTimestamp());
    }

    @Test
    public void testAddEventWithWebAuthenticationDetails() {
        HttpSession session = new MockHttpSession(null, "test-session-id");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setRemoteAddr("1.2.3.4");
        WebAuthenticationDetails details = new WebAuthenticationDetails((HttpServletRequest) request);
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", details);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        PersistentAuditEvent persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getData()).containsEntry("remoteAddress", "1.2.3.4");
        assertThat(persistentAuditEvent.getData()).containsEntry("sessionId","test-session-id");
    }

    @Test
    public void testAddEventWithNullData() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", null);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        PersistentAuditEvent persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getData()).containsEntry("test-key","null");
    }

    @Test
    public void addAuditEventWithAnonymousUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent(Constants.ANONYMOUS_USER, "test-type", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).isEmpty();
    }

    @Test
    public void addAuditEventWithAuthorizationFailureType() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "AUTHORIZATION_FAILURE", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).isEmpty();
    }

    @Test
    public void convertToAuditEventFail() {
        AuditEventConverter auditEventConverter = new AuditEventConverter();
        PersistentAuditEvent persistentAuditEvent = null;
        AuditEvent auditEvents = auditEventConverter.convertToAuditEvent(persistentAuditEvent);
        assertThat(auditEvents).isNull();

        List<PersistentAuditEvent> persistentAuditEvents = null;
        List<AuditEvent> auditEvents2 = auditEventConverter.convertToAuditEvent(persistentAuditEvents);
        assertThat(auditEvents2).isEqualTo(Collections.emptyList());
    }


    @Test
    public void auditEventServiceFindAllTest() {
        AuditEventService auditEventService = new AuditEventService(persistenceAuditEventRepository, auditEventConverter);
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        Pageable pageable = PageRequest.of(0, 8);

        Page<AuditEvent> events = auditEventService.findAll(pageable);
        AuditEvent auditEvent = events.toList().get(0);
        assertThat(auditEvent.getType()).isEqualTo(event.getType());
        assertThat(auditEvent.getData()).isEqualTo(event.getData());
        assertThat(auditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(auditEvent.getTimestamp()).isEqualTo(event.getTimestamp());
    }

    @Test
    public void auditEventServiceFindByDatesTest() {
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        Instant oneHourAhead = Instant.now().plusSeconds(3600);
        AuditEventService auditEventService = new AuditEventService(persistenceAuditEventRepository, auditEventConverter);
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        Pageable pageable = PageRequest.of(0, 8);

        Page<AuditEvent> events = auditEventService.findByDates(oneHourAgo, oneHourAhead, pageable);
        AuditEvent auditEvent = events.toList().get(0);
        assertThat(auditEvent.getType()).isEqualTo(event.getType());
        assertThat(auditEvent.getData()).isEqualTo(event.getData());
        assertThat(auditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(auditEvent.getTimestamp()).isEqualTo(event.getTimestamp());
    }

    @Test
    public void auditEventServiceFindTest() {
        AuditEventService auditEventService = new AuditEventService(persistenceAuditEventRepository, auditEventConverter);
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<PersistentAuditEvent> persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal("test-user");
        PersistentAuditEvent persistentAuditEvent = persistentAuditEvents.get(0);

        Optional<AuditEvent> events = auditEventService.find(persistentAuditEvent.getId());
        AuditEvent auditEvent = events.get();
        assertThat(auditEvent.getType()).isEqualTo(event.getType());
        assertThat(auditEvent.getData()).isEqualTo(event.getData());
        assertThat(auditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(auditEvent.getTimestamp()).isEqualTo(event.getTimestamp());
    }
}
