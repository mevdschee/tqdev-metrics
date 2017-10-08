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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The InfluxDbFileReporter class reports values in the metric registry to
 * InfluxDB readable files.
 */
public class InfluxDbFileReporter extends InfluxDbReporter {

	private final String metricPath;

	public InfluxDbFileReporter(String metricPath, String instanceName, MetricRegistry registry) {
		super(instanceName, registry);
		this.metricPath = metricPath;
	}

	/**
	 * Report.
	 *
	 * @return true, if successful
	 */
	public boolean report() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH");
		String filename = metricPath + "/" + formatter.format(new Date(System.currentTimeMillis())) + ".txt";
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(filename, true), 8192);
			this.write(out);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
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
		MetricRegistry registry = MetricRegistry.getInstance();
		InfluxDbFileReporter reporter = new InfluxDbFileReporter(metricPath, instanceName, registry);

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate((Runnable) () -> {
			reporter.report();
		}, 1, intervalInSeconds, TimeUnit.SECONDS);
	}

}