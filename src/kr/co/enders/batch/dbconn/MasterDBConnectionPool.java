package kr.co.enders.batch.dbconn;

import kr.co.enders.batch.conf.GetPropsFromConfig;
import kr.co.enders.batch.utils.StringUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

public class MasterDBConnectionPool
{
	/**
	 * MASTER DB
	 */
	static Logger              logger         = Logger.getLogger(DBConnectionManager.class);
	static private int         clients;
	private String             driver         = null;
	private String             url            = null;
	private String             user           = null;
	private String             password       = null;
	private int                maxConn;
	private String             encoding;
	private long               timeOutInterval;

	private int                checkedOut;
	private Vector<Connection> connectionPool = new Vector<Connection>();
	private Vector<Long>       lastAccessed   = new Vector<Long>();

	public MasterDBConnectionPool() {
		/*
		this.driver = "oracle.jdbc.driver.OracleDriver"; //GetPropsFromConfig.get("drivers");
		this.url = "jdbc:oracle:thin:@localhost:1521:orcl"; //GetPropsFromConfig.get("db.masterurl");
		this.user = "happy"; //GetPropsFromConfig.get("db.masteruser");
		this.password = "1111" ;//GetPropsFromConfig.get("db.masterpassword");
		this.encoding = "KSC5601" ;// GetPropsFromConfig.get("db.encoding");
		this.maxConn = 10 ; // Integer.parseInt(StringUtil.nvl(GetPropsFromConfig.get("db.maxconn"), "10"));
		this.timeOutInterval = 1000 * 60 * 30; // default timeout 30 min
		 */
		this.driver = GetPropsFromConfig.get("drivers");
		this.url = GetPropsFromConfig.get("db.dburl");
		this.user = GetPropsFromConfig.get("db.user");
		this.password = GetPropsFromConfig.get("db.rmspassword");
		this.encoding = GetPropsFromConfig.get("db.encoding");
		this.maxConn = Integer.parseInt(StringUtil.nvl(GetPropsFromConfig.get("db.maxconn"), "10"));
		this.timeOutInterval = 1000 * 60 * 30; // default timeout 30 min
	}

	private Connection newConnection()
	{
		Connection con = null;
		try {
			if (user == null) {
				con = DriverManager.getConnection(url);
			} else {
				Properties props = new Properties();
				props.put("user", user);
				props.put("password", password);
				props.put("encoding", encoding);
				con = DriverManager.getConnection(url, props);
			}
			logger.debug("Created a new connection in pool HyunDai Master DB");
		} catch (SQLException e) {
			logger.error("Connection  --- maxConn [" + maxConn + "]" + " --- driver [" + driver + "]"
					+ " --- checkedOut [" + checkedOut + "]" + " --- pool name [MASTER]"
					+ " --- freeConnections.size [" + connectionPool.size() + "]" + " --- con[" + con + "]" 
					+ " --- pool name [MASTER]" + " ---  [" + e.getMessage() + "]");
			return null;
		} finally {
			logger.debug("Connection ok --- maxConn [" + maxConn + "]" + " --- checkedOut [" + checkedOut + "]" 
					+ " --- pool name [MASTER]" + " --- freeConnections.size [" + connectionPool.size() + "]"
					+ " --- con[" + con + "]"+ " --- pool name [MASTER]");

			logger.info("================================newConnection==========================================");
			logger.info("Connection  ok --- maxConn [" + maxConn + "]" + " --- checkedOut [" + checkedOut + "]" 
					+ " --- pool name [MASTER]" + " --- freeConnections.size [" + connectionPool.size() + "]" 
					+ " --- con[" + con + "]" + " --- pool name [MASTER]" + "\n");
					
		}
		return con;
	}

	public synchronized Connection getConnection()
	{
		Connection con = null;
		boolean validConn = true; 
		Long accessTime;

		if ((connectionPool.size() > 0) && (checkedOut >= 0)) { 
			con = (Connection) connectionPool.firstElement();
			accessTime = (Long) lastAccessed.firstElement();

			connectionPool.removeElementAt(0);
			lastAccessed.removeElementAt(0);

			try {
				if (con.isClosed()) {
					logger.debug("Removed bad connection from MASTER");
					validConn = false; 
					con = getConnection();
				} else if (isTimeOuted(accessTime)) {
					logger.debug("Removed TimeOuted connection from MASTER");
					con.close();
					validConn = false;
					con = getConnection();
				}
			} catch (SQLException e) {
				logger.error("SQL Exception Removed bad connection from MASTER"); 
				validConn = false;
				con = getConnection();
			}
		} else if (checkedOut < 0) {
			checkedOut = 0;
			release();
			con = newConnection();
		} else if ((maxConn == 0) || (checkedOut < maxConn)) {
			con = newConnection();
		} else if ((checkedOut == 0) && (connectionPool.size() == 0)) {
			con = newConnection();
		}
		if ((con != null) && (validConn == true)) {
			checkedOut++;
		}
		logger.debug("(+)getConnection: Clients [" + clients + "]" + " maxConn[" + maxConn + "]"
				+ " checkedOut[" + checkedOut + "]" + " pool name[MASTER]"
				+ " freeConnections.size[" + connectionPool.size() + "]" + " con[" + con + "]");
		return con;
	}	

	public synchronized Connection getConnection(long timeout)
	{
		long startTime = new Date().getTime();
		Connection con;

		while ((con = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
			if ((new Date().getTime() - startTime) >= timeout) { 
				return null;
			}
		}
		return con;
	}

	private boolean isTimeOuted(Long accessTime)
	{
		long interval = System.currentTimeMillis() - accessTime.longValue();
		logger.debug("timeoutInterval [" + timeOutInterval + "] interval [" + interval + "]");
		return interval > timeOutInterval;
	}

	public synchronized void freeConnection(Connection con)
	{
		connectionPool.addElement(con);
		lastAccessed.addElement(new Long(System.currentTimeMillis()));
		checkedOut--;
		notifyAll(); 
		logger.debug("(-)freeConnection: Clients [" + clients + "]" + " maxConn[" + maxConn + "]"
				+ " checkedOut[" + checkedOut + "]" + " pool name[MASTER]"
				+ " freeConnections.size[" + connectionPool.size() + "]" + " con[" + con + "]"
				+ " pool name[MASTER]");
	}

	public synchronized void release()
	{
		Enumeration<Connection> allConnections = connectionPool.elements();
		while (allConnections.hasMoreElements()) {
			Connection con = (Connection) allConnections.nextElement();
			try {
				con.close();
				logger.debug("Closed connection for pool MASTER");
			} catch (SQLException e) {
				logger.error("Can't close connection for pool MASTER");
			}
		}
		connectionPool.removeAllElements();
		lastAccessed.removeAllElements();

		logger.debug("All connection released : Clients [" + clients + "]" + " maxConn[" + maxConn + "]" 
				+ " checkedOut[" + checkedOut + "]" + " pool name[MASTER]"
				+ " freeConnections.size[" + connectionPool.size() + "]");
	}
}
