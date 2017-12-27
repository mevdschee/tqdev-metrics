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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tqdev.metrics.core.MetricRegistry;

abstract class PrometheusReporter {

	/**
	 * The instanceName used to identify the source of the metrics in Prometheus.
	 */
	protected final String instanceName;

	/**
	 * The registry in which the metrics, that this JMXReporter reports, are stored.
	 */
	protected final MetricRegistry registry;

	/**
	 * Instantiates a new JMX reporter.
	 *
	 * @param registry
	 *            the registry in which the metrics, that this JMXReporter reports,
	 *            are stored
	 * @param instanceName
	 *            the name of the JVM instance or machine that generates the metrics
	 */
	public PrometheusReporter(MetricRegistry registry, String instanceName) {
		this.instanceName = instanceName;
		this.registry = registry;
	}

	// metric_name [
	// "{" label_name "=" `"` label_value `"` { "," label_name "=" `"` label_value
	// `"` } [ "," ] "}"
	// ] value [ timestamp ]

	// http_requests_total{method="post",code="200"} 1027 1395066363000

	// metric_name = [a-zA-Z_:][a-zA-Z0-9_:]*
	// label_name = [a-zA-Z_][a-zA-Z0-9_]*
	// NB: Label names beginning with __ are reserved for internal use
	// label_value = any sequence of UTF-8 characters, but the backslash, the
	// double-quote, and the line-feed characters have to be escaped as \\, \", and
	// \n, respectively.
	// value = value is a float: Nan, +Inf, and -Inf are valid values.
	// timestamp = milliseconds since epoch (int64)

	/**
	 * Write.
	 *
	 * @param out
	 *            the out
	 */
	public void write(OutputStream out) {
		PrintWriter w = new PrintWriter(out);
		String instanceStr = instanceName.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"");
		String time = String.valueOf((registry.getMillis() / 1000) * 1000);
		for (String type : registry.getTypes()) {
			String typeStr = type.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"");
			for (String key : registry.getKeys(type)) {
				String keyStr = key.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"");
				String parts[] = typeStr.split("\\.", 3);
				w.print(parts[0]);
				w.print("{host=\"");
				w.print(instanceStr);
				w.print("\",instance=\"");
				if (parts.length > 1) {
					w.print(parts[1]);
				}
				w.print("\",type=\"");
				if (parts.length > 2) {
					w.append(parts[2]);
				}
				w.print("\",type_instance=\"");
				w.print(keyStr);
				w.print("\"} ");
				w.print(registry.get(type, key));
				w.println(" " + time);
			}
		}
		w.close();
	}

	public abstract boolean report();

	/**
	 * Run.
	 *
	 * @param intervalInSeconds
	 *            the interval in seconds
	 */
	public void run(int intervalInSeconds) {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(() -> this.report(), 1, intervalInSeconds, TimeUnit.SECONDS);
	}
}
