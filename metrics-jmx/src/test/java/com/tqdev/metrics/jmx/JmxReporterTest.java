/* Copyright (C) 2017 Maurits van der Schee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tqdev.metrics.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class JmxReporterTest tests the JmxReporter.
 */
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

	/**
	 * Read from JMX.
	 *
	 * @param type
	 *            the type
	 * @param key
	 *            the key
	 * @return the long
	 * @throws MBeanException
	 *             the MBean exception
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws ReflectionException
	 *             the reflection exception
	 */
	private long readJmx(String type, String key)
			throws MBeanException, AttributeNotFoundException, ReflectionException {
		final CompositeDataSupport composite = (CompositeDataSupport) reporter.getAttribute(type);
		return (long) composite.get(key);
	}

	/**
	 * Write to JMX.
	 *
	 * @param type
	 *            the type
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws MBeanException
	 *             the MBean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws InvalidAttributeValueException
	 *             the invalid attribute value exception
	 * @throws OpenDataException
	 *             the open data exception
	 */
	private void writeJmx(String type, String key, long value) throws AttributeNotFoundException, MBeanException,
			ReflectionException, InvalidAttributeValueException, OpenDataException {
		final CompositeDataSupport composite = (CompositeDataSupport) reporter.getAttribute(type);
		final CompositeType compositeType = composite.getCompositeType();
		final String[] keys = composite.getCompositeType().keySet().toArray(new String[] {});
		final HashMap<String, Object> map = new HashMap<>();
		for (String k : keys) {
			if (k == key) {
				map.put(k, value);
			} else {
				map.put(k, composite.get(k));
			}
		}
		reporter.setAttribute(new Attribute(type, new CompositeDataSupport(compositeType, map)));
	}

	/**
	 * Should throw exception on unknown type.
	 *
	 * @throws MBeanException
	 *             the MBean exception
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws ReflectionException
	 *             the reflection exception
	 */
	@Test
	public void shouldThrowExceptionOnUnknownType()
			throws MBeanException, AttributeNotFoundException, ReflectionException {
		try {
			readJmx("jdbc.Statement.Invocations", "select");
			Assert.fail("readJmx should have thrown an AttributeNotFoundException");
		} catch (Exception e) {
			assertThat(e.getClass().getSimpleName()).isEqualTo("AttributeNotFoundException");
		}
	}

	/**
	 * Should throw exception on unknown key.
	 *
	 * @throws MBeanException
	 *             the MBean exception
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws ReflectionException
	 *             the reflection exception
	 */
	@Test
	public void shouldThrowExceptionOnUnknownKey()
			throws MBeanException, AttributeNotFoundException, ReflectionException {
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
	 *
	 * @throws MBeanException
	 *             the MBean exception
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws ReflectionException
	 *             the reflection exception
	 */
	@Test
	public void shouldThrowExceptionWhenWritingToKey()
			throws MBeanException, AttributeNotFoundException, ReflectionException {
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
	 *
	 * @throws MBeanException
	 *             the MBean exception
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws ReflectionException
	 *             the reflection exception
	 */
	@Test
	public void shouldReportWrittenValues() throws MBeanException, AttributeNotFoundException, ReflectionException {
		registry.increment("jdbc.Statement.Invocations", "select");
		registry.add("jdbc.Statement.Durations", "select", 123456789);
		assertThat(readJmx("jdbc.Statement.Invocations", "select")).isEqualTo(1);
		assertThat(readJmx("jdbc.Statement.Durations", "select")).isEqualTo(123456789);
	}

	/**
	 * Should report global information.
	 */
	@Test
	public void shouldReportGlobalInformation() {
		MBeanInfo info = reporter.getMBeanInfo();
		assertThat(info.getClassName()).isEqualTo("com.tqdev.metrics.jmx.JmxReporter");
		assertThat(info.getDescription()).isEqualTo("");
	}

	/**
	 * Should report information on reset operation.
	 */
	@Test
	public void shouldReportInformationOnResetOperation() {
		MBeanInfo info = reporter.getMBeanInfo();
		assertThat(info.getOperations().length).isEqualTo(1);
		assertThat(info.getOperations()[0].getName()).isEqualTo("resetCounters");
		assertThat(info.getOperations()[0].getReturnType()).isEqualTo("java.lang.Void");
		assertThat(info.getOperations()[0].getSignature().length).isEqualTo(0);
	}

	/**
	 * Should reset when invoking reset operation.
	 *
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws MBeanException
	 *             the MBean exception
	 */
	@Test
	public void shouldResetWhenInvokingResetOperation() throws ReflectionException, MBeanException {
		registry.increment("jdbc.Statement.Invocations", "select");
		assertThat(registry.has("jdbc.Statement.Invocations", "select")).isTrue();
		reporter.invoke("resetCounters", new Object[] {}, new String[] {});
		assertThat(registry.has("jdbc.Statement.Invocations", "select")).isFalse();
	}

	/**
	 * Should register in MBean server.
	 *
	 * @throws MalformedObjectNameException
	 *             the malformed object name exception
	 * @throws InstanceAlreadyExistsException
	 *             the instance already exists exception
	 * @throws NotCompliantMBeanException
	 *             the not compliant MBean exception
	 * @throws MBeanRegistrationException
	 *             the MBean registration exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 * @throws ReflectionException
	 *             the reflection exception
	 */
	@Test
	public void shouldRegisterInMBeanServer()
			throws MalformedObjectNameException, InstanceAlreadyExistsException, NotCompliantMBeanException,
			MBeanRegistrationException, InstanceNotFoundException, IntrospectionException, ReflectionException {
		reporter.register("com.tqdev.metrics", "TQdev.com's Metrics");
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("com.tqdev.metrics:type=Metrics");
		assertThat(mbs.isRegistered(name)).isTrue();
		assertThat(mbs.getMBeanInfo(name).getDescription()).isEqualTo("TQdev.com's Metrics");
		mbs.unregisterMBean(name);
		assertThat(mbs.isRegistered(name)).isFalse();
	}

}