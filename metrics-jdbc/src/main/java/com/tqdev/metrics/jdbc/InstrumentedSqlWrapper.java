package com.tqdev.metrics.jdbc;

import com.tqdev.metrics.core.MetricRegistry;

import java.sql.SQLException;

abstract class InstrumentedSqlWrapper {
    protected MetricRegistry registry;

    InstrumentedSqlWrapper(MetricRegistry registry) {
        this.registry = registry;
    }

    interface SqlHandler<C> {
        C execute() throws SQLException;
    }

    <C> C timedExecute(String sql, SqlHandler<C> f) throws SQLException {
        long start = System.nanoTime();
        try {
            return f.execute();
        } finally {
            long duration = System.nanoTime() - start;
            registry.increment("jdbc.query.Invocations", sql);
            registry.add("jdbc.query.Durations", sql, duration);
        }
    }

}
