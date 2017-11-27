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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.junit.Before;
import org.junit.Test;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class InfluxDbHttpReporterTest.
 */
public class InfluxDbHttpReporterTest {

	/** The registry. */
	protected MetricRegistry registry;

	/**
	 * Initialize.
	 */
	@Before
	public void setUp() {
		when(registry.getMillis()).thenReturn(1510373758123L);
		registry = spy(new MetricRegistry());
	}

	/**
	 * Gets the HTTP header reader.
	 *
	 * @param inputStream
	 *            the input stream
	 * @return the HTTP header reader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedReader getHttpHeaderReader(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[8192];
		byte[] separator = { 13, 10, 13, 10 };
		int offset = 0;
		while (inputStream.read(buffer, offset, 1) != -1) {
			offset++;
			int matches = 0;
			for (int i = 0; i < separator.length; i++) {
				int index = offset - separator.length + i;
				if (index > 0 && buffer[index] == separator[i]) {
					matches++;
				}
			}
			if (matches == separator.length) {
				offset -= separator.length;
				break;
			}
			if (offset == buffer.length) {
				throw new RuntimeException("Response header does not fit buffer");
			}
		}
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, 0, offset)));
	}

	/**
	 * Gets the HTTP body reader.
	 *
	 * @param inputStream
	 *            the input stream
	 * @return the HTTP body reader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedReader getHttpBodyReader(InputStream inputStream) throws IOException {
		return new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream), "UTF-8"));
	}

	/**
	 * Read headers.
	 *
	 * @param reader
	 *            the reader
	 * @return the hash map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private HashMap<String, String> readHeaders(BufferedReader reader) throws IOException {
		String line;
		HashMap<String, String> headers = new HashMap<>();
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(": ", 2);
			headers.put(parts[0], parts[1]);
		}
		return headers;
	}

	/**
	 * Should post data.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 */
	@Test
	public void shouldPostData() throws IOException, InterruptedException {
		InfluxDbHttpReporter reporter = new InfluxDbHttpReporter("http://localhost:8086/write?db=collectd", "localhost",
				registry);
		registry.add("jdbc.Statement.Duration", "select", 123);
		String request;
		String content;
		HashMap<String, String> headers = new HashMap<>();
		try (ServerSocket server = new ServerSocket(8086)) {
			Thread report = new Thread(() -> {
				reporter.report();
			});
			report.start();
			try (Socket connection = server.accept()) {
				BufferedReader head = getHttpHeaderReader(connection.getInputStream());
				request = head.readLine();
				headers = readHeaders(head);
				BufferedReader body = getHttpBodyReader(connection.getInputStream());
				content = body.readLine();
			}
			report.join();
		}
		assertThat(request).isEqualTo("POST /write?db=collectd HTTP/1.1");
		assertThat(headers.get("Content-Encoding")).isEqualTo("gzip");
		assertThat(content).isEqualTo(
				"jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000");
	}

	/**
	 * Should post data with utf 8 characters.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 */
	@Test
	public void shouldPostDataWithUtf8Characters() throws IOException, InterruptedException {
		InfluxDbHttpReporter reporter = new InfluxDbHttpReporter("http://localhost:8086/write?db=collectd", "localhost",
				registry);
		registry.add("spring.Username.Duration", "Александр", 123);
		String request;
		String content;
		HashMap<String, String> headers = new HashMap<>();
		try (ServerSocket server = new ServerSocket(8086)) {
			Thread report = new Thread(() -> {
				reporter.report();
			});
			report.start();
			try (Socket connection = server.accept()) {
				BufferedReader head = getHttpHeaderReader(connection.getInputStream());
				request = head.readLine();
				headers = readHeaders(head);
				BufferedReader body = getHttpBodyReader(connection.getInputStream());
				content = body.readLine();
			}
			report.join();
		}
		assertThat(request).isEqualTo("POST /write?db=collectd HTTP/1.1");
		assertThat(headers.get("Content-Encoding")).isEqualTo("gzip");
		assertThat(content).isEqualTo(
				"spring,host=localhost,instance=Username,type=Duration,type_instance=Александр value=123i 1510373758000000000");
	}

}