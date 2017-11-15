package com.tqdev.metrics.jdbc;

import com.tqdev.metrics.core.MetricRegistry;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class InstrumentedDataSource implements DataSource, Closeable {
    private DataSource wrapped;
    private MetricRegistry registry;

    private volatile boolean metricsEnabled = false;

    public InstrumentedDataSource(DataSource wrapped, MetricRegistry registry) {
        this.wrapped = wrapped;
        this.registry = registry;
    }

    public void setMetricsEnabled(boolean enabled) {
        metricsEnabled = enabled;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!metricsEnabled) {
            return wrapped.getConnection();
        }
        return new InstrumentedConnection(wrapped.getConnection(), registry);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (!metricsEnabled) {
            return wrapped.getConnection();
        }
        return new InstrumentedConnection(wrapped.getConnection(username, password), registry);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return wrapped.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        wrapped.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        wrapped.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return wrapped.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return wrapped.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return wrapped.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return wrapped.isWrapperFor(iface);
    }

    @Override
    public void close() throws IOException {
        if (wrapped instanceof Closeable) {
            ((Closeable) wrapped).close();
        }
    }
}
