package kr.co.enders.batch.dbconn;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import kr.co.enders.batch.utils.DateTimeUtil;

public class DBConnectionManager
{

	static Logger							   logger  = Logger.getLogger(DBConnectionManager.class);

	static private DBConnectionManager		  instance;											 // The single instance

	static private int						  clients;

	private Vector<Driver>					  drivers = new Vector<Driver>();

	private PrintWriter						 log;

	private Hashtable<String, DBConnectionPool> pools   = new Hashtable<String, DBConnectionPool>();

	/**
	 * ***********************************************************************************************
	 * ********************************** start DBConnectionPool *************************************
	 * ***********************************************************************************************
	*/
	class DBConnectionPool
	{

		private int				checkedOut;
	
		private Vector<Connection> freeConnections = new Vector<Connection>();
	
		private int				maxConn;
	
		private String			 name;
	
		private String			 password;
	
		private String			 URL;
	
		private String			 user;
	
		private String			 encoding;
	
		private Vector<Long>	   lastAccessed	= new Vector<Long>();
	
		private long			   timeOutInterval;
	
		public DBConnectionPool(String name, String URL, String user, String password, String encoding, int maxConn) {
			this.name = name;
			this.URL = URL;
			this.user = user;
			this.password = password;
			this.encoding = encoding;
			this.maxConn = maxConn;
	
			this.timeOutInterval = 1000 * 60 * 30; // default timeout 30 min
		}
	
		public DBConnectionPool(String name, String URL, String user, String password, String encoding, int maxConn, int initConn) {
			this.name = name;
			this.URL = URL;
			this.user = user;
			this.password = password;
			this.encoding = encoding;
			this.maxConn = maxConn;
			this.timeOutInterval = 1000 * 60 * 30; // default timeout 30 min
	
			for (int i = 0; i < initConn; i++) {
				freeConnections.addElement(newConnection());
				logger.info("======================freeConnections.addElement(newConnection())======================");
				lastAccessed.addElement(new Long(System.currentTimeMillis()));
			}
		}
	
		public DBConnectionPool(String name, String URL, String user, String password, String encoding, int maxConn, int initConn, long timeOut) {
			this(name, URL, user, password, encoding, maxConn, initConn);
			this.timeOutInterval = timeOut;
		}
	
		private Connection newConnection()
		{
			Connection con = null;
			try {
				if (user == null) {
					con = DriverManager.getConnection(URL);
				} else {
					Properties props = new Properties();
					props.put("user", user);
					props.put("password", password);
					props.put("encoding", encoding);
					con = DriverManager.getConnection(URL, props);
				}
				log("Created a new connection in pool " + name);
			} catch (SQLException e) {
				log("Connection  -- maxConn [" + maxConn + "]" + " --- checkedOut [" + checkedOut + "]" 
						+ " --- pool name [" + name + "]" + " --- freeConnections.size [" + freeConnections.size() + "]" 
						+ " --- con[" + con + "]" + " --- pool name [" + name + "]" + " ---  [" + e.getMessage() + "]");
				log(e, "Can't create a new connection for " + URL);
	
				logger.error("Connection  --- maxConn [" + maxConn + "]" + " --- checkedOut [" + checkedOut + "]" 
						 + " --- pool name [" + name + "]" + " --- freeConnections.size [" + freeConnections.size() + "]"
						 + " --- con[" + con + "]" + " --- pool name [" + name + "]" + " ---   ["+ e.getMessage() + "]");
						 
				return null;
			} finally {
				log("Connection   ok --- maxConn [" + maxConn + "]" + " --- checkedOut [" + checkedOut + "]" 
				+ " --- pool name [" + name + "]" + " --- freeConnections.size ["+ freeConnections.size() + "]" 
				+ " --- con[" + con + "]" + " --- pool name [" + name + "]");
				
			logger.debug("================================newConnection==========================================");
			logger.debug("Connection  ok --- maxConn [" + maxConn + "]" + " --- checkedOut ["+ checkedOut + "]" 
						+ " --- pool name [" + name + "]"+ " --- freeConnections.size [" + freeConnections.size() + "]" 
						+ " --- con[" + con + "]" + " --- pool name [" + name + "]" + "\n");
			}
			return con;
		}
	
