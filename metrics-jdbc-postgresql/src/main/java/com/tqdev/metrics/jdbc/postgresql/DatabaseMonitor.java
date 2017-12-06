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
package com.tqdev.metrics.jdbc.postgresql;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.tqdev.metrics.core.Gauge;
import com.tqdev.metrics.core.MetricRegistry;

public class DatabaseMonitor extends DatabaseCachedAccess {

	final MetricRegistry registry;

	public DatabaseMonitor(MetricRegistry registry, Connection connection) {
		super(connection);
		this.registry = registry;
		registerStatistics();
	}

	private void registerStatistics() {
		registry.set("pgsql.Database.Statistics", "connections",
				(Gauge) () -> queryLong("SELECT count(*) FROM pg_stat_activity"));
		registry.set("pgsql.Database.Statistics", "active-connections",
				(Gauge) () -> queryLong("SELECT count(*) FROM pg_stat_activity WHERE state='active'"));
		registry.set("pgsql.Database.Statistics", "transactions",
				(Gauge) () -> queryLong("SELECT xact_commit + xact_rollback FROM pg_stat_database"));
		registry.set("pgsql.Database.Statistics", "rollbacks",
				(Gauge) () -> queryLong("SELECT xact_rollback FROM pg_stat_database"));
		registry.set("pgsql.Database.Statistics", "disk-size",
				(Gauge) () -> queryLong("SELECT SUM(pg_database_size(datname)) FROM pg_database"));
		registry.set("pgsql.Database.Statistics", "oldest-tx", (Gauge) () -> queryLong(
				"SELECT EXTRACT(EPOCH FROM now()-xact_start)*1000000000 FROM pg_stat_activity WHERE xact_start IS NOT NULL ORDER BY xact_start ASC LIMIT 1"));
		registry.set("pgsql.Database.Statistics", "locks-granted",
				(Gauge) () -> queryLong("SELECT count(*) FROM pg_locks WHERE granted='t'"));

		registry.set("pgsql.Database.Statistics", "heap-hit-rate", (Gauge) () -> queryLong(
				"SELECT  sum(heap_blks_hit) / (sum(heap_blks_hit)+sum(heap_blks_read)) FROM pg_statio_user_tables group by relname"));
		registry.set("pgsql.Database.Statistics", "idx-hit-rate", (Gauge) () -> queryLong(
				"SELECT 100000*sum() / (sum(idx_blks_hit)+sum(idx_blks_read)) FROM pg_statio_user_tables"));
		registry.set("pgsql.Database.Statistics", "toast-hit-rate", (Gauge) () -> queryLong(
				"SELECT 100000*sum(toast_blks_hit) / (sum(toast_blks_hit)+sum(toast_blks_read)) FROM pg_statio_user_tables"));
		registry.set("pgsql.Database.Statistics", "tidx-hit-rate", (Gauge) () -> queryLong(
				"SELECT 100000*sum(tidx_blks_hit) / (sum(tidx_blks_hit)+sum(tidx_blks_read)) FROM pg_statio_user_tables"));
	}

	public Map<String, String> getSystemInformation() {
		HashMap<String, String> map = new HashMap<>();
		map.put("dbVersion", queryString("SELECT substring(version() from $$(\\d+\\.\\d+)\\.\\d+$$)"));
		return map;
	}

}
