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
package com.tqdev.metrics.influxdb;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.FixedWindowRollingPolicy;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.SizeBasedTriggeringPolicy;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The InfluxDbFileReporter class reports values in the metric registry to
 * InfluxDB readable files.
 */
public class InfluxDbLogReporter extends InfluxDbReporter {

	private static Logger logger = null;

	public InfluxDbLogReporter(String instanceName, MetricRegistry registry) {
		super(instanceName, registry);
	}

	/**
	 * Report.
	 *
	 * @return true, if successful
	 */
	public boolean report() {
		this.write(logger);
		return true;
	}

	/**
	 * Start.
	 *
	 * @param metricPath
	 *            the metric path
	 * @param instanceName
	 *            the instance name
	 * @param intervalInSeconds
	 *            the interval in seconds
	 */
	public static void start(String metricPath, String instanceName, int intervalInSeconds) {
		if (logger == null) {
			initLogging(metricPath);
		}

		MetricRegistry registry = MetricRegistry.getInstance();
		InfluxDbLogReporter reporter = new InfluxDbLogReporter(instanceName, registry);

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate((Runnable) () -> {
			reporter.report();
		}, 1, intervalInSeconds, TimeUnit.SECONDS);
	}

	private static void initLogging(String metricPath) {
		logger = Logger.getLogger(InfluxDbLogReporter.class);
		RollingFileAppender fa = new RollingFileAppender();
		fa.setName("MetricLogger");
		fa.setFile(metricPath + "/metrics.log");
		FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
		rollingPolicy.setFileNamePattern(metricPath + "/metrics.log.%i.gz");
		rollingPolicy.setMaxIndex(10);
		fa.setRollingPolicy(rollingPolicy);
		SizeBasedTriggeringPolicy triggeringPolicy = new SizeBasedTriggeringPolicy();
		triggeringPolicy.setMaxFileSize(20000);
		fa.setTriggeringPolicy(triggeringPolicy);
		fa.setLayout(new PatternLayout("%m%n"));
		fa.setThreshold(Level.INFO);
		fa.setAppend(true);
		fa.activateOptions();
		logger.addAppender(fa);
	}

}