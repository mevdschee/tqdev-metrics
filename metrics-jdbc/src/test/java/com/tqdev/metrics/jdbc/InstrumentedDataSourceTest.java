package com.tqdev.metrics.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class InstrumentedDataSourceTest extends InstrumentedDataSourceTestBase {

	@Before
	public void initialize() {
		registry.reset();
	}

	@Test
	public void shouldMeasureSelectSingleUserOnceWithPreparedStatement() throws SQLException {
		String sql = "select * from users where id = ?";
		PreparedStatement statement = dataSource.getConnection().prepareStatement(sql);
		statement.setInt(1, 1);
		ResultSet resultSet = statement.executeQuery();
		assertThat(resultSet).isInstanceOf(ResultSet.class);
		assertThat(registry.get("jdbc.Statement.Invocations", sql)).isEqualTo(1);
		assertThat(registry.get("jdbc.Statement.Durations", sql)).isEqualTo(123456789);
	}

	@Test
	public void shouldMeasureSelectSingleUserTwiceWithPreparedStatement() throws SQLException {
		String sql = "select * from users where id = ?";
		PreparedStatement statement = dataSource.getConnection().prepareStatement(sql);
		statement.setInt(1, 1);
		statement.executeQuery();
		statement.executeQuery();
		assertThat(registry.get("jdbc.Statement.Invocations", sql)).isEqualTo(2);
		assertThat(registry.get("jdbc.Statement.Durations", sql)).isEqualTo(246913578);
	}

}