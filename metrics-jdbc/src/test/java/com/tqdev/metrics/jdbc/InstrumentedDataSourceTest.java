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
	 * Initialize by resetting the metric registry.
	 */
	@Before
	public void initialize() {
		registry.reset();
	}

	@Test
	public void shouldMeasureQueryWithPreparedStatement() throws SQLException {
		dataSource.getConnection().prepareStatement("select").executeQuery();
		assertThat(registry.get("jdbc.Statement.Invocations", "select")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "select")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureQueryStringWithPreparedStatement() throws SQLException {
		dataSource.getConnection().prepareStatement("select1").executeQuery("select2");
		assertThat(registry.get("jdbc.Statement.Invocations", "select2")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "select2")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureQueryTwiceWithPreparedStatement() throws SQLException {
		PreparedStatement statement = dataSource.getConnection().prepareStatement("select");
		statement.executeQuery();
		statement.executeQuery();
		assertThat(registry.get("jdbc.Statement.Invocations", "select")).isEqualTo(2);
		assertThat(registry.get("jdbc.Statement.Durations", "select")).isEqualTo(246913578);
	}

	@Test
	public void shouldMeasureUpdateWithPreparedStatement() throws SQLException {
		dataSource.getConnection().prepareStatement("update").executeUpdate();
		assertThat(registry.get("jdbc.Statement.Invocations", "update")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "update")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureUpdateStringWithPreparedStatement() throws SQLException {
		dataSource.getConnection().prepareStatement("update1").executeUpdate("update2");
		assertThat(registry.get("jdbc.Statement.Invocations", "update2")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "update2")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureExecuteWithPreparedStatement() throws SQLException {
		dataSource.getConnection().prepareStatement("delete").execute();
		assertThat(registry.get("jdbc.Statement.Invocations", "delete")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "delete")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureExecuteStringWithPreparedStatement() throws SQLException {
		dataSource.getConnection().prepareStatement("delete1").execute("delete2");
		assertThat(registry.get("jdbc.Statement.Invocations", "delete2")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "delete2")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureQueryWithStatement() throws SQLException {
		dataSource.getConnection().createStatement().executeQuery("select");
		assertThat(registry.get("jdbc.Statement.Invocations", "select")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "select")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureUpdateWithStatement() throws SQLException {
		dataSource.getConnection().createStatement().executeUpdate("update");
		assertThat(registry.get("jdbc.Statement.Invocations", "update")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "update")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureExecuteWithStatement() throws SQLException {
		dataSource.getConnection().createStatement().execute("delete");
		assertThat(registry.get("jdbc.Statement.Invocations", "delete")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "delete")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureQueryWithCallableStatement() throws SQLException {
		dataSource.getConnection().prepareCall("call").executeQuery();
		assertThat(registry.get("jdbc.Statement.Invocations", "call")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "call")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureQueryStringWithCallableStatement() throws SQLException {
		dataSource.getConnection().prepareCall("call1").executeQuery("call2");
		assertThat(registry.get("jdbc.Statement.Invocations", "call2")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "call2")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureUpdateWithCallableStatement() throws SQLException {
		dataSource.getConnection().prepareCall("call").executeUpdate();
		assertThat(registry.get("jdbc.Statement.Invocations", "call")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "call")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureUpdateStringWithCallableStatement() throws SQLException {
		dataSource.getConnection().prepareCall("call1").executeUpdate("call2");
		assertThat(registry.get("jdbc.Statement.Invocations", "call2")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "call2")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureExecuteWithCallableStatement() throws SQLException {
		dataSource.getConnection().prepareCall("call").execute();
		assertThat(registry.get("jdbc.Statement.Invocations", "call")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "call")).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureExecuteStringWithCallableStatement() throws SQLException {
		dataSource.getConnection().prepareCall("call1").execute("call2");
		assertThat(registry.get("jdbc.Statement.Invocations", "call2")).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", "call2")).isEqualTo(123456789);
	}
}