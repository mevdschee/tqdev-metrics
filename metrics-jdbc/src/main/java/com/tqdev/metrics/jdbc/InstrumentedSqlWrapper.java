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

import java.sql.SQLException;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * The Class InstrumentedSqlWrapper.
 */
abstract class InstrumentedSqlWrapper {

	/** The metric registry. */
	protected MetricRegistry registry;

	/**
	 * Instantiates a new instrumented sql wrapper.
	 *
	 * @param registry
	 *            the registry
	 */
	InstrumentedSqlWrapper(MetricRegistry registry) {
		this.registry = registry;
	}

	/**
	 * The Interface SqlHandler.
	 *
	 * @param <C>
	 *            the generic type
	 */
	interface SqlHandler<C> {

		/**
		 * Execute.
		 *
		 * @return the c
		 * @throws SQLException
		 *             the SQL exception
		 */
		C execute() throws SQLException;
	}

	/**
	 * Timed execute.
	 *
	 * @param <C>
	 *            the generic type
	 * @param sql
	 *            the sql
	 * @param f
	 *            the f
	 * @return the c
	 * @throws SQLException
	 *             the SQL exception
	 */
	<C> C timedExecute(String sql, SqlHandler<C> f) throws SQLException {
		if (!registry.isEnabled()) {
			return f.execute();
		}
		long start = registry.getTime();
		try {
			return f.execute();
		} finally {
			long duration = registry.getTime() - start;
			registry.increment("jdbc.Statement.Invocations", sql);
			registry.add("jdbc.Statement.Durations", sql, duration);
		}
	}

}
