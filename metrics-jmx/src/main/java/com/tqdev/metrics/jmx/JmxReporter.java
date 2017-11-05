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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * OpenMBean for accessing (a part of) the metric registry via JMX
 */
public class JmxReporter implements DynamicMBean {

	/**
	 * The registry in which the metrics, that this JMXReporter reports, are
	 * stored.
	 */
	private final MetricRegistry registry;

	/**
	 * Instantiates a new JMX reporter.
	 *
	 * @param type
	 *            the type of the metrics in the registry that this JMXReporter
	 *            reports
	 * @param registry
	 *            the registry in which the metrics, that this JMXReporter
	 *            reports, are stored
	 */
	public JmxReporter(MetricRegistry registry) {
		this.registry = registry;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String type) throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (registry.hasType(type)) {
			LinkedHashMap<String, Long> items = new LinkedHashMap<>();
			for (String key : registry.getKeys(type)) {
				items.put(key, registry.get(type, key));
			}
			LinkedHashMap<String, Long> sortedItems = sortByComparator(items);
			CompositeDataSupport result = null;
			try {
				result = new CompositeDataSupport(getCompositeType(type), sortedItems);
			} catch (OpenDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
		throw new AttributeNotFoundException("Cannot find attribute: " + type);
	}

	private static LinkedHashMap<String, Long> sortByComparator(LinkedHashMap<String, Long> unsortMap) {

		LinkedList<Entry<String, Long>> list = new LinkedList<>(unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		// Maintaining insertion order with the help of LinkedList
		LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<>();
		for (Entry<String, Long> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

		throw new AttributeNotFoundException("No attribute can be set in this MBean");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
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

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.
	 * AttributeList)
	 */
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return new AttributeList();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.management.DynamicMBean#invoke(java.lang.String,
	 * java.lang.Object[], java.lang.String[])
	 */
	@Override
	public Object invoke(String operationName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {

		throw new RuntimeOperationsException(new IllegalArgumentException("No operations defined for this OpenMBean"),
				"No operations defined for this OpenMBean");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {

		ArrayList<OpenMBeanAttributeInfoSupport> attributes = new ArrayList<>();

		for (String type : registry.getTypes()) {
			attributes.add(new OpenMBeanAttributeInfoSupport(type, type, getCompositeType(type), true, false, false));
		}

		OpenMBeanInfoSupport PSOMBInfo = new OpenMBeanInfoSupport(this.getClass().getName(), "TQdev.com's Metrics",
				attributes.toArray(new OpenMBeanAttributeInfoSupport[] {}), new OpenMBeanConstructorInfoSupport[0],
				new OpenMBeanOperationInfoSupport[0], new MBeanNotificationInfo[0]);

		return PSOMBInfo;
	}

	@SuppressWarnings("rawtypes")
	private CompositeType getCompositeType(String type) {
		ArrayList<String> nameList = new ArrayList<>();
		ArrayList<OpenType> typeList = new ArrayList<>();

		for (String key : registry.getKeys(type)) {
			nameList.add(key);
			typeList.add(SimpleType.LONG);
		}

		String[] nameArray = nameList.toArray(new String[nameList.size()]);
		OpenType[] typeArray = typeList.toArray(new OpenType[typeList.size()]);

		CompositeType compositeType = null;
		try {
			compositeType = new CompositeType(type, type, nameArray, nameArray, typeArray);
		} catch (OpenDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return compositeType;
	}

	public static void start(String domain, MetricRegistry metricRegistry) throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName(domain + ":type=Metrics");
		if (!mbs.isRegistered(name)) {
			mbs.registerMBean(new JmxReporter(metricRegistry), name);
		}
	}
}