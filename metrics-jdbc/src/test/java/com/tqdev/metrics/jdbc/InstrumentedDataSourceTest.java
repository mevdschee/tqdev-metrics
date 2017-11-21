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

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

/**
 * The Class InstrumentedDataSourceTest.
 */
public class InstrumentedDataSourceTest extends InstrumentedDataSourceTestBase {

	/**
	 * Initialize.
	 */
	@Before
	public void initialize() {
		registry.reset();
	}

	/**
	 * Should measure select single user once with prepared statement.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void shouldMeasureSelectSingleUserOnceWithPreparedStatement() throws SQLException {
		String sql = "select * from users where id = ?";
		PreparedStatement statement = dataSource.getConnection().prepareStatement(sql);
		statement.setInt(1, 1);
		assertThat(statement.executeQuery()).isNotNull();
		assertThat(registry.get("jdbc.Statement.Invocations", sql)).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", sql)).isEqualTo(123456789);
	}

	/**
	 * Should measure select single user twice with prepared statement.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void shouldMeasureSelectSingleUserTwiceWithPreparedStatement() throws SQLException {
		String sql = "select * from users where id = ?";
		PreparedStatement statement = dataSource.getConnection().prepareStatement(sql);
		statement.setInt(1, 1);
		assertThat(statement.executeQuery()).isNotNull();
		assertThat(statement.executeQuery()).isNotNull();
		assertThat(registry.get("jdbc.Statement.Invocations", sql)).isEqualTo(2);
		assertThat(registry.get("jdbc.Statement.Durations", sql)).isEqualTo(246913578);
	}

}