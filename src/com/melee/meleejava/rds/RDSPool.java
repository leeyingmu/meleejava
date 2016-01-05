package com.melee.meleejava.rds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.hooks.AbstractConnectionHook;
import com.jolbox.bonecp.hooks.AcquireFailConfig;
import com.melee.meleejava.utils.Config;
import com.melee.meleejava.utils.JsonUtils;
import com.melee.meleejava.utils.MeleeLogger;

public class RDSPool {
	
	protected static final MeleeLogger logger = Config.logger;
	
	private static final Map<String, RDSPool> pools = new HashMap<String, RDSPool>();
	
	public static RDSPool getPool(String name) {
		if (pools.size() == 0) {
			init();
		}
		return pools.get(name);
	}
	
	public static void init() {
		Map<String, Object> config = Config.getInstance().getRDSPoolConfig();
		String driver = JsonUtils.getJsonVal(config, "driver");
		int minPool = JsonUtils.getJsonVal(config, "min_pool");
		int maxPool = JsonUtils.getJsonVal(config, "max_pool");
		int idleTimeout = JsonUtils.getJsonVal(config, "idle_timeout");
		Map<String, String> binds = Config.getInstance().getRDSBinds();
		for (String name: binds.keySet()) {
			String url = binds.get(name);
			String auth = url.substring(url.indexOf("//")+2, url.indexOf("@"));
			url = url.replaceAll(auth+"@", "");
			String username = auth.split(":")[0];
			String password = auth.split(":")[1];
			pools.put(name, new RDSPool(name, driver, url, username, password, minPool, maxPool, idleTimeout));
		}
	}
	
	public static void destroy() {
		for (String k: pools.keySet()) {
			RDSPool pool = pools.get(k);
			pool.pool.close();
			logger.info("RDSDB", "close pool " + k);
		}
	}
	
	private String name;
	private BoneCP pool;
	
	private RDSPool(String name, String driver, String url, String username, String password, int minPool, int maxPool, long idleTimeout) {
		this.name = name;
		Connection conn = null;
		try {
			Class.forName(driver);
			
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(url);
			config.setUsername(username);
			config.setPassword(password);
			config.setMinConnectionsPerPartition(minPool);
			config.setMaxConnectionsPerPartition(maxPool);
			config.setPartitionCount(1);
			config.setLazyInit(true);
			config.setAcquireIncrement(2);
			config.setConnectionHook(new MyConnectionHook());
			config.setAcquireRetryAttempts(5);
			config.setTransactionRecoveryEnabled(true);
			config.setReleaseHelperThreads(2);
			config.setConnectionTimeoutInMs(idleTimeout);
			this.pool = new BoneCP(config);
			conn = this.pool.getConnection();
			logger.info("RDSDB", "create pool " + this.name + ": " + (conn.isValid(30) ? "success":"failed" ));
		
		} catch (Exception e) {
			logger.error(e);
		} finally {
			free(conn);
		}
	}
	
	/**
	 * 获取该数据库连接池的一个连接，千万别忘了在一个
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return this.pool.getConnection();
	}
	
	/**
	 * 获取该连接池的一个连接，并执行RDSPoolExecutor
	 * @param e
	 * @return
	 * @throws SQLException
	 */
	public <T> T execute(RDSPoolExecutor<T> e) throws SQLException {
		Connection conn = null;
		try {
			conn = this.getConnection();
			logger.debug("RDSDB", "execute get one connection from " + this.name);
			return e.execute(conn);
		} finally {
			free(conn);
			logger.debug("RDSDB", "execute close one connection from " + this.name);
		}
	}
	
	/**
	 * 获取多个连接池的各一个连接，并执行RDSPoolExecutor，获取连接的顺序与names参数顺序一致
	 * @param e
	 * @param names
	 * @return
	 * @throws SQLException
	 */
	public static <T> T execute(RDSPoolExecutor<T> e, String... names) throws SQLException {
		Connection[] conns = new Connection[names.length];
		try {
			for (int i=0; i<names.length; i++) {
				conns[i] = getPool(names[i]).getConnection();
			}
			logger.debug("RDSDB", "execute get "+ names.length +" connections");
			return e.execute(conns);
		} finally {
			for (Connection conn: conns) {
				free(conn);
			}
			logger.debug("RDSDB", "execute close "+ names.length +" connections");
		}
	}
	
	/**
	 * 为防止外部使用连接池时忘记关掉连接，这里提供一个回调使用连接的接口，配合execute方法使用，可以不用关闭连接.
	 * @author lifachang
	 * @param <T>
	 */
	public static interface RDSPoolExecutor<T> {
		/**
		 * @param conns 数组顺序与RDSPool类的execute方法names参数顺序一致
		 * @return
		 */
		public T execute(Connection... conns);
	}
	
	private class MyConnectionHook extends AbstractConnectionHook {
		
		@Override
		public void onAcquire(ConnectionHandle connection) {
			super.onAcquire(connection);
		}
		@Override
		public boolean onAcquireFail(Throwable t, AcquireFailConfig acquireConfig) {
			return super.onAcquireFail(t, acquireConfig);
		}
		@Override
		public boolean onConnectionException(ConnectionHandle connection, String state, Throwable t)
		{
			return super.onConnectionException(connection, state, t);
		}
	}
	
	public static void free(Connection conn) {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e);
			}
	}
	
	public static void close(PreparedStatement psmt) {
		try {
			if (psmt != null)
				psmt.close();
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
	public static void close(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
	public static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	
	public static void close(ResultSet rs, PreparedStatement psmt) {
		close(rs);
		close(psmt);
	}
	
	public static void close(ResultSet rs, Statement stmt) {
		close(rs);
		close(stmt);
	}
	
}
