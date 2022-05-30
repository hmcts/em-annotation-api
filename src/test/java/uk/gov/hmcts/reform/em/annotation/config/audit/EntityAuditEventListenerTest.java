package uk.gov.hmcts.reform.em.annotation.config.audit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.microsoft.applicationinsights.boot.dependencies.apachecommons.lang3.reflect.FieldUtils;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityAuditEventListenerTest {
    private EntityAuditEventListener entityAuditEventListener;

    @Mock
    private AsyncEntityAuditEventWriter asyncEntityAuditEventWriter;

    @Mock
    private BeanFactory beanFactory;

    private static final BeanFactory originalBeanFactory;

    static {
        try {
            originalBeanFactory = (BeanFactory) FieldUtils.readDeclaredStaticField(
                    EntityAuditEventListener.class, "beanFactory", true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final String noBeanFound;

    {
        try {
            noBeanFound = (String) FieldUtils.readDeclaredStaticField(
                    EntityAuditEventListener.class, "NO_BEAN_FOUND", true);
        } catch (IllegalAccessException e) {
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
    static void tearDown() {
        EntityAuditEventListener.setBeanFactory(originalBeanFactory);
    }


    @Test
    public void testSetBeanFactory() throws IllegalAccessException {
        EntityAuditEventListener.setBeanFactory(beanFactory);
        BeanFactory beanFactory1 = (BeanFactory) FieldUtils.readDeclaredStaticField(
                EntityAuditEventListener.class, "beanFactory", true);
        Assert.assertEquals(beanFactory1, beanFactory);
    }


    @Test
    @DisplayName("Create calls writeAuditEvent")
    public void testSuccessOnPostCreate() {
        doNothing().when(asyncEntityAuditEventWriter).writeAuditEvent(anyString(), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate("Create");
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(anyString(), eq(EntityAuditAction.CREATE));
    }

    @Test
    @DisplayName("No bean logs exception for create")
    public void testNoBeanOnPostCreate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(anyString(), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate("Create");
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), noBeanFound);
    }

    @Test
    @DisplayName("Other exceptions are logged for create")
    public void testExceptionOnPostCreate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(anyString(), eq(EntityAuditAction.CREATE));
        entityAuditEventListener.onPostCreate("Create");
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), "Exception while persisting create audit entity {}");
    }

    @Test
    @DisplayName("Update calls writeAuditEvent")
    public void testSuccessOnPostUpdate() {
        doNothing().when(asyncEntityAuditEventWriter).writeAuditEvent(anyString(), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate("Update");
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(anyString(), eq(EntityAuditAction.UPDATE));
    }

    @Test
    @DisplayName("No bean logs exception for update")
    public void testNoBeanOnPostUpdate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(anyString(), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate("Update");
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), noBeanFound);
    }

    @Test
    @DisplayName("Other exceptions are logged for update")
    public void testExceptionOnPostUpdate() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(anyString(), eq(EntityAuditAction.UPDATE));
        entityAuditEventListener.onPostUpdate("Update");
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), "Exception while persisting update audit entity {}");
    }

    @Test
    @DisplayName("Remove calls writeAuditEvent")
    public void testSuccessOnPostRemove() {
        doNothing().when(asyncEntityAuditEventWriter).writeAuditEvent(anyString(), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove("Remove");
        verify(asyncEntityAuditEventWriter, times(1))
                .writeAuditEvent(anyString(), eq(EntityAuditAction.DELETE));
    }

    @Test
    @DisplayName("No bean logs exception for remove")
    public void testNoBeanOnPostRemove() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new NoSuchBeanDefinitionException("AsyncEntityAuditEventWriter"))
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(anyString(), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove("Remove");
        Assert.assertEquals(logsList.size(), 1);
        Assert.assertEquals(logsList.get(0).getMessage(), noBeanFound);
    }

    @Test
    @DisplayName("Other exceptions are logged for remove")
    public void testExceptionOnPostRemove() {
        List<ILoggingEvent> logsList = getiLoggingEvents();
        doThrow(new IllegalArgumentException())
                .when(asyncEntityAuditEventWriter)
                .writeAuditEvent(anyString(), eq(EntityAuditAction.DELETE));
        entityAuditEventListener.onPostRemove("Remove");
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

