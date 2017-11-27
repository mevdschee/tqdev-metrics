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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class InfluxDbFileReporterTest.
 */
public class InfluxDbFileReporterTest {

	/** The registry. */
	protected final MetricRegistry registry = spy(MetricRegistry.getInstance());

	/** The reporter. */
	private InfluxDbFileReporter reporter;

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
		when(registry.getMillis()).thenReturn(1510373758123L);
		registry.reset();
		tempPath = Files.createTempDirectory(null);
		reporter = new InfluxDbFileReporter(tempPath.toString(), "yyyyMMdd", 2, "localhost", registry);
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
		String content = String.join("\n", Files.readAllLines(tempPath.resolve("20171111.txt")));
		assertThat(success).isTrue();
		assertThat(content).isEqualTo(
				"jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000");
	}

	/**
	 * Should append file.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void shouldAppendFile() throws IOException {
		String line = "jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000\n";
		Files.write(tempPath.resolve("20171111.txt"), line.getBytes(), StandardOpenOption.CREATE_NEW);
		registry.add("jdbc.Statement.Duration", "select", 123);
		boolean success = reporter.report();
		String content = String.join("\n", Files.readAllLines(tempPath.resolve("20171111.txt")));
		assertThat(success).isTrue();
		assertThat(content).isEqualTo(
				"jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000\n"
						+ "jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000");
	}

	/**
	 * Should compress file.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void shouldCompressFile() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		String line = "jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000\n";
		Files.write(tempPath.resolve("20171110.txt"), line.getBytes(), StandardOpenOption.CREATE_NEW);
		boolean success = reporter.report();
		File[] gzipFiles = tempPath.toFile().listFiles((f, s) -> s.endsWith(".gz"));
		File[] textFiles = tempPath.toFile().listFiles((f, s) -> s.endsWith(".txt"));
		assertThat(success).isTrue();
		assertThat(gzipFiles.length).isEqualTo(1);
		assertThat(gzipFiles[0].getName()).isEqualTo("20171110.txt.gz");
		assertThat(textFiles.length).isEqualTo(1);
		assertThat(textFiles[0].getName()).isEqualTo("20171111.txt");
	}

	/**
	 * Should rotate files.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void shouldRotateFiles() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		String line = "jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758000000000\n";
		Files.createFile(tempPath.resolve("20171108.txt.gz"));
		Files.createFile(tempPath.resolve("20171109.txt.gz"));
		Files.write(tempPath.resolve("20171110.txt"), line.getBytes(), StandardOpenOption.CREATE_NEW);
		boolean success = reporter.report();
		File[] gzipFiles = tempPath.toFile().listFiles((f, s) -> s.endsWith(".gz"));
		Arrays.sort(gzipFiles);
		File[] textFiles = tempPath.toFile().listFiles((f, s) -> s.endsWith(".txt"));
		assertThat(success).isTrue();
		assertThat(gzipFiles.length).isEqualTo(2);
		assertThat(gzipFiles[0].getName()).isEqualTo("20171109.txt.gz");
		assertThat(gzipFiles[1].getName()).isEqualTo("20171110.txt.gz");
		assertThat(textFiles.length).isEqualTo(1);
		assertThat(textFiles[0].getName()).isEqualTo("20171111.txt");
	}

}