package kr.co.enders.batch.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import kr.co.enders.batch.utils.DBManager;

/**
 * =======================================================
 * 설명 : 저장 및 기타DB 접속용 공통처리
 * =======================================================
 */
public abstract class AbstractDbLogic {
	private static final Logger logger = Logger.getLogger(AbstractDbLogic.class);

	/**
	 * 범용 Insert / Update / Delete 처리로직
	 * @param psQuery 쿼리, psInja 쿼리에 보낼 인자값
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	protected void executeUpdate(String psQuery, String ... psInja) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBManager.getOraConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(psQuery);
			if (psInja != null) {
				for (int i = 0; i < psInja.length; i++) {
					pstmt.setString(i+1, psInja[i]);
				}
			}
			pstmt.executeUpdate();
			conn.commit();
		} catch (ClassNotFoundException e) {
			errorHandle(e, conn);
		} finally {
			close(pstmt, conn);
		}
	}
	
	/**
	 * 범용 Insert / Update / Delete 처리로직
	 * @param psQuery 쿼리
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	protected void executeUpdate(String psQuery) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBManager.getOraConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(psQuery);
			pstmt.executeUpdate();
			conn.commit();
		} catch (ClassNotFoundException e) {
			errorHandle(e, conn);
		} finally {
			close(pstmt, conn);
		}
	}
	
	/**
	 * 범용 SELECT / TRUNCATE 처리로직
	 * @param psQuery 쿼리
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	protected void executeQuery(String psQuery) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBManager.getOraConn();
			pstmt = conn.prepareStatement(psQuery);
			pstmt.executeQuery();
		} catch (ClassNotFoundException e) {
			errorHandle(e, conn);
		} finally {
			close(pstmt, conn);
		}
	}
	
	protected void executeUser(String psQuery) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBManager.getOraConn();
			pstmt = conn.prepareStatement(psQuery);
			pstmt.execute();
		} catch (ClassNotFoundException e) {
			errorHandle(e, conn);
		} finally {
			close(pstmt, conn);
		}
	}
	
	protected void executeBatch(String psQuery) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBManager.getOraBatchConn();
			pstmt = conn.prepareStatement(psQuery);
			pstmt.execute();
		} catch (ClassNotFoundException e) {
			errorHandle(e, conn);
		} finally {
			close(pstmt, conn);
		}
	}
	
	/**
	 * 범용 프로시저 처리로직
	 * @param psQuery 프로시저
	 * @param outParamType OutParameter의 유형 (Type클래스 이용 / OutParameter이 없을 경우 0 설정) 
	 * @param psInja 프로시저에 보낼 인자값
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	protected Object executeProcedure(String psQuery, int outParamType, String ... psInja) throws Exception {
		Connection conn = null;
		CallableStatement cstmt = null;
		Object rtn = null;
		try {
			
			 logger.info("[START]======================executeProcedure start==================" );
	       	 Class.forName("oracle.jdbc.driver.OracleDriver");
	       //	 conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "happy", "1111");
	       	conn = BatchDBManager.getOraLocalConn();
	       	
			cstmt = conn.prepareCall(psQuery);
			int i = 0;
			if (psInja != null) {
				for (;i < psInja.length; i++) {
					cstmt.setString(i+1, psInja[i]);
				}
			}
			if (outParamType != 0) {
				cstmt.registerOutParameter(i+1, outParamType);
			}
			cstmt.execute();
			if (outParamType != 0) {
				rtn = cstmt.getString(i+1);
			}
		} catch (ClassNotFoundException e) {
			logger.info("[FINISH]======================executeProcedure=============  exception =>" + e.getMessage() );
			errorHandle(e, conn);
		} finally {
			close(cstmt, conn);
		}
		return rtn;
	}
	
	protected Object executeBatchProcedure(String psQuery, int outParamType, String ... psInja) throws Exception {
		Connection conn = null;
		CallableStatement cstmt = null;
		Object rtn = null;
		try {
			conn = DBManager.getOraBatchConn();
			cstmt = conn.prepareCall(psQuery);
			int i = 0;
			if (psInja != null) {
				for (;i < psInja.length; i++) {
					cstmt.setString(i+1, psInja[i]);
				}
			}
			if (outParamType != 0) {
				cstmt.registerOutParameter(i+1, outParamType);
			}
			cstmt.execute();
			if (outParamType != 0) {
				rtn = cstmt.getString(i+1);
			}
		} catch (ClassNotFoundException e) {
			errorHandle(e, conn);
		} finally {
			close(cstmt, conn);
		}
		return rtn;
	}

	/**
	 * SQL처리 중 발생한 에러처리
	 * @param e
	 * @param conn
	 * @throws Exception 
	 */
	protected void errorHandle(Exception e, Connection ... conns) throws Exception {
		if (e instanceof ClassNotFoundException) {
			logger.error("DB처리 중 예외가 발생하였습니다. (SQL드라이버 관련오류)", e);
		} else if (e instanceof SQLException) {
			logger.error("DB처리 중 예외가 발생하였습니다. (SQL실행오류)", e);
			if (conns != null) {
				for (Connection conn : conns) {
					rollback(conn);
				}
			}
		}
		throw e;
	}
	
	/**
	 * 롤백을 위한 공통처리
	 * @param conn
	 */
	protected void rollback(Connection conn) {
		try {
			if (conn != null) conn.rollback();
		} catch (SQLException e1) {
			logger.error("롤백 처리중 예외가 발생하였습니다.", e1);
		}
	}
	
	/**
	 * PreparedStatement 및 Connection Close를 위한 공통처리
	 * @param pstmt
	 * @param conn
	 */
	protected void close(PreparedStatement pstmt, Connection conn) {
		close(pstmt, conn, null);
	}
	
	/**
	 * PreparedStatement 및 Connection Close를 위한 공통처리
	 * pstmt 복수건 처리가능
	 * @param pstmts 
	 * @param conn
	 */
	protected void close(Connection conn, PreparedStatement ... pstmts) {
		try {
			for (PreparedStatement pstmt : pstmts) {
				if (pstmt != null) pstmt.close();
			}
			if (conn != null) conn.close();
		} catch (SQLException e) {
			logger.error("PreparedStatement 혹은 Connection Close처리중 예외가 발생하였습니다.", e);
		}
	}
		
	/**
	 * PreparedStatement 및 ResultSet, Connection Close를 위한 공통처리
	 * @param pstmt
	 * @param conn
	 * @param rs
	 */
	protected void close(PreparedStatement pstmt, Connection conn, ResultSet rs) {
		try {
			if (rs != null) rs.close();
			if (pstmt != null) pstmt.close();
			if (conn != null) conn.close();
		} catch (SQLException e) {
			logger.error("PreparedStatement 혹은 Connection Close처리중 예외가 발생하였습니다.", e);
		}
	}
}
