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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

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
	 */
	@Before
	public void initialize() throws IOException {
		when(registry.getTime()).thenReturn(1510373758000000000L);
		registry.reset();
		tempPath = Files.createTempDirectory(null);
		tempPath.toFile().deleteOnExit();
		reporter = new InfluxDbFileReporter(tempPath.toString(), "yyyyMMdd", 2, "localhost", registry);
	}

	/**
	 * Cleanup.
	 */
	@After
	public void cleanup() {
		File[] directoryListing = tempPath.toFile().listFiles();
		if (directoryListing != null) {
			for (int i = 0; i < directoryListing.length; i++) {
				directoryListing[i].delete();
			}
		}
	}

	@Test
	public void shouldCreateFile() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		boolean success = reporter.report();
		String content = String.join("\n", Files.readAllLines(tempPath.resolve("20171111.txt")));
		assertThat(success).isTrue();
		assertThat(content).isEqualTo(
				"jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758");
	}

	@Test
	public void shouldCompressFile() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		String line = "jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758";
		Files.write(tempPath.resolve("20171110.txt"), line.getBytes(), StandardOpenOption.CREATE_NEW);
		boolean success = reporter.report();
		FilenameFilter gzipFileFilter = (f, s) -> s.endsWith(".gz");
		File[] gzipFiles = tempPath.toFile().listFiles(gzipFileFilter);
		FilenameFilter textFileFilter = (f, s) -> s.endsWith(".txt");
		File[] textFiles = tempPath.toFile().listFiles(textFileFilter);
		assertThat(success).isTrue();
		assertThat(gzipFiles.length).isEqualTo(1);
		assertThat(gzipFiles[0].getName()).isEqualTo("20171110.txt.gz");
		assertThat(textFiles.length).isEqualTo(1);
		assertThat(textFiles[0].getName()).isEqualTo("20171111.txt");
	}

	@Test
	public void shouldRotateFiles() throws IOException {
		registry.add("jdbc.Statement.Duration", "select", 123);
		String line = "jdbc,host=localhost,instance=Statement,type=Duration,type_instance=select value=123i 1510373758";
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(line.getBytes());
		gzip.flush();
		gzip.close();
		byte[] compressed = obj.toByteArray();
		Files.write(tempPath.resolve("20171108.txt.gz"), compressed, StandardOpenOption.CREATE_NEW);
		Files.write(tempPath.resolve("20171109.txt.gz"), compressed, StandardOpenOption.CREATE_NEW);
		Files.write(tempPath.resolve("20171110.txt"), line.getBytes(), StandardOpenOption.CREATE_NEW);
		boolean success = reporter.report();
		FilenameFilter gzipFileFilter = (f, s) -> s.endsWith(".gz");
		File[] gzipFiles = tempPath.toFile().listFiles(gzipFileFilter);
		Arrays.sort(gzipFiles);
		FilenameFilter textFileFilter = (f, s) -> s.endsWith(".txt");
		File[] textFiles = tempPath.toFile().listFiles(textFileFilter);
		assertThat(success).isTrue();
		assertThat(gzipFiles.length).isEqualTo(2);
		assertThat(gzipFiles[0].getName()).isEqualTo("20171109.txt.gz");
		assertThat(gzipFiles[1].getName()).isEqualTo("20171110.txt.gz");
		assertThat(textFiles.length).isEqualTo(1);
		assertThat(textFiles[0].getName()).isEqualTo("20171111.txt");
	}

}