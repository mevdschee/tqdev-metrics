package com.tqdev.metrics.jdbc;

import java.sql.SQLException;

import com.tqdev.metrics.core.MetricRegistry;

abstract class InstrumentedSqlWrapper {
	protected MetricRegistry registry;

	InstrumentedSqlWrapper(MetricRegistry registry) {
		this.registry = registry;
	}

	interface SqlHandler<C> {
		C execute() throws SQLException;
	}

	<C> C timedExecute(String sql, SqlHandler<C> f) throws SQLException {
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
