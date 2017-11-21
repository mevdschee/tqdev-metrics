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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
	public void shouldMeasurePreparedStatement() throws SQLException {
		PreparedStatement statements[] = { dataSource.getConnection().prepareStatement("select"),
				dataSource.getConnection().prepareStatement("select", 1),
				dataSource.getConnection().prepareStatement("select", new int[] {}),
				dataSource.getConnection().prepareStatement("select", new String[] {}),
				dataSource.getConnection().prepareStatement("select", 1, 1),
				dataSource.getConnection().prepareStatement("select", 1, 1, 1) };
		for (PreparedStatement statement : statements) {
			statement.execute();
			statement.executeQuery();
			statement.executeUpdate();
			statement.executeLargeUpdate();
		}
		assertThat(registry.get("jdbc.Statement.Invocations", "select")).isEqualTo(1L * 4 * statements.length);
		assertThat(registry.get("jdbc.Statement.Durations", "select")).isEqualTo(123456789L * 4 * statements.length);
	}

	@Test
	public void shouldMeasureCallableStatement() throws SQLException {
		CallableStatement statements[] = { dataSource.getConnection().prepareCall("select"),
				dataSource.getConnection().prepareCall("select", 1, 1),
				dataSource.getConnection().prepareCall("select", 1, 1, 1) };
		for (CallableStatement statement : statements) {
			statement.execute();
			statement.executeQuery();
			statement.executeUpdate();
			statement.executeLargeUpdate();
		}
		assertThat(registry.get("jdbc.Statement.Invocations", "select")).isEqualTo(1L * 4 * statements.length);
		assertThat(registry.get("jdbc.Statement.Durations", "select")).isEqualTo(123456789L * 4 * statements.length);
	}

	@Test
	public void shouldMeasureStatement() throws SQLException {
		Statement statements[] = { dataSource.getConnection().createStatement(),
				dataSource.getConnection().createStatement(1, 1), dataSource.getConnection().createStatement(1, 1, 1) };
		for (Statement statement : statements) {
			statement.execute("select");
			statement.execute("select", 1);
			statement.execute("select", new int[] {});
			statement.execute("select", new String[] {});
			statement.executeQuery("select");
			statement.executeUpdate("select");
			statement.executeUpdate("select", 1);
			statement.executeUpdate("select", new int[] {});
			statement.executeUpdate("select", new String[] {});
			statement.executeLargeUpdate("select");
			statement.executeLargeUpdate("select", 1);
			statement.executeLargeUpdate("select", new int[] {});
			statement.executeLargeUpdate("select", new String[] {});
			statement.addBatch("select");
			statement.executeBatch();
			statement.executeLargeBatch();
		}
		assertThat(registry.get("jdbc.Statement.Invocations", "select")).isEqualTo(1L * 15 * statements.length);
		assertThat(registry.get("jdbc.Statement.Durations", "select")).isEqualTo(123456789L * 15 * statements.length);
	}

}