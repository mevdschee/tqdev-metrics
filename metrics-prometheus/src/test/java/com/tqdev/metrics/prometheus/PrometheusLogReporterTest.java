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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class PrometheusFileReporterTest.
 */
public class PrometheusLogReporterTest {

	/** The registry. */
	protected MetricRegistry registry;

	/** The reporter. */
	private PrometheusLogReporter reporter;

	/** The reporter. */
	private Path tempPath;

	/**
	 * Initialize.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Before
	public void setUp() throws IOException {
		registry = spy(new MetricRegistry());
		tempPath = Files.createTempDirectory(null);
		reporter = new PrometheusLogReporter(registry, "localhost", tempPath.toString(), 2);
		when(registry.getMillis()).thenReturn(1510373758123L);
	}

	/**
	 * Cleanup.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException {
		File[] directoryListing = tempPath.toFile().listFiles();
		if (directoryListing != null) {
			for (int i = 0; i < directoryListing.length; i++) {
				directoryListing[i].delete();
			}
		}
		Files.delete(tempPath);
	}

	/**
	 * Should create directory.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void shouldCreateDirectory() throws IOException {
		boolean deleted = tempPath.toFile().delete();
		registry.add("jdbc.Statement.Duration", "select", 123);
		boolean success = reporter.report();
		boolean exists = tempPath.toFile().exists();
		assertThat(deleted).isTrue();
		assertThat(success).isTrue();
		assertThat(exists).isTrue();
	}

	/**
	 * Should create file.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void shouldCreateFile() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		boolean success = reporter.report();
		String content = String.join("\n", Files.readAllLines(tempPath.resolve("1510373758123.prom")));
		assertThat(success).isTrue();
		assertThat(content).isEqualTo(
				"jdbc{host=\"localhost\",instance=\"Statement\",type=\"Duration\",type_instance=\"select\"} 123 1510373758000");
	}

	/**
	 * Should remove files.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void shouldRemoveFiles() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		String line = "jdbc{host=\"localhost\",instance=\"Statement\",type=\"Duration\",type_instance=\"select\"} 123 1510373758000\n";
		Files.createFile(tempPath.resolve("1510373738123.prom"));
		Files.createFile(tempPath.resolve("1510373748123.prom"));
		Files.write(tempPath.resolve("1510373758123.prom"), line.getBytes(), StandardOpenOption.CREATE_NEW);
		boolean success = reporter.report();
		File[] promFiles = tempPath.toFile().listFiles((f, s) -> s.endsWith(".prom"));
		assertThat(success).isTrue();
		assertThat(promFiles.length).isEqualTo(2);
		assertThat(promFiles[0].getName()).isEqualTo("1510373748123.prom");
		assertThat(promFiles[1].getName()).isEqualTo("1510373758123.prom");
	}

}