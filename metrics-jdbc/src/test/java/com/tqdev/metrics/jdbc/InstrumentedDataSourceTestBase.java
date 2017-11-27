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
package com.tqdev.metrics.jdbc;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.sql.DataSource;

import org.junit.Before;
import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class InstrumentedDataSourceTestBase.
 */
public class InstrumentedDataSourceTestBase {

	/** The registry. */
	protected MetricRegistry registry;

	/** The data source. */
	protected InstrumentedDataSource dataSource;

	/** The current time . */
	protected long now = 1510373758000000000L;

	/**
	 * Initialize by resetting the metric registry.
	 */
	@Before
	public void setUp() {
		registry = spy(new MetricRegistry());
		dataSource = new InstrumentedDataSource(mock(DataSource.class, RETURNS_DEEP_STUBS), registry);
		when(registry.getNanos()).thenAnswer(i -> now += 123456789);
	}

}