		public synchronized Connection getConnection()
		{
			Connection con = null;
			boolean validConn = true; // checkedOut 값을 한번만 증가시키기 위한 변수.
			Long accessTime;
	
			if ((freeConnections.size() > 0) && (checkedOut >= 0)) {
				// Pick the first Connection in the Vector
				// to get round-robin usage
				con = (Connection) freeConnections.firstElement();
				accessTime = (Long) lastAccessed.firstElement();
		
				freeConnections.removeElementAt(0);
				lastAccessed.removeElementAt(0);
	
				try {
					if (con.isClosed()) {
						log("Removed bad connection from " + name);
						validConn = false;
						// Try again recursively
						con = getConnection();
					} else if (isTimeOuted(accessTime)) {
						log("Removed TimeOuted connection from " + name);
						con.close();
						validConn = false;
						con = getConnection();
					}
				} catch (SQLException e) {
					log("SQL Exception Removed bad connection from " + name);
					// Try again recursively
					validConn = false;
					con = getConnection();
				}
			} else if (checkedOut < 0) {
				checkedOut = 0;
				release();
				con = newConnection();
			} else if ((maxConn == 0) || (checkedOut < maxConn)) {
				con = newConnection();
			} else if ((checkedOut == 0) && (freeConnections.size() == 0)) {
				con = newConnection();
			}
			
			if ((con != null) && (validConn == true)) {
				checkedOut++;
			}
			log("(+)getConnection: Clients [" + clients + "]" + " maxConn[" + maxConn + "]"
			  + " checkedOut[" + checkedOut + "]" + " pool name[" + name + "]"
			  + " freeConnections.size[" + freeConnections.size() + "]" + " con[" + con + "]");
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
					// Timeout has expired
					return null;
				}
			}
			return con;
		}
	
		private boolean isTimeOuted(Long accessTime)
		{
			long interval = System.currentTimeMillis() - accessTime.longValue();
			log("timeoutInterval [" + timeOutInterval + "] interval [" + interval + "]");
			return interval > timeOutInterval;
		}
	
		public synchronized void freeConnection(Connection con)
		{
			freeConnections.addElement(con);
			lastAccessed.addElement(new Long(System.currentTimeMillis()));
			checkedOut--;
			notifyAll();
			// release();
			// clients--;
			log("(-)freeConnection: Clients [" + clients + "]" + " maxConn[" + maxConn + "]"
			  + " checkedOut[" + checkedOut + "]" + " pool name[" + name + "]"
			  + " freeConnections.size[" + freeConnections.size() + "]" + " con[" + con + "]"
			  + " pool name[" + name + "]");
		}
	
		public synchronized void release()
		{
			Enumeration<Connection> allConnections = freeConnections.elements();
			while (allConnections.hasMoreElements()) {
				Connection con = (Connection) allConnections.nextElement();
				try {
					con.close();
					log("Closed connection for pool " + name);
				} catch (SQLException e) {
					log(e, "Can't close connection for pool " + name);
				}
			}
			freeConnections.removeAllElements();
			lastAccessed.removeAllElements();
	
			log("All connection released : Clients [" + clients + "]" + " maxConn[" + maxConn + "]"
			  + " checkedOut[" + checkedOut + "]" + " pool name[" + name + "]"
			  + " freeConnections.size[" + freeConnections.size() + "]");
		}
	}

	/**
	 * ***********************************************************************************************
	 * ************************************ end DBConnectionPool *************************************
	 * ***********************************************************************************************
	 */

	public DBConnectionManager() {
		init();
	}

	private void init()	
	{
		Properties dbProps = null;
		try {
		  kr.co.enders.batch.conf.Config config = new DBConfiguration();
		  dbProps = config.getProperties();
		}
		catch (Exception e) {
		  logger.error("Can't read the properties file. Make sure db.properties is in the CLASSPATH");
		  dbProps = new Properties();
		  return;
		}
		String logFile = dbProps.getProperty("logpath", "DBConnectionManager.log")
						 + DateTimeUtil.getShortDateString() + ".log";
		logger.debug("logfile : " + logFile);
	
		try {
		  log = new PrintWriter(new FileWriter(logFile, true), true);
		}
		catch (IOException e) {
		  logger.error("Can't open the log file: ".concat(String.valueOf(logFile)));
		  log = new PrintWriter(System.err);
		}
	
		loadDrivers(dbProps);
		createPools(dbProps);
	}

	private void loadDrivers(Properties props)
	{
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
		  String driverClassName = st.nextToken().trim();
		  try {
			Driver driver = (Driver) Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(driver);
			drivers.addElement(driver);
			log("Registered JDBC driver " + driverClassName);
		  }
		  catch (Exception e) {
			log("Can't register JDBC driver: " + driverClassName + ", Exception: " + e);
		  }
		}
	}

	private void createPools(Properties props)
	{
		try {
		  Enumeration<?> propNames = props.propertyNames();
		  while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			//db.url = jdbc:oracle:thin:@10.20.8.203:1521:F3CSM
			if (name.endsWith(".url")) {
			  String poolName = name.substring(0, name.lastIndexOf("."));
			  String url = props.getProperty(poolName + ".url");
			  if (url == null) {
				log("No URL specified for " + poolName);
				continue;
			  }
			  String user = props.getProperty(poolName + ".user");
			  String password = props.getProperty(poolName + ".password");
			  String enc = props.getProperty(poolName + ".encoding");
			  String maxconn = props.getProperty(poolName + ".maxconn", "0");
			  log(this + " user : " + user);
			  log(this + " password : " + password);
			  log(this + " encoding : " + enc);
			  log(this + " maxconn : " + maxconn);
			  //logger.debug("user=======> " + user);
			  //logger.debug("password=======> " + password);
	
			  // max connection
			  int max;
			  try {
				max = Integer.valueOf(maxconn).intValue();
			  }
			  catch (NumberFormatException e) {
				log("Invalid maxconn value " + maxconn + " for " + poolName);
				max = 0;
			  }
			  // init connection
			  String initconn = props.getProperty(poolName + ".initconn", "0");
			  int init;
			  try {
				init = Integer.valueOf(initconn).intValue();
			  }
			  catch (NumberFormatException e) {
				log("Invalid initconn value " + initconn + " for " + poolName);
				init = 0;
			  }
			  // timeout
			  // default 30 min
			  String timeout = props.getProperty(poolName + ".timeout", "1800000");
			  long tout;
			  try {
				tout = Long.valueOf(timeout).longValue();
			  }
			  catch (NumberFormatException e) {
				log("Invalid timeout value " + timeout + " for " + poolName);
				tout = 1800000;
			  }
	
			  log(this
				  + "Before ******* new DBConnectionPool(poolName, url, user, password, enc, max, init, tout) ");
			  DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, enc, max,
														   init, tout);
			  pools.put(poolName, pool);
			  log("Initialized pool " + poolName);
			}
		  }
		}
		catch (Exception e) {
		  log("Exception : " + e);
		}
	}

	public void freeConnection(String name, Connection con)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
		  log(this.getClass() + " freeConnection() in " + name);
		  pool.freeConnection(con);
		}
	}

	public Connection getConnection(String name)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
		  log(this.getClass() + " getConnection() in " + name);
		  return pool.getConnection();
		}
		else {
		  log(this.getClass() + " getConnection() FAILED!! " + name);
		}
		return null;
	}

	public Connection getConnection(String name, long time)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) { return pool.getConnection(time); }
		return null;
	}

	static synchronized public DBConnectionManager getInstance()
	{
		if (instance == null) {
		  instance = new DBConnectionManager();
		}
		clients++;
		return instance;
	}

	private void log(String msg)
	{
		log.println(new Date() + ": [" + pools.hashCode() + "] " + msg);
	}

	private void log(Throwable e, String msg)
	{
		log.println(new Date() + ": [" + pools.hashCode() + "] " + msg);
		e.printStackTrace(log);
	}

	public synchronized void release()
	{
		// Wait until called by the last client
		log("Mgr.release(): Clients [" + clients + "] ");
		if (--clients != 0) { return; }
	
		Enumeration<DBConnectionPool> allPools = pools.elements();
		while (allPools.hasMoreElements()) {
		  DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
		  pool.release();
		}
		Enumeration<Driver> allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
		  Driver driver = (Driver) allDrivers.nextElement();
		  try {
			DriverManager.deregisterDriver(driver);
			log("Deregistered JDBC driver " + driver.getClass().getName());
		  }
		  catch (SQLException e) {
			log(e, "Can't deregister JDBC driver: " + driver.getClass().getName());
		  }
		}
	}
}
