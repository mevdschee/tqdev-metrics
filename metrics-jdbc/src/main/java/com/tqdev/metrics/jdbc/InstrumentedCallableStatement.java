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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.tqdev.metrics.core.MetricRegistry;

/**
 * Instrumentation wrapper class for {@link CallableStatement}.
 */
public class InstrumentedCallableStatement extends InstrumentedPreparedStatement implements CallableStatement {

	/** The wrapped callable statement. */
	private final CallableStatement callableStatement;

	/**
	 * Instantiates a new instrumented callable statement.
	 *
	 * @param sql
	 *            the sql
	 * @param callableStatement
	 *            the callable statement
	 * @param registry
	 *            the registry
	 */
	InstrumentedCallableStatement(String sql, CallableStatement callableStatement, MetricRegistry registry) {
		super(sql, callableStatement, registry);
		this.callableStatement = callableStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(int, int)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		callableStatement.registerOutParameter(parameterIndex, sqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#wasNull()
	 */
	@Override
	public boolean wasNull() throws SQLException {
		return callableStatement.wasNull();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getString(int)
	 */
	@Override
	public String getString(int parameterIndex) throws SQLException {
		return callableStatement.getString(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBoolean(int)
	 */
	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return callableStatement.getBoolean(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getByte(int)
	 */
	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return callableStatement.getByte(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getShort(int)
	 */
	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return callableStatement.getShort(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getInt(int)
	 */
	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return callableStatement.getInt(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getLong(int)
	 */
	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return callableStatement.getLong(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getFloat(int)
	 */
	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return callableStatement.getFloat(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getDouble(int)
	 */
	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return callableStatement.getDouble(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBigDecimal(int, int)
	 */
	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return callableStatement.getBigDecimal(parameterIndex, scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBytes(int)
	 */
	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return callableStatement.getBytes(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getDate(int)
	 */
	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return callableStatement.getDate(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTime(int)
	 */
	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return callableStatement.getTime(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTimestamp(int)
	 */
	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return callableStatement.getTimestamp(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getObject(int)
	 */
	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return callableStatement.getObject(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBigDecimal(int)
	 */
	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return callableStatement.getBigDecimal(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
	 */
	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return callableStatement.getObject(parameterIndex, map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getRef(int)
	 */
	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return callableStatement.getRef(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBlob(int)
	 */
	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return callableStatement.getBlob(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getClob(int)
	 */
	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return callableStatement.getClob(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getArray(int)
	 */
	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return callableStatement.getArray(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
	 */
	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return callableStatement.getDate(parameterIndex, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
	 */
	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return callableStatement.getTime(parameterIndex, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
	 */
	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return callableStatement.getTimestamp(parameterIndex, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(int, int,
	 * java.lang.String)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		callableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String,
	 * int)
	 */
	@Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		callableStatement.registerOutParameter(parameterName, sqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String,
	 * int, int)
	 */
	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		callableStatement.registerOutParameter(parameterName, sqlType, scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String,
	 * int, java.lang.String)
	 */
	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		callableStatement.registerOutParameter(parameterName, sqlType, typeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getURL(int)
	 */
	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return callableStatement.getURL(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
	 */
	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		callableStatement.setURL(parameterName, val);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int)
	 */
	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		callableStatement.setNull(parameterName, sqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBoolean(java.lang.String, boolean)
	 */
	@Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		callableStatement.setBoolean(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setByte(java.lang.String, byte)
	 */
	@Override
	public void setByte(String parameterName, byte x) throws SQLException {
		callableStatement.setByte(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setShort(java.lang.String, short)
	 */
	@Override
	public void setShort(String parameterName, short x) throws SQLException {
		callableStatement.setShort(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setInt(java.lang.String, int)
	 */
	@Override
	public void setInt(String parameterName, int x) throws SQLException {
		callableStatement.setInt(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setLong(java.lang.String, long)
	 */
	@Override
	public void setLong(String parameterName, long x) throws SQLException {
		callableStatement.setLong(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setFloat(java.lang.String, float)
	 */
	@Override
	public void setFloat(String parameterName, float x) throws SQLException {
		callableStatement.setFloat(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setDouble(java.lang.String, double)
	 */
	@Override
	public void setDouble(String parameterName, double x) throws SQLException {
		callableStatement.setDouble(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBigDecimal(java.lang.String,
	 * java.math.BigDecimal)
	 */
	@Override
	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		callableStatement.setBigDecimal(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setString(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setString(String parameterName, String x) throws SQLException {
		callableStatement.setString(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBytes(java.lang.String, byte[])
	 */
	@Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		callableStatement.setBytes(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date)
	 */
	@Override
	public void setDate(String parameterName, Date x) throws SQLException {
		callableStatement.setDate(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time)
	 */
	@Override
	public void setTime(String parameterName, Time x) throws SQLException {
		callableStatement.setTime(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String,
	 * java.sql.Timestamp)
	 */
	@Override
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		callableStatement.setTimestamp(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String,
	 * java.io.InputStream, int)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		callableStatement.setAsciiStream(parameterName, x, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String,
	 * java.io.InputStream, int)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		callableStatement.setBinaryStream(parameterName, x, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setObject(java.lang.String,
	 * java.lang.Object, int, int)
	 */
	@Override
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		callableStatement.setObject(parameterName, x, targetSqlType, scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setObject(java.lang.String,
	 * java.lang.Object, int)
	 */
	@Override
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		callableStatement.setObject(parameterName, x, targetSqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setObject(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setObject(String parameterName, Object x) throws SQLException {
		callableStatement.setObject(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String,
	 * java.io.Reader, int)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		callableStatement.setCharacterStream(parameterName, reader, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date,
	 * java.util.Calendar)
	 */
	@Override
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		callableStatement.setDate(parameterName, x, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time,
	 * java.util.Calendar)
	 */
	@Override
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		callableStatement.setTime(parameterName, x, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String,
	 * java.sql.Timestamp, java.util.Calendar)
	 */
	@Override
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		callableStatement.setTimestamp(parameterName, x, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int,
	 * java.lang.String)
	 */
	@Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		callableStatement.setNull(parameterName, sqlType, typeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getString(java.lang.String)
	 */
	@Override
	public String getString(String parameterName) throws SQLException {
		return callableStatement.getString(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
	 */
	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return callableStatement.getBoolean(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getByte(java.lang.String)
	 */
	@Override
	public byte getByte(String parameterName) throws SQLException {
		return callableStatement.getByte(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getShort(java.lang.String)
	 */
	@Override
	public short getShort(String parameterName) throws SQLException {
		return callableStatement.getShort(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getInt(java.lang.String)
	 */
	@Override
	public int getInt(String parameterName) throws SQLException {
		return callableStatement.getInt(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getLong(java.lang.String)
	 */
	@Override
	public long getLong(String parameterName) throws SQLException {
		return callableStatement.getLong(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getFloat(java.lang.String)
	 */
	@Override
	public float getFloat(String parameterName) throws SQLException {
		return callableStatement.getFloat(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getDouble(java.lang.String)
	 */
	@Override
	public double getDouble(String parameterName) throws SQLException {
		return callableStatement.getDouble(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBytes(java.lang.String)
	 */
	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return callableStatement.getBytes(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getDate(java.lang.String)
	 */
	@Override
	public Date getDate(String parameterName) throws SQLException {
		return callableStatement.getDate(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTime(java.lang.String)
	 */
	@Override
	public Time getTime(String parameterName) throws SQLException {
		return callableStatement.getTime(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
	 */
	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return callableStatement.getTimestamp(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String parameterName) throws SQLException {
		return callableStatement.getObject(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
	 */
	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return callableStatement.getBigDecimal(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getObject(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return callableStatement.getObject(parameterName, map);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getRef(java.lang.String)
	 */
	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return callableStatement.getRef(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getBlob(java.lang.String)
	 */
	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return callableStatement.getBlob(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getClob(java.lang.String)
	 */
	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return callableStatement.getClob(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getArray(java.lang.String)
	 */
	@Override
	public Array getArray(String parameterName) throws SQLException {
		return callableStatement.getArray(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getDate(java.lang.String,
	 * java.util.Calendar)
	 */
	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return callableStatement.getDate(parameterName, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTime(java.lang.String,
	 * java.util.Calendar)
	 */
	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return callableStatement.getTime(parameterName, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String,
	 * java.util.Calendar)
	 */
	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return callableStatement.getTimestamp(parameterName, cal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getURL(java.lang.String)
	 */
	@Override
	public URL getURL(String parameterName) throws SQLException {
		return callableStatement.getURL(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getRowId(int)
	 */
	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return callableStatement.getRowId(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getRowId(java.lang.String)
	 */
	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return callableStatement.getRowId(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setRowId(java.lang.String,
	 * java.sql.RowId)
	 */
	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		callableStatement.setRowId(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNString(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setNString(String parameterName, String value) throws SQLException {
		callableStatement.setNString(parameterName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String,
	 * java.io.Reader, long)
	 */
	@Override
	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		callableStatement.setNCharacterStream(parameterName, value, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNClob(java.lang.String,
	 * java.sql.NClob)
	 */
	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		callableStatement.setNClob(parameterName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader,
	 * long)
	 */
	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		callableStatement.setClob(parameterName, reader, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBlob(java.lang.String,
	 * java.io.InputStream, long)
	 */
	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		callableStatement.setBlob(parameterName, inputStream, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNClob(java.lang.String,
	 * java.io.Reader, long)
	 */
	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		callableStatement.setNClob(parameterName, reader, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getNClob(int)
	 */
	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return callableStatement.getNClob(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getNClob(java.lang.String)
	 */
	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return callableStatement.getNClob(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setSQLXML(java.lang.String,
	 * java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		callableStatement.setSQLXML(parameterName, xmlObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getSQLXML(int)
	 */
	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return callableStatement.getSQLXML(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getSQLXML(java.lang.String)
	 */
	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return callableStatement.getSQLXML(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getNString(int)
	 */
	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return callableStatement.getNString(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getNString(java.lang.String)
	 */
	@Override
	public String getNString(String parameterName) throws SQLException {
		return callableStatement.getNString(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getNCharacterStream(int)
	 */
	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return callableStatement.getNCharacterStream(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getNCharacterStream(java.lang.String)
	 */
	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return callableStatement.getNCharacterStream(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getCharacterStream(int)
	 */
	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return callableStatement.getCharacterStream(parameterIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getCharacterStream(java.lang.String)
	 */
	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return callableStatement.getCharacterStream(parameterName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		callableStatement.setBlob(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.sql.Clob)
	 */
	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		callableStatement.setClob(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String,
	 * java.io.InputStream, long)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		callableStatement.setAsciiStream(parameterName, x, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String,
	 * java.io.InputStream, long)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		callableStatement.setBinaryStream(parameterName, x, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String,
	 * java.io.Reader, long)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		callableStatement.setCharacterStream(parameterName, reader, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		callableStatement.setAsciiStream(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		callableStatement.setBinaryStream(parameterName, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String,
	 * java.io.Reader)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		callableStatement.setCharacterStream(parameterName, reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String,
	 * java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		callableStatement.setNCharacterStream(parameterName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader)
	 */
	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		callableStatement.setClob(parameterName, reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setBlob(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		callableStatement.setBlob(parameterName, inputStream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setNClob(java.lang.String,
	 * java.io.Reader)
	 */
	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		callableStatement.setNClob(parameterName, reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getObject(int, java.lang.Class)
	 */
	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return callableStatement.getObject(parameterIndex, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#getObject(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return callableStatement.getObject(parameterName, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setObject(java.lang.String,
	 * java.lang.Object, java.sql.SQLType, int)
	 */
	@Override
	public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength)
			throws SQLException {
		callableStatement.setObject(parameterName, x, targetSqlType, scaleOrLength);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#setObject(java.lang.String,
	 * java.lang.Object, java.sql.SQLType)
	 */
	@Override
	public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
		callableStatement.setObject(parameterName, x, targetSqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(int,
	 * java.sql.SQLType)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
		callableStatement.registerOutParameter(parameterIndex, sqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(int,
	 * java.sql.SQLType, int)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
		callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(int,
	 * java.sql.SQLType, java.lang.String)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
		callableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String,
	 * java.sql.SQLType)
	 */
	@Override
	public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
		callableStatement.registerOutParameter(parameterName, sqlType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String,
	 * java.sql.SQLType, int)
	 */
	@Override
	public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
		callableStatement.registerOutParameter(parameterName, sqlType, scale);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String,
	 * java.sql.SQLType, java.lang.String)
	 */
	@Override
	public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
		callableStatement.registerOutParameter(parameterName, sqlType, typeName);
	}
}
