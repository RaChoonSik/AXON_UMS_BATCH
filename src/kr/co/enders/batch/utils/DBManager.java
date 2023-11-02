package kr.co.enders.batch.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kr.co.enders.batch.crypto.seed.Encryption;

/**
 * =======================================================
 * 설명 : DB접속 처리용 유틸
 * =======================================================
 */
public class DBManager {
/** =========================== ORACLE  ============================== */
	public static Connection getOraConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {		 
		return getConn(PropManager.getDBStrValue("ORA_DRV_NAME"),
				PropManager.getDBStrValue("ORA_JDBC_PATH"),				 
				PropManager.getDBStrValue("ORA_USER"),
				PropManager.getDBStrValue("ORA_PW"));
	}

	public static Connection getSMSConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {	
		return getConn(PropManager.getDBStrValue("ORA_DRV_NAME"),
				PropManager.getDBStrValue("SMS_JDBC_PATH"), 	 
				Encryption.decodeMessage(PropManager.getAccStr("SMS_USER")),
				Encryption.decodeMessage(PropManager.getAccStr("SMS_PW")));
	}
	
	public static Connection getOraBatchConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConn(PropManager.getDBStrValue("ORA_DRV_NAME"),
				PropManager.getDBStrValue("ORA_JDBC_PATH"),
				Encryption.decodeMessage(PropManager.getAccStr("ORA_BATCH_USER")),
				Encryption.decodeMessage(PropManager.getAccStr("ORA_BATCH_PW")));
	}
	
	public static Connection getOraBatchConnA() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConnA(PropManager.getDBStrValue("ORA_DRV_NAME"),
				PropManager.getDBStrValue("ORA_JDBC_PATH"),
				Encryption.decodeMessage(PropManager.getAccStr("ORA_BATCH_USER")),
				Encryption.decodeMessage(PropManager.getAccStr("ORA_BATCH_PW")));
	}
	 
	public static Connection getOraMasterConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConnA(PropManager.getDBStrValue("ORA_DRV_NAME"),
				PropManager.getDBStrValue("ORA_JDBC_MASTER_PATH"),
				PropManager.getDBStrValue("ORA_MASTER_USER"),
				PropManager.getDBStrValue("ORA_MASTER_PW"));
	} 
	 
	public static Connection getOraLocalConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConnA(PropManager.getDBStrValue("ORA_DRV_NAME"),
				PropManager.getDBStrValue("ORA_JDBC_LOCAL_PATH"),
				PropManager.getDBStrValue("ORA_LOCAL_USER"),
				PropManager.getDBStrValue("ORA_LOCAL_PW"));
	}
	
	/** =========================== MySql  ============================== */
	public static Connection getMySqlConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConn(PropManager.getDBStrValue("MYSQL_DRV_NAME"),
				PropManager.getDBStrValue("MYSQL_JDBC_PATH"),
				PropManager.getDBStrValue("MYSQL_USER"),
				PropManager.getDBStrValue("MYSQL_PW"));
	}

	/** =========================== MSSql  ============================== */
	public static Connection getMSSqlConn() throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConn(PropManager.getDBStrValue("MSSQL_DRV_NAME"),
				PropManager.getDBStrValue("MSSQL_JDBC_PATH"),
				PropManager.getDBStrValue("MSSQL_USER"),
				PropManager.getDBStrValue("MSSQL_PW"));
	}

	/** =========================== Direct  ============================== */
	public static Connection getDBTypeConn(String dbType) throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		Connection conn = null;
		switch(dbType) {
			case "MSSQL":
				conn = getMSSqlConn();
				break;
			case "MYSQL":
				conn = getMySqlConn();
				break;
			case "ORACLE":
				conn = getOraConn();
				break;
			default:
				conn = null;
		}
		return conn;
	}
	
	public static Connection getDBConn(String drvName, String jdbcPath, String user, String pw) throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
		return getConn(drvName,jdbcPath, user, pw);
	}
	 
	protected static Connection getConn(String drvName, String jdbcPath, String user, String pw) throws SQLException, ClassNotFoundException {
		Connection conn = null;
		Class.forName(drvName);
		conn = DriverManager.getConnection(jdbcPath, user, pw);
		conn.setAutoCommit(false);
		return conn;
	}
	
	protected static Connection getConnA(
		String drvName, String jdbcPath, String user, String pw) throws SQLException, ClassNotFoundException {
		Connection conn = null;
		Class.forName(drvName);
		conn = DriverManager.getConnection(jdbcPath, user, pw);
		conn.setAutoCommit(false);
		return conn;
	}
	
	public static void main(String[] args)   throws IOException, ClassNotFoundException, SQLException {
		Connection conn = null;
		String drvName = "oracle.jdbc.driver.OracleDriver";
		String jdbcPath = "jdbc:oracle:thin:@localhost:1521:orcl"; 
		
		String user = "showcard"; 
		String pw = "p_showcard_w"; 
		
		Class.forName(drvName);
		conn = DriverManager.getConnection(jdbcPath, user, pw);
		System.out.println(conn); 
		
	}
}
