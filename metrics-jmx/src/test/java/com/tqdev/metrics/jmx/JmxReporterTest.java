package com.tqdev.metrics.jmx;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.tqdev.metrics.core.MetricRegistry;

public class JmxReporterTest {

    /** The registry. */
    protected final MetricRegistry registry = MetricRegistry.getInstance();

    /** The reporter. */
    private final JmxReporter reporter = new JmxReporter(registry);

    /**
     * Initialize.
     */
    @Before
    public void initialize() {
        registry.reset();
    }

    private long readJmx(String type, String key) throws MBeanException, AttributeNotFoundException, ReflectionException {
        final CompositeDataSupport composite = (CompositeDataSupport) reporter.getAttribute(type);
        return (long) composite.get(key);
    }

    private void writeJmx(String type, String key, long value) throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException, OpenDataException {
        final CompositeDataSupport composite = (CompositeDataSupport) reporter.getAttribute(type);
        final CompositeType compositeType = composite.getCompositeType();
        final String[] keys = composite.getCompositeType().keySet().toArray(new String[] {});
        final HashMap<String,Object> map = new HashMap<>();
        for (String k : keys) {
            if (k==key) {
                //TODO: continue
                //map.
            }
        }
        final Object[] values = composite.getAll(keys);
        reporter.setAttribute(new Attribute(type,new CompositeDataSupport(compositeType, keys,values)));
    }

    /**
     * Should throw exception on unknown type.
     */
    @Test
    public void shouldThrowExceptionOnUnknownType() throws MBeanException, AttributeNotFoundException, ReflectionException {
        try {
            readJmx("jdbc.Statement.Invocations", "select");
            Assert.fail("readJmx should have thrown an AttributeNotFoundException");
        } catch (Exception e) {
            assertThat(e.getClass().getSimpleName()).isEqualTo("AttributeNotFoundException");
        }
    }

    /**
     * Should throw exception on unknown key.
     */
    @Test
    public void shouldThrowExceptionOnUnknownKey() throws MBeanException, AttributeNotFoundException, ReflectionException {
        registry.increment("jdbc.Statement.Invocations", "update");
        try {
            readJmx("jdbc.Statement.Invocations", "select");
            Assert.fail("readJmx should have thrown an InvalidKeyException");
        } catch (Exception e) {
            assertThat(e.getClass().getSimpleName()).isEqualTo("InvalidKeyException");
        }
    }

    /**
     * Should throw exception when writing to key.
     */
    @Test
    public void shouldThrowExceptionWhenWritingToKey() throws MBeanException, AttributeNotFoundException, ReflectionException {
        registry.increment("jdbc.Statement.Invocations", "select");
        try {
            writeJmx("jdbc.Statement.Invocations", "select", 2);
            Assert.fail("writeJmx should have thrown an AttributeNotFoundException");
        } catch (Exception e) {
            assertThat(e.getClass().getSimpleName()).isEqualTo("AttributeNotFoundException");
        }
    }

    /**
     * Should report written values.
     */
    @Test
    public void shouldReportWrittenValues() throws MBeanException, AttributeNotFoundException, ReflectionException {
        registry.increment("jdbc.Statement.Invocations", "select");
        registry.add("jdbc.Statement.Durations", "select",123456789);
        assertThat(readJmx("jdbc.Statement.Invocations", "select")).isEqualTo(1);
        assertThat(readJmx("jdbc.Statement.Durations", "select")).isEqualTo(123456789);
    }

    /**
     * Should report global information
     */
    @Test
    public void shouldReportGlobalInformation() {
        MBeanInfo info = reporter.getMBeanInfo();
        assertThat(info.getClassName()).isEqualTo("com.tqdev.metrics.jmx.JmxReporter");
        assertThat(info.getDescription()).isEqualTo("TQdev.com's Metrics");
    }

    /**
     * Should report information on reset operation
     */
    @Test
    public void shouldReportInformationOnResetOperation() {
        MBeanInfo info = reporter.getMBeanInfo();
        assertThat(info.getOperations().length).isEqualTo(1);
        assertThat(info.getOperations()[0].getName()).isEqualTo("reset");
        assertThat(info.getOperations()[0].getReturnType()).isEqualTo("java.lang.Void");
        assertThat(info.getOperations()[0].getSignature().length).isEqualTo(0);
    }

    /**
     * Should reset when invoking reset operation
     */
    @Test
    public void shouldResetWhenInvokingResetOperation() throws ReflectionException, MBeanException {
        registry.increment("jdbc.Statement.Invocations", "select");
        assertThat(registry.has("jdbc.Statement.Invocations", "select")).isTrue();
        reporter.invoke("reset", new Object[]{}, new String[] {});
        assertThat(registry.has("jdbc.Statement.Invocations", "select")).isFalse();
    }

}