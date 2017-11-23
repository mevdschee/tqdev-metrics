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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	 * @param reportUrl
	 *            the report URL
	 * @param instanceName
	 *            the instance name
	 * @param registry
	 *            the registry
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	public InfluxDbHttpReporter(String reportUrl, String instanceName, int intervalInSeconds, MetricRegistry registry) {
		super(instanceName, intervalInSeconds, registry);
		this.reportUrl = reportUrl;
	}

	/**
	 * Gets the valid URI.
	 *
	 * @param reportUrl
	 *            the report URL
	 * @return the valid URI
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 */
	private URI getValidUri(String reportUrl) throws URISyntaxException {
		URI url = new URI(reportUrl);
		String query = url.getQuery();

		Map<String, String> parameters = new HashMap<String, String>();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			// An '=' sign can be omitted in URL query params
			if (idx > 0) {
				String key = pair.substring(0, idx);
				String value = pair.substring(idx + 1);
				parameters.put(key, value);
			} else {
				parameters.put(pair, "");
			}
		}
		parameters.put("precision", "s");
		StringBuilder newQuery = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			String value = parameters.get(key);
			if (!first) {
				newQuery.append("&");
			}
			first = false;
			newQuery.append(key);
			if (!"".equals(value)) {
				newQuery.append('=').append(value);
			}
		}
		return new URI(url.getScheme(), url.getAuthority(), url.getPath(), newQuery.toString(), url.getFragment());

	}

	/**
	 * Report.
	 *
	 * @return true, if successful
	 */
	public boolean report() {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) getValidUri(reportUrl).toURL().openConnection();
			con.setRequestMethod("POST");
			con.setConnectTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(2)).intValue());
			con.setReadTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(2)).intValue());

			// Send post request
			con.setDoOutput(true);

			this.write(con.getOutputStream());
			return true;
		} catch (IOException e) {
			// TODO: log
			// on server disconnects, return false
		} catch (URISyntaxException e) {
			// TODO: log
			// on invalid URI
		} finally {
			// cleanup connection streams
			if (con != null) {
				try {
					con.getInputStream().close();
				} catch (IOException ignore) {
					// ignore when connection cannot be closed
				}
			}
		}
		return false;
	}

	/**
	 * Run.
	 *
	 * @param intervalInSeconds
	 *            the interval in seconds
	 */
	public void run() {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(() -> this.report(), 1, intervalInSeconds, TimeUnit.SECONDS);
	}

}