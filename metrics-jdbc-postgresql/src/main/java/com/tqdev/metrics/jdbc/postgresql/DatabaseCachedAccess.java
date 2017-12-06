package com.tqdev.metrics.jdbc.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class DatabaseCachedAccess {

	final Connection connection;

	public DatabaseCachedAccess(Connection connection) {
		this.connection = connection;
	}

	protected long queryLong(String sql) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return -1;
			}
			return rs.getInt(0);
		} catch (SQLException e) {
			return -1;
		}
	}

	protected String queryString(String sql) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return "";
			}
			return rs.getString(0);
		} catch (SQLException e) {
			return "";
		}
	}

	protected long queryLongWithParameter(String sql, String p1) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, p1);
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return -1;
			}
			return rs.getInt(0);
		} catch (SQLException e) {
			return -1;
		}
	}

	protected Map<String, Long> queryPairs(String sql) {
		TreeMap<String, Long> pairs = new TreeMap<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				pairs.put(rs.getString(0), rs.getLong(1));
			}
			return pairs;
		} catch (SQLException e) {
			return pairs;
		}
	}

	protected String[] queryStrings(String sql) {
		ArrayList<String> list = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString(0));
			}
			return list.toArray(new String[] {});
		} catch (SQLException e) {
			return list.toArray(new String[] {});
		}
	}

}
