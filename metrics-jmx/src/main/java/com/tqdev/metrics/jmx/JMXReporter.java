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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.SimpleType;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * OpenMBean for accessing the
 */
public class JMXReporter implements DynamicMBean {

	private final String type;
	private final MetricRegistry storage;

	public JMXReporter(String type, MetricRegistry registry) {
		this.type = type;
		storage = registry;
	}

	@Override
	public Object getAttribute(String attribute_name)
			throws AttributeNotFoundException, MBeanException, ReflectionException {

		if (attribute_name == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"),
					"Cannot call getAttributeInfo with null attribute name");
		}
		if (storage.has(type, attribute_name)) {
			return storage.get(type, attribute_name);
		}
		throw new AttributeNotFoundException("Cannot find attribute: " + type + "." + attribute_name);
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

		throw new AttributeNotFoundException("No attribute can be set in this MBean");
	}

	@Override
	public AttributeList getAttributes(String[] attributeNames) {

		if (attributeNames == null) {
			throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"),
					"Cannot call getAttributes with null attribute names");
		}
		AttributeList resultList = new AttributeList();

		if (attributeNames.length == 0) {
			return resultList;
		}

		for (String attributeName : attributeNames) {
			try {
				Object value = getAttribute(attributeName);
				resultList.add(new Attribute(attributeName, value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (resultList);
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return new AttributeList();
	}

	@Override
	public Object invoke(String operationName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {

		throw new RuntimeOperationsException(new IllegalArgumentException("No operations defined for this OpenMBean"),
				"No operations defined for this OpenMBean");
	}

	@Override
	public MBeanInfo getMBeanInfo() {

		ArrayList<OpenMBeanAttributeInfoSupport> attributes = new ArrayList<>();

		for (String key : storage.getKeys(type)) {
			attributes.add(
					new OpenMBeanAttributeInfoSupport(key, type + " of " + key, SimpleType.LONG, true, false, false));
		}

		OpenMBeanInfoSupport PSOMBInfo = new OpenMBeanInfoSupport(this.getClass().getName(), type,
				attributes.toArray(new OpenMBeanAttributeInfoSupport[] {}), new OpenMBeanConstructorInfoSupport[0],
				new OpenMBeanOperationInfoSupport[0], new MBeanNotificationInfo[0]);

		return PSOMBInfo;
	}
	
	public static void start() {
		start(JMXReporter.class.getPackage().getName());
	}
	
	public static void start(String domain) {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		MetricRegistry registry = MetricRegistry.getInstance();

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate((Runnable) () -> {
			try {
				for (String type : registry.getTypes()) {
					String parts[] = type.split("\\.", 2);
					ObjectName name;
					if (parts.length < 2) {
						name = new ObjectName(domain + ":type=" + type);
					} else {
						name = new ObjectName(domain + "." + parts[0] + ":type=" + parts[1]);
					}
					if (!mbs.isRegistered(name)) {
						mbs.registerMBean(new JMXReporter(type, registry), name);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, 1, 5, TimeUnit.SECONDS);
	}
}