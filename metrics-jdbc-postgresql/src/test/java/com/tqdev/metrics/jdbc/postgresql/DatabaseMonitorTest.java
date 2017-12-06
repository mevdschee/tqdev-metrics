package com.tqdev.metrics.jdbc.postgresql;

import org.junit.Before;

import com.tqdev.metrics.core.MetricRegistry;

public class DatabaseMonitorTest {

	protected MetricRegistry registry;
	protected DatabaseMonitor databaseMonitor;

	// TODO: fix javadoc

	@Before
	public void setUp() {
		registry = new MetricRegistry();
		// databaseMonitor = new DatabaseMonitor(registry,);
	}

}