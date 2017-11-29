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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The InfluxDbHttpReporter class reports values in the metric registry to
 * InfluxDB.
 */
public class InfluxDbHttpReporter extends InfluxDbReporter {

	/** The report URL. */
	protected final String reportUrl;

	/**
	 * Instantiates a new InfluxDB HTTP reporter.
	 *
	 * @param registry
	 *            the registry
	 * @param instanceName
	 *            the instance name
	 * @param reportUrl
	 *            the report URL
	 */
	public InfluxDbHttpReporter(MetricRegistry registry, String instanceName, String reportUrl) {
		super(registry, instanceName);
		this.reportUrl = reportUrl;
	}

	/**
	 * Instantiates a new InfluxDB HTTP reporter.
	 *
	 * @param registry
	 *            the registry
	 * @param instanceName
	 *            the instance name
	 * @param reportUrl
	 *            the report URL
	 * @param intervalInSeconds
	 *            the interval in seconds
	 */
	public InfluxDbHttpReporter(MetricRegistry registry, String instanceName, String reportUrl, int intervalInSeconds) {
		super(registry, instanceName);
		this.reportUrl = reportUrl;
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
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(reportUrl).openConnection();
			con.setRequestMethod("POST");
			con.setConnectTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(2)).intValue());
			con.setReadTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(2)).intValue());

			// Send post request
			con.setDoOutput(true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(baos);
			this.write(gzos);
			byte[] bytes = baos.toByteArray();
			con.setRequestProperty("Content-Encoding", "gzip");
			con.setRequestProperty("Content-Length", Long.toString(bytes.length));
			con.getOutputStream().write(bytes);

			if (con.getResponseCode() == 200) {
				return true;
			}
		} catch (IOException e) {
			// TODO: log
			// on server disconnects, return false
		} finally {
			// cleanup connection streams
			if (con != null) {
				try {
					con.getInputStream().close();
					con.getOutputStream().close();
				} catch (IOException ignore) {
					// ignore when connection cannot be closed
				}
			}
		}
		return false;
	}

}