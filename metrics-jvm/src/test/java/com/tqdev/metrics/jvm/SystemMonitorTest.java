package com.tqdev.metrics.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;

import org.junit.Before;
import org.junit.Test;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.UnixOperatingSystemMXBean;
import com.tqdev.metrics.core.MetricRegistry;

@SuppressWarnings("restriction")
public class SystemMonitorTest {

	protected MetricRegistry registry;
	protected SystemMonitor systemMonitor;

	// TODO: fix javadoc

	@Before
	public void setUp() {
		registry = new MetricRegistry();
		systemMonitor = new SystemMonitor(registry);
	}

	@Test
	public void shouldCreateGauges() {
		assertThat(registry.getKeys("jvm.Os.Cpu")).contains("load");
		try {
			@SuppressWarnings("unused")
			OperatingSystemMXBean sun = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			assertThat(registry.getKeys("jvm.Os.Cpu")).contains("process");
			assertThat(registry.getKeys("jvm.Os.Cpu")).contains("system");
			assertThat(registry.getKeys("jvm.Os.Memory")).contains("free");
			assertThat(registry.getKeys("jvm.Os.Memory")).contains("total");
			assertThat(registry.getKeys("jvm.Os.Swap")).contains("free");
			assertThat(registry.getKeys("jvm.Os.Swap")).contains("total");
		} catch (Exception e) {
			// ignore
		}
		try {
			@SuppressWarnings("unused")
			UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			assertThat(registry.getKeys("jvm.Os.FileDescriptors")).contains("open");
			assertThat(registry.getKeys("jvm.Os.FileDescriptors")).contains("max");
		} catch (Exception e) {
			// ignore
		}
	}

	@Test
	public void shouldMeasureGauges() {
		assertThat(registry.get("jvm.Os.Cpu", "process")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jvm.Os.Cpu", "system")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jvm.Os.Memory", "free")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jvm.Os.Memory", "total")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jvm.Os.Swap", "free")).isGreaterThanOrEqualTo(0);
		assertThat(registry.get("jvm.Os.Swap", "total")).isGreaterThanOrEqualTo(0);
	}

}