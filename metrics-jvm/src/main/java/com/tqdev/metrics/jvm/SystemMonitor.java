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
package com.tqdev.metrics.jvm;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

import com.tqdev.metrics.core.Gauge;
import com.tqdev.metrics.core.MetricRegistry;

public class SystemMonitor {

	final MetricRegistry registry;

	public SystemMonitor(MetricRegistry registry) {
		this.registry = registry;
		registerOsStatistics();
		registerRuntimeMemory();
		registerGarbageCollectionStatistics();
		registerMemoryStatistics();
		registerDiskStatistics();
		registerThreadStatistics();
	}

	@SuppressWarnings("restriction")
	private void registerOsStatistics() {
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		registry.set("jvm.Os.Cpu", "load", (Gauge) () -> Math.round(os.getSystemLoadAverage() * 100));
		try {
			com.sun.management.OperatingSystemMXBean sun;
			sun = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			registry.set("jvm.Os.Cpu", "process", (Gauge) () -> Math.round(sun.getProcessCpuLoad() * 100));
			registry.set("jvm.Os.Cpu", "system", (Gauge) () -> Math.round(sun.getSystemCpuLoad() * 100));
			registry.set("jvm.Os.Memory", "free", (Gauge) () -> sun.getFreePhysicalMemorySize());
			registry.set("jvm.Os.Memory", "total", (Gauge) () -> sun.getTotalPhysicalMemorySize());
			registry.set("jvm.Os.Swap", "free", (Gauge) () -> sun.getFreeSwapSpaceSize());
			registry.set("jvm.Os.Swap", "total", (Gauge) () -> sun.getTotalSwapSpaceSize());
		} catch (Exception e) {
			// ignore
		}
		try {
			com.sun.management.UnixOperatingSystemMXBean unix;
			unix = (com.sun.management.UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			registry.set("jvm.Os.FileDescriptors", "open", (Gauge) () -> unix.getOpenFileDescriptorCount());
			registry.set("jvm.Os.FileDescriptors", "max", (Gauge) () -> unix.getMaxFileDescriptorCount());
		} catch (Exception e) {
			// ignore
		}
	}

	private void registerRuntimeMemory() {
		registry.set("jvm.Memory.Runtime", "free", (Gauge) () -> Runtime.getRuntime().freeMemory());
		registry.set("jvm.Memory.Runtime", "total", (Gauge) () -> Runtime.getRuntime().totalMemory());
		registry.set("jvm.Memory.Runtime", "max", (Gauge) () -> {
			long max = Runtime.getRuntime().maxMemory();
			return (max == Long.MAX_VALUE) ? -1 : max;
		});
	}

	private void registerGarbageCollectionStatistics() {
		registry.set("jvm.Memory.GarbageCollection", "count", (Gauge) () -> {
			long total = 0;
			for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
				long count = gc.getCollectionCount();
				if (count >= 0) {
					total += count;
				}
			}
			return total;
		});
		registry.set("jvm.Memory.GarbageCollection", "time", (Gauge) () -> {
			long total = 0;
			for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
				long count = gc.getCollectionTime();
				if (count >= 0) {
					total += count;
				}
			}
			return total;
		});
		registry.set("jvm.Memory.GarbageCollection", "uptime", (Gauge) () -> {
			return ManagementFactory.getRuntimeMXBean().getUptime();
		});
	}

	private void registerMemoryStatistics() {
		final MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		registry.set("jvm.Memory.Heap", "used", (Gauge) () -> mem.getHeapMemoryUsage().getUsed());
		registry.set("jvm.Memory.Heap", "init", (Gauge) () -> mem.getHeapMemoryUsage().getInit());
		registry.set("jvm.Memory.Heap", "max", (Gauge) () -> mem.getHeapMemoryUsage().getMax());
		registry.set("jvm.Memory.Heap", "comitted", (Gauge) () -> mem.getHeapMemoryUsage().getCommitted());
		registry.set("jvm.Memory.NonHeap", "used", (Gauge) () -> mem.getNonHeapMemoryUsage().getUsed());
		registry.set("jvm.Memory.NonHeap", "init", (Gauge) () -> mem.getNonHeapMemoryUsage().getInit());
		registry.set("jvm.Memory.NonHeap", "max", (Gauge) () -> mem.getNonHeapMemoryUsage().getMax());
		registry.set("jvm.Memory.NonHeap", "comitted", (Gauge) () -> mem.getNonHeapMemoryUsage().getCommitted());
	}

	private void registerDiskStatistics() {
		for (File root : File.listRoots()) {
			long size;
			try {
				size = root.getTotalSpace();
			} catch (Exception e) {
				size = -1;
			}
			if (size > 0) {
				registry.set("jvm.Disk.Free", root.getAbsolutePath(), (Gauge) () -> {
					try {
						return root.getFreeSpace();
					} catch (Exception e) {
						return -1;
					}
				});
				registry.set("jvm.Disk.Total", root.getAbsolutePath(), (Gauge) () -> {
					try {
						return root.getTotalSpace();
					} catch (Exception e) {
						return -1;
					}
				});
			}
		}
	}

	private void registerThreadStatistics() {
		final ThreadMXBean thread = ManagementFactory.getThreadMXBean();
		registry.set("jvm.Thread.Count", "total", (Gauge) () -> thread.getThreadCount());
		registry.set("jvm.Thread.Count", "daemon", (Gauge) () -> thread.getDaemonThreadCount());
	}

	public Map<String, String> getSystemInformation() {
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		HashMap<String, String> map = new HashMap<>();
		map.put("cpu_cores", String.valueOf(Runtime.getRuntime().availableProcessors()));
		map.put("cpu_architecture", os.getArch());
		map.put("os_name", os.getName());
		map.put("os_version", os.getVersion());
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		map.put("rt_name", runtime.getName());
		map.put("vm_name", runtime.getVmName());
		map.put("vm_vendor", runtime.getVmVendor());
		map.put("vm_version", runtime.getVmVersion());
		return map;
	}

}
