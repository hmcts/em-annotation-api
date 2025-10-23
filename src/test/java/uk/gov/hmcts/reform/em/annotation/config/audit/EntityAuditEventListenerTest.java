package uk.gov.hmcts.reform.em.annotation.config.audit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EntityAuditEventListenerTest {
    private EntityAuditEventListener entityAuditEventListener;

    @Mock
    private AsyncEntityAuditEventWriter asyncEntityAuditEventWriter;

    @Mock
    private BeanFactory beanFactory;

    private static final BeanFactory originalBeanFactory;
    private final String noBeanFound = "No bean found for AsyncEntityAuditEventWriter";
    private static final Field beanFactoryField;

    static {
        try {

            beanFactoryField = Class.forName(EntityAuditEventListener.class.getName()).getDeclaredField("beanFactory");
            // Allow modification on the field
            beanFactoryField.setAccessible(true);

            originalBeanFactory = (BeanFactory)beanFactoryField
                    .get(Class.forName(EntityAuditEventListener.class.getName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    void setUp() {
        entityAuditEventListener = new EntityAuditEventListener();
        EntityAuditEventListener.setBeanFactory(beanFactory);
        lenient().when(beanFactory.getBean(AsyncEntityAuditEventWriter.class)).thenReturn(asyncEntityAuditEventWriter);
    }

    @AfterAll
    static void tearDownAll() {
        EntityAuditEventListener.setBeanFactory(originalBeanFactory);
    }


    @Test
    void testSetBeanFactory() throws Exception {
        EntityAuditEventListener.setBeanFactory(beanFactory);

        var beanFactory1 = (BeanFactory)beanFactoryField.get(Class.forName(EntityAuditEventListener.class.getName()));

        assertEquals(beanFactory1, beanFactory);
    }

    @Test
    @DisplayName("Create calls writeAuditEvent")
    void testSuccessOnPostCreate() {
        doNothing()
            .when(asyncEntityAuditEventWriter)
            .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate(mock(Rectangle.class));
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
    }

    @Test
    @DisplayName("No bean logs exception for create")
    void testNoBeanOnPostCreate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate(mock(Rectangle.class));
        assertEquals(1, logsList.size());
        assertEquals(noBeanFound, logsList.get(0).getMessage());
    }

    @Test
    @DisplayName("Other exceptions are logged for create")
    void testExceptionOnPostCreate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate(mock(Rectangle.class));
        assertEquals(1, logsList.size());
        assertEquals("Exception while persisting create audit entity {}", logsList.get(0).getMessage());
    }

    @Test
    @DisplayName("Update calls writeAuditEvent")
    void testSuccessOnPostUpdate() {
        doNothing()
            .when(asyncEntityAuditEventWriter)
            .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate(mock(Rectangle.class));
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
    }

    @Test
    @DisplayName("No bean logs exception for update")
    void testNoBeanOnPostUpdate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate(mock(Rectangle.class));
        assertEquals(1, logsList.size());
        assertEquals(noBeanFound, logsList.get(0).getMessage());
    }

    @Test
    @DisplayName("Other exceptions are logged for update")
    void testExceptionOnPostUpdate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate(mock(Rectangle.class));
        assertEquals(1, logsList.size());
        assertEquals("Exception while persisting update audit entity {}", logsList.get(0).getMessage());
    }

    @Test
    @DisplayName("Remove calls writeAuditEvent")
    void testSuccessOnPostRemove() {
        doNothing()
            .when(asyncEntityAuditEventWriter)
            .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove(mock(Rectangle.class));
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
    }

    @Test
    @DisplayName("No bean logs exception for remove")
    void testNoBeanOnPostRemove() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove(mock(Rectangle.class));
        assertEquals(1, logsList.size());
        assertEquals(noBeanFound, logsList.get(0).getMessage());
    }

    @Test
    @DisplayName("Other exceptions are logged for remove")
    void testExceptionOnPostRemove() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove(mock(Rectangle.class));
        assertEquals(1, logsList.size());
        assertEquals("Exception while persisting delete audit entity {}", logsList.get(0).getMessage());
    }

    private List<ILoggingEvent> getiLoggingEvents() {
        Logger logger = (Logger) LoggerFactory.getLogger(EntityAuditEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        return listAppender.list;
    }
}

