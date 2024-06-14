package uk.gov.hmcts.reform.em.annotation.config.audit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityAuditEventListenerTest {
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



    @Before
    public void setUp() {
        entityAuditEventListener = new EntityAuditEventListener();
        EntityAuditEventListener.setBeanFactory(beanFactory);
        when(beanFactory.getBean(AsyncEntityAuditEventWriter.class)).thenReturn(asyncEntityAuditEventWriter);
    }

    @AfterAll
    static void tearDownAll() {
        EntityAuditEventListener.setBeanFactory(originalBeanFactory);
    }


    @Test
    public void testSetBeanFactory() throws Exception {
        EntityAuditEventListener.setBeanFactory(beanFactory);

        var beanFactory1 = (BeanFactory)beanFactoryField.get(Class.forName(EntityAuditEventListener.class.getName()));

        Assert.assertEquals(beanFactory1, beanFactory);
    }

    @Test
    @DisplayName("Create calls writeAuditEvent")
    public void testSuccessOnPostCreate() {
        doNothing()
            .when(asyncEntityAuditEventWriter)
            .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate(mock(Rectangle.class));
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
    }

    @Test
    @DisplayName("No bean logs exception for create")
    public void testNoBeanOnPostCreate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate(mock(Rectangle.class));
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), noBeanFound);
    }

    @Test
    @DisplayName("Other exceptions are logged for create")
    public void testExceptionOnPostCreate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate(mock(Rectangle.class));
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), "Exception while persisting create audit entity {}");
    }

    @Test
    @DisplayName("Update calls writeAuditEvent")
    public void testSuccessOnPostUpdate() {
        doNothing()
            .when(asyncEntityAuditEventWriter)
            .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate(mock(Rectangle.class));
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
    }

    @Test
    @DisplayName("No bean logs exception for update")
    public void testNoBeanOnPostUpdate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate(mock(Rectangle.class));
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), noBeanFound);
    }

    @Test
    @DisplayName("Other exceptions are logged for update")
    public void testExceptionOnPostUpdate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate(mock(Rectangle.class));
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), "Exception while persisting update audit entity {}");
    }

    @Test
    @DisplayName("Remove calls writeAuditEvent")
    public void testSuccessOnPostRemove() {
        doNothing()
            .when(asyncEntityAuditEventWriter)
            .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove(mock(Rectangle.class));
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
    }

    @Test
    @DisplayName("No bean logs exception for remove")
    public void testNoBeanOnPostRemove() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove(mock(Rectangle.class));
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), noBeanFound);
    }

    @Test
    @DisplayName("Other exceptions are logged for remove")
    public void testExceptionOnPostRemove() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(any(Rectangle.class), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove(mock(Rectangle.class));
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), "Exception while persisting delete audit entity {}");
    }

    private List<ILoggingEvent> getiLoggingEvents() {
        Logger logger = (Logger) LoggerFactory.getLogger(EntityAuditEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        return listAppender.list;
    }
}

