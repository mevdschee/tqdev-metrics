package com.tqdev.metrics.jdbc;

import java.sql.*;
import javax.sql.DataSource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.tqdev.metrics.core.MetricRegistry;

public class InstrumentedDataSourceTestBase {

    /**
     * The registry.
     */
    protected final MetricRegistry registry = spy(MetricRegistry.getInstance());

    protected final InstrumentedDataSource dataSource;

    public InstrumentedDataSourceTestBase() {
        long now = 1510373758000000000L;
        when(registry.getTime()).thenReturn(now, now + 123456789);

        DataSource mockedDataSource = mock(DataSource.class);
        dataSource = new InstrumentedDataSource(mockedDataSource, registry);
        dataSource.setMetricsEnabled(true);
        try {
            // other mocks
            Connection connection = mock(Connection.class);
            Statement statement = mock(Statement.class);
            CallableStatement callableStatement = mock(CallableStatement.class);
            PreparedStatement preparedStatement = mock(PreparedStatement.class);
            ResultSet resultSet = mock(ResultSet.class);
            // datasource
            doReturn(connection).when(mockedDataSource).getConnection();
            doReturn(connection).when(mockedDataSource).getConnection(anyString(), anyString());
            // connection
            doReturn(statement).when(connection).createStatement();
            doReturn(statement).when(connection).createStatement(anyInt(), anyInt());
            doReturn(statement).when(connection).createStatement(anyInt(), anyInt(), anyInt());
            doReturn(callableStatement).when(connection).prepareCall(anyString());
            doReturn(callableStatement).when(connection).prepareCall(anyString(), anyInt(), anyInt());
            doReturn(callableStatement).when(connection).prepareCall(anyString(), anyInt(), anyInt(), anyInt());
            doReturn(preparedStatement).when(connection).prepareStatement(anyString());
            doReturn(preparedStatement).when(connection).prepareStatement(anyString(), anyInt());
            doReturn(preparedStatement).when(connection).prepareStatement(anyString(), any(int[].class));
            doReturn(preparedStatement).when(connection).prepareStatement(anyString(), anyInt(), anyInt());
            doReturn(preparedStatement).when(connection).prepareStatement(anyString(), any(String[].class));
            doReturn(preparedStatement).when(connection).prepareStatement(anyString(), anyInt(), anyInt(), anyInt());
            // statement
            doReturn(resultSet).when(statement).executeQuery(anyString());
            doReturn(true).when(statement).execute(anyString());
            doReturn(true).when(statement).execute(anyString(), anyInt());
            doReturn(true).when(statement).execute(anyString(), any(int[].class));
            doReturn(true).when(statement).execute(anyString(), any(String[].class));
            doReturn(1).when(statement).executeUpdate(anyString());
            doReturn(1).when(statement).executeUpdate(anyString(), anyInt());
            doReturn(1).when(statement).executeUpdate(anyString(), any(int[].class));
            doReturn(1).when(statement).executeUpdate(anyString(), any(String[].class));
            doReturn(1L).when(statement).executeLargeUpdate(anyString());
            doReturn(1L).when(statement).executeLargeUpdate(anyString(), anyInt());
            doReturn(1L).when(statement).executeLargeUpdate(anyString(), any(int[].class));
            doReturn(1L).when(statement).executeLargeUpdate(anyString(), any(String[].class));
            // preparedStatement
            doReturn(resultSet).when(preparedStatement).executeQuery();
            doReturn(resultSet).when(preparedStatement).executeQuery(anyString());
            doReturn(true).when(preparedStatement).execute();
            doReturn(true).when(preparedStatement).execute(anyString());
            doReturn(true).when(preparedStatement).execute(anyString(), anyInt());
            doReturn(true).when(preparedStatement).execute(anyString(), any(int[].class));
            doReturn(true).when(preparedStatement).execute(anyString(), any(String[].class));
            doReturn(1).when(preparedStatement).executeUpdate();
            doReturn(1).when(preparedStatement).executeUpdate(anyString());
            doReturn(1).when(preparedStatement).executeUpdate(anyString(), anyInt());
            doReturn(1).when(preparedStatement).executeUpdate(anyString(), any(int[].class));
            doReturn(1).when(preparedStatement).executeUpdate(anyString(), any(String[].class));
            doReturn(1L).when(preparedStatement).executeLargeUpdate();
            doReturn(1L).when(preparedStatement).executeLargeUpdate(anyString());
            doReturn(1L).when(preparedStatement).executeLargeUpdate(anyString(), anyInt());
            doReturn(1L).when(preparedStatement).executeLargeUpdate(anyString(), any(int[].class));
            doReturn(1L).when(preparedStatement).executeLargeUpdate(anyString(), any(String[].class));
            // callableStatement
            doReturn(resultSet).when(callableStatement).executeQuery();
            doReturn(resultSet).when(callableStatement).executeQuery(anyString());
            doReturn(true).when(callableStatement).execute();
            doReturn(true).when(callableStatement).execute(anyString());
            doReturn(true).when(callableStatement).execute(anyString(), anyInt());
            doReturn(true).when(callableStatement).execute(anyString(), any(int[].class));
            doReturn(true).when(callableStatement).execute(anyString(), any(String[].class));
            doReturn(1).when(callableStatement).executeUpdate();
            doReturn(1).when(callableStatement).executeUpdate(anyString());
            doReturn(1).when(callableStatement).executeUpdate(anyString(), anyInt());
            doReturn(1).when(callableStatement).executeUpdate(anyString(), any(int[].class));
            doReturn(1).when(callableStatement).executeUpdate(anyString(), any(String[].class));
            doReturn(1L).when(callableStatement).executeLargeUpdate();
            doReturn(1L).when(callableStatement).executeLargeUpdate(anyString());
            doReturn(1L).when(callableStatement).executeLargeUpdate(anyString(), anyInt());
            doReturn(1L).when(callableStatement).executeLargeUpdate(anyString(), any(int[].class));
            doReturn(1L).when(callableStatement).executeLargeUpdate(anyString(), any(String[].class));
        } catch (SQLException e) {
            // ignore;
        }
    }

}