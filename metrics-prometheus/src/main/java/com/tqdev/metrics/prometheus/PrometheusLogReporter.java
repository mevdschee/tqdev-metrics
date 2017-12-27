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
package com.tqdev.metrics.prometheus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The PrometheusFileReporter class reports values in the metric registry to
 * Prometheus readable files for use by the Prometheus node_reporter.
 */
public class PrometheusLogReporter extends PrometheusReporter {

	/** The metric path. */
	protected final String metricPath;

	/** The max file count. */
	protected final int maxFileCount;

	/**
	 * Instantiates a new Prometheus file reporter.
	 *
	 * @param registry
	 *            the registry
	 * @param instanceName
	 *            the instance name
	 * @param metricPath
	 *            the metric path
	 * @param maxFileCount
	 *            the max file count
	 */
	public PrometheusLogReporter(MetricRegistry registry, String instanceName, String metricPath, int maxFileCount) {
		super(registry, instanceName);
		this.metricPath = metricPath;
		this.maxFileCount = maxFileCount;
	}

	/**
	 * Instantiates a new Prometheus file reporter.
	 *
	 * @param registry
	 *            the registry
	 * @param instanceName
	 *            the instance name
	 * @param metricPath
	 *            the metric path
	 * @param maxFileCount
	 *            the max file count
	 * @param intervalInSeconds
	 *            the interval in seconds
	 */
	public PrometheusLogReporter(MetricRegistry registry, String instanceName, String metricPath, int maxFileCount,
			int intervalInSeconds) {
		super(registry, instanceName);
		this.metricPath = metricPath;
		this.maxFileCount = maxFileCount;
		run(intervalInSeconds);
	}

	/**
	 * Report.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean report() {
		if (!registry.isEnabled()) {
			return true;
		}
		File dir = new File(metricPath);
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				return false;
			}
		}
		long threadId = Thread.currentThread().getId();
		String temp = metricPath + "/" + registry.getMillis() + "." + threadId;
		String filename = metricPath + "/" + registry.getMillis() + ".prom";
		try {
			File tempFile = new File(temp);
			FileOutputStream fos = new FileOutputStream(tempFile);
			BufferedOutputStream out = new BufferedOutputStream(fos, 8192);
			write(out);
			out.close();
			tempFile.renameTo(new File(filename));
			remove(maxFileCount);
		} catch (IOException e) {
			// TODO: log
			return false;
		}
		return true;
	}

	/**
	 * Removes the.
	 *
	 * @param maxFileCount
	 *            the max file count
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void remove(int maxFileCount) throws IOException {
		File dir = new File(metricPath);
		FilenameFilter promFileFilter = (f, s) -> s.endsWith(".prom");
		File[] directoryListing = dir.listFiles(promFileFilter);
		if (directoryListing != null) {
			Arrays.sort(directoryListing);
			for (int i = 0; i < directoryListing.length - maxFileCount; i++) {
				directoryListing[i].delete();
			}
		} else {
			throw new IOException("Directory not listable: " + metricPath);
		}
	}

}