package com.tqdev.metrics.spring.loaders;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tqdev.metrics.core.MetricRegistry;
import com.tqdev.metrics.jmx.JmxReporter;

@Component
public class JmxReporterLoader {

	/**
	 * Start with the default domain (the package name of this class).
	 */
	@Autowired
	public JmxReporterLoader(MetricRegistry metricRegistry) {
		try {
			JmxReporter.start("com.tqdev.metrics", metricRegistry);
		} catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}