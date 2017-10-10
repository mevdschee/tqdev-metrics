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

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The InfluxDbFileReporter class reports values in the metric registry to
 * InfluxDB readable files.
 */
public class InfluxDbFileReporter extends InfluxDbReporter {

	private final String metricPath;
	private final int maxFileCount;

	public InfluxDbFileReporter(String metricPath, int maxFileCount, String instanceName, MetricRegistry registry) {
		super(instanceName, registry);
		this.metricPath = metricPath;
		this.maxFileCount = maxFileCount;
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
			write(out);
			compress(filename);
			remove(maxFileCount);
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

	private void compress(String filename) throws IOException {
		File current = new File(filename);
		File dir = new File(metricPath);
		FilenameFilter textFileFilter = (file, s) -> file.isFile() && s.endsWith(".txt");
		File[] directoryListing = dir.listFiles(textFileFilter);
		if (directoryListing != null) {
			for (File file : directoryListing) {
				if (file.getCanonicalPath()!=current.getCanonicalPath()) {
					FileOutputStream fos = new FileOutputStream(file.getPath() + ".gz");
					GZIPOutputStream gzos = new GZIPOutputStream(fos);
					byte[] buffer = new byte[8192];
					int length;
					FileInputStream fis = new FileInputStream(file.getPath());
					while ((length = fis.read(buffer)) > 0) {
						gzos.write(buffer, 0, length);
					}
					fis.close();
					gzos.finish();
					gzos.close();
					file.delete();
				}
			}
		} else {
			throw new IOException("Directory not listable: "+metricPath);
		}
	}

	private void remove(int maxFileCount) throws IOException {
		File dir = new File(metricPath);
		FilenameFilter gzipFileFilter = (file, s) -> file.isFile() && s.endsWith(".gz");
		File[] directoryListing = dir.listFiles(gzipFileFilter);
		Arrays.sort(directoryListing);
		if (directoryListing != null) {
			for (int i=0;i < directoryListing.length-maxFileCount; i++) {
					directoryListing[i].delete();
			}
		} else {
			throw new IOException("Directory not listable: "+metricPath);
		}
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
	public static void start(String metricPath, int maxFileCount, String instanceName, int intervalInSeconds) {
		MetricRegistry registry = MetricRegistry.getInstance();
		InfluxDbFileReporter reporter = new InfluxDbFileReporter(metricPath, maxFileCount, instanceName, registry);

		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(() -> reporter.report(), 1, intervalInSeconds, TimeUnit.SECONDS);
	}

}