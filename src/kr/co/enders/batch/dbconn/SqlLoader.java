package kr.co.enders.batch.dbconn;

import kr.co.enders.batch.exceptions.DBException;
import kr.co.enders.batch.utils.StrConvertUtil;
import kr.co.enders.batch.utils.StringUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

/*
 * sql을 받아서 실행후 vector나 exception throw 시킨다. 
 * UPDATE LIST 2003.2.20 Clob을 이용한 처리.
 */
public class SqlLoader
{
  /**
   * 로거 정의
   */
  static Logger logger = Logger.getLogger(SqlLoader.class);

  public SqlLoader() {
  }

  /**
   * <pre>
   * sql실행한 결과값을 반환한다.
   * sql문장은 count값을 구하는 내용이다.
   * 
   * @param String Sql
   * @return int
   */
  public static int countSelectSql(String Sql) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int count = 0;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection("db");
      pstmt = conn.prepareStatement(Sql);
      rs = pstmt.executeQuery();
      try {
        rs.next();
        count = rs.getInt(1);
      }
      catch (Exception x) {
        count = 0;
      }
      return count;
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * sql실행한 결과값을 반환한다.
   * sql문장은 특정값을 구하는 내용이다.
   * 
   * @param String Sql
   * @return String
   */
  public static String oneSelectSql(String Sql) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String temp = null;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection("db");
      pstmt = conn.prepareStatement(Sql);
      rs = pstmt.executeQuery();
      try {
        rs.next();
        temp = nullCheck(rs.getString(1));
      }
      catch (Exception x) {
        temp = "";
      }
      //System.out.println("[CLS][SqlLoader:oneSelectSql]" + temp);
      return temp;
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * sql실행한 결과값을 반환한다.
   * sql문장은 특정값을 구하는 내용이다.
   * 
   * @param String Sql
   *        String conName
   * @return String
   */
  public static String oneSelectSql(String Sql, String conName) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String temp = null;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection(conName);
      pstmt = conn.prepareStatement(Sql);
      rs = pstmt.executeQuery();
      try {
        rs.next();
        temp = nullCheck(rs.getString(1));
      }
      catch (Exception x) {
        temp = "";
      }
      return temp;
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * sql실행한 결과값을 반환한다.
   * sql문장은 특정값을 구하는 내용이다.
   * 
   * @param String Sql
   *        Connection conn
   * @return String
   */
  public static int intSelectSql(String Sql, Connection conn) throws DBException, Exception
  {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int count = 0;
    try {
      pstmt = conn.prepareStatement(Sql);
      rs = pstmt.executeQuery();
      try {
        rs.next();
        count = rs.getInt(1);
      }
      catch (Exception x) {
        count = -1;
      }
      return count;
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      close(pstmt, rs);
    }
  }

  public static String oneSelectSql(String Sql, Connection conn) throws DBException, Exception
  {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String temp = null;
    try {
      pstmt = conn.prepareStatement(Sql);
      rs = pstmt.executeQuery();
      try {
        rs.next();
        temp = nullCheck(rs.getString(1));
      }
      catch (Exception x) {
        temp = "";
      }
      return temp;
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      close(pstmt, rs);
    }
  }

  /**
   * <pre>
   * sql실행한 결과값을 반환한다.
   * 
   * @param String Sql
   * @return String
   */
  public static Vector<HashMap<String, String>> selectSql(String Sql) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;
    Vector<HashMap<String, String>> vResult = new Vector<HashMap<String, String>>();
    HashMap<String, String> hResult = null;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection("db");

      // Logger.sys.println("[SqlLoader][selectSql][Sql]====>"+Sql);
      // Logger.sys.println("[SqlLoader][selectSql][csql]====>"+CharacterConvert.us2kr(Sql));

      String sql = Sql;

      // 한글이 깨질수도 있으므로 경우에따라서서는 유니코드 변환이 필요할수 도 있음.
      // Logger.sys.println("[SqlLoader][selectSql][sql]====>"+sql);
      // Logger.sys.println("[SqlLoader][selectSql][csql]====>"+CharacterConvert.us2kr(sql));

      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();

      vResult.removeAllElements();
      rsmd = rs.getMetaData();
      while (rs.next()) {
        hResult = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rs.getString(rsmd.getColumnName(i)) == null) {
            hResult.put(rsmd.getColumnName(i), "");
          }
          else {
            hResult.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
          }
        }
        vResult.addElement(hResult);
      }
      /*
       * vResult1.removeAllElements();
       * while (rs.next()) {
       * Vector vResult2 = new Vector();
       * for (int i = 0; i < filed; i++) {
       * String temp = "";
       * temp = nullCheck(rs.getString(i + 1));
       * Logger.sys.println("[SqlLoader][selectSql][temp]"+temp);
       * vResult2.addElement(temp);
       * }
       * vResult1.addElement(vResult2);
       * }
       */
      return vResult;
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  public static Vector<HashMap<String, String>> selectSql(String Sql, String conName)
      throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;
    Vector<HashMap<String, String>> vResult = new Vector<HashMap<String, String>>();
    HashMap<String, String> hResult = null;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection(conName);

      String sql = Sql;

      // Logger.sys.println("==========================================================");
      // Logger.sys.println("[SQL][SqlLoader][selectSql]====>"+sql);
      // Logger.sys.println("==========================================================");

      // 한글이 깨질수도 있으므로 경우에따라서서는 유니코드 변환이 필요할수 도 있음.
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      rsmd = rs.getMetaData();
      vResult.removeAllElements();
      while (rs.next()) {
        hResult = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rs.getString(rsmd.getColumnName(i)) == null) {
            hResult.put(rsmd.getColumnName(i), "");
          }
          else {
            hResult.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
          }
        }
        vResult.addElement(hResult);
      }
      return vResult;
      /*
       * vResult1.removeAllElements();
       * while (rs.next()) {
       * Vector vResult2 = new Vector();
       * for (int i = 0; i < filed; i++) {
       * String temp = "";
       * temp = rs.getString(i + 1);
       * vResult2.addElement(nullCheck(temp));
       * }
       * vResult1.addElement(vResult2);
       * }
       * return vResult1;
       */
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  public static Vector<HashMap<String, String>> selectSql(String Sql, Connection conn)
      throws DBException, Exception
  {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd = null;
    Vector<HashMap<String, String>> vResult = new Vector<HashMap<String, String>>();
    HashMap<String, String> hResult = null;
    try {
      String sql = Sql;
      // 한글이 깨질수도 있으므로 경우에따라서서는 유니코드 변환이 필요할수 도 있음.
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      rsmd = rs.getMetaData();
      vResult.removeAllElements();
      while (rs.next()) {
        hResult = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rs.getString(rsmd.getColumnName(i)) == null) {
            hResult.put(rsmd.getColumnName(i), "");
          }
          else {
            hResult.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
          }
        }
        vResult.addElement(hResult);
      }
      return vResult;
      /*
       * vResult1.removeAllElements();
       * while (rs.next()) {
       * Vector vResult2 = new Vector();
       * for (int i = 0; i < filed; i++) {
       * String temp = "";
       * temp = rs.getString(i + 1);
       * vResult2.addElement(nullCheck(temp));
       * }
       * vResult1.addElement(vResult2);
       * }
       * return vResult1;
       */
    }
    catch (SQLException s) {
      throw new DBException(s.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      close(pstmt, rs);
    }
  }

  public static void execSql(String Sql) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection("db");
      // setAutoCommit의 경우 로컬일 경우에는 아래구문을 주석처리하고
      // 만약 뷰나 sysnonym일 경우에는 여기서 conn.commit()을 때려주어야 한다.
      // 이유는 확실하지 않다.
      conn.setAutoCommit(false);
      String sql = StrConvertUtil.E2K(Sql);
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
      pstmt.close();
      conn.commit();
      conn.setAutoCommit(true);
    }
    catch (SQLException e) {
      try {
        conn.rollback();
      }
      catch (SQLException se) {
        throw new DBException(e.toString());
      }
      throw new DBException(e.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  public static void execSql(String Sql, String conName) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection(conName);
      String sql = Sql;

      // Logger.sys.println("==========================================================");
      // Logger.sys.println("[SQL][SqlLoader][selectSql]====>"+sql);
      // Logger.sys.println("==========================================================");

      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
      pstmt.close();
    }
    catch (SQLException e) {
      throw new DBException(e.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
  }

  public static int execSqlReInt(String Sql) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    int result = 0;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection("db");
      // setAutoCommit의 경우 로컬일 경우에는 아래구문을 주석처리하고
      // 만약 뷰나 sysnonym일 경우에는 여기서 conn.commit()을 때려주어야 한다.
      // 이유는 확실하지 않다.
      conn.setAutoCommit(false);

      String sql = Sql;
      pstmt = conn.prepareStatement(sql);
      result = pstmt.executeUpdate();
      pstmt.close();

      conn.commit();
      conn.setAutoCommit(true);
    }
    catch (SQLException e) {
      try {
        conn.rollback();
      }
      catch (SQLException se) {
        throw new DBException(e.toString());
      }
      throw new DBException(e.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
    return result;
  }

  public static int execSqlint(String Sql, String conName) throws DBException, Exception
  {
    DBConnectionManager connMgr = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    int rows = 0;
    try {
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection(conName);
      String sql = StrConvertUtil.E2K(Sql);
      pstmt = conn.prepareStatement(sql);
      rows = pstmt.executeUpdate();
      pstmt.close();
    }
    catch (SQLException e) {
      throw new DBException(e.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      try {
        if (pstmt != null) {
          pstmt.close();
        }
      }
      catch (Exception e) {
      }
      try {
        if (conn != null) {
          connMgr.freeConnection("db", conn);
        }
      }
      catch (Exception e) {
      }
    }
    return rows;
  }

  public static void execSql(String Sql, Connection conn) throws DBException, Exception
  {
    PreparedStatement pstmt = null;
    try {
      String sql = Sql;
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
      pstmt.close();
    }
    catch (SQLException e) {
      throw new DBException(e.toString());
    }
    catch (Exception e) {
      throw new Exception(e.toString());
    }
    finally {
      close(pstmt);
    }
  }
  

  public static int execProc(String Sql, Connection conn) throws DBException, Exception
  {
	CallableStatement cstmt = conn.prepareCall(Sql);
	int nRet = 0;
    try {
      cstmt.executeUpdate();
    }
    catch (SQLException e) {
      throw new DBException(e.toString());
    }
    catch (Exception e) {
      nRet  = -1;
      throw new Exception(e.toString());
    }
    finally {
    cstmt.close();
    }
    return nRet;
  }

  public static Vector<HashMap<String, String>> callOracleStoredProcCURSORParameter(String procName,
      String where, String[] columns, String[] columnType)
      throws SQLException {

    DBConnectionManager connMgr = null;
    Connection conn = null;
    CallableStatement callableStatement = null;
    ResultSet rs = null;
    StringBuffer getCursorSql = null; //"{call getDBUSERCursor(?,?)}";
    Vector<HashMap<String, String>> vResult = new Vector<HashMap<String, String>>();
    HashMap<String, String> hResult = null;

    try {
      //DB Connection
      connMgr = DBConnectionManager.getInstance();
      conn = connMgr.getConnection("db");
      //Procedure Call
      getCursorSql = new StringBuffer();
      getCursorSql.append("{call ").append(procName).append("(?,?)}");
      callableStatement = conn.prepareCall(getCursorSql.toString());
      callableStatement.setString(1, where);
      //Get Cursor back
      callableStatement.registerOutParameter(2, OracleTypes.CURSOR);
      // execute getDBUSERCursor store procedure
      callableStatement.execute();
      // get cursor and cast it to ResultSet
      rs = (ResultSet) callableStatement.getObject(2);
      vResult.removeAllElements();
      String tmpVal = null;
      while (rs.next()) {
        hResult = new HashMap<String, String>();
        for (int i = 0; i < columns.length; i++) {
          //decide get method based on data type
          if (columnType[i].equals("STR")) tmpVal = rs.getString(columns[i]);
          else if (columnType[i].equals("NUM")) tmpVal = "" + rs.getInt(columns[i]);
          //Get data
          if (tmpVal == null) hResult.put(columns[i], "");
          else hResult.put(columns[i], tmpVal);
          //logger.debug("Column Name==[" + columns[i] + "] / Column Type==[" + columnType[i] + "] / tmpVal==["
          //    + tmpVal + "]");
        }
        vResult.addElement(hResult);
      }
      logger.debug("vResult counts ==[" + vResult.size() + "]");
    }
    catch (SQLException e) {
      logger.error(e);
      System.out.println(e);
    }
    finally {
      if (rs != null) {
        rs.close();
      }
      if (callableStatement != null) {
        callableStatement.close();
      }
      if (conn != null) {
        connMgr.freeConnection("db", conn);
      }
    }
    return vResult;
  }

  public static String nullCheck(String param)
  {
    String re = "";
    if (param == null) { return re; }
    // re = StringUtility.replaceStr(param,"''","'");
    re = param;
    // return CharacterConvert.E2K(re);
    return re;
  }

  /**
   * <pre>
   * connection, PreparedStatement, ResultSet 객체를 반환한다.
   * 
   * @param Connection con
   * @param PreparedStatement ps
   * @param ResultSet rs
   * @return void
   */
  public static void close(Connection con, PreparedStatement ps, ResultSet rs)
  {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (Exception e) {
      }
    }
    if (ps != null) {
      try {
        ps.close();
      }
      catch (Exception e) {
      }
    }
    if (con != null) {
      try {
        con.close();
      }
      catch (Exception e) {
        logger.error(StringUtil.stackTraceToString(e));
      }
    }
  }

  /**
   * <pre>
   * connection, Statement, ResultSet 객체를 반환한다.
   * 
   * @param Connection con
   * @param Statement stmt
   * @param ResultSet rs
   * @return void
   */
  public static void close(Connection con, Statement stmt, ResultSet rs)
  {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (Exception e) {
      }
    }
    if (stmt != null) {
      try {
        stmt.close();
      }
      catch (Exception e) {
      }
    }
    if (con != null) {
      try {
        con.close();
      }
      catch (Exception e) {
        logger.error(StringUtil.stackTraceToString(e));
      }
    }
  }

  /**
   * <pre>
   * connection, PreparedStatement 객체를 반환한다.
   * 
   * @param Connection con
   * @param PreparedStatement stmt
   * @return void
   */
  public static void close(Connection con, PreparedStatement ps)
  {
    if (ps != null) {
      try {
        ps.close();
      }
      catch (Exception e) {
      }
    }
    if (con != null) {
      try {
        con.close();
      }
      catch (Exception e) {
        logger.error(StringUtil.stackTraceToString(e));
      }
    }
  }

  /**
   * <pre>
   * connection, Statement 객체를 반환한다.
   * 
   * @param Connection con
   * @param Statement ps
   * @return void
   */
  public static void close(Connection con, Statement stmt)
  {
    if (stmt != null) {
      try {
        stmt.close();
      }
      catch (Exception e) {
      }
    }
    if (con != null) {
      try {
        con.close();
      }
      catch (Exception e) {
        logger.error(StringUtil.stackTraceToString(e));
      }
    }
  }

  /**
   * <pre>
   * connection, ResultSet 객체를 반환한다.
   * 
   * @param Connection con
   * @param ResultSet rs
   * @return void
   */
  public static void close(Connection con, ResultSet rs)
  {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (Exception e) {
      }
    }
    if (con != null) {
      try {
        con.close();
      }
      catch (Exception e) {
        logger.error(StringUtil.stackTraceToString(e));
      }
    }
  }

  /**
   * <pre>
   * preparedStatement, ResultSet 객체를 반환한다.
   * 
   * @param PreparedStatement ps
   * @param ResultSet rs
   * @return void
   */
  public static void close(PreparedStatement ps, ResultSet rs)
  {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (Exception e) {
      }
    }
    if (ps != null) {
      try {
        ps.close();
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * statement, ResultSet 객체를 반환한다.
   * 
   * @param Statement stmt
   * @param ResultSet rs
   * @return void
   */
  public static void close(Statement stmt, ResultSet rs)
  {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (Exception e) {
      }
    }
    if (stmt != null) {
      try {
        stmt.close();
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * connection 객체를 반환한다.
   * 
   * @param Connection con
   * @return void
   */
  public static void close(Connection con)
  {
    if (con != null) {
      try {
        con.close();
      }
      catch (Exception e) {
        logger.error(StringUtil.stackTraceToString(e));
      }
    }
  }

  /**
   * <pre>
   * preparedStatement 객체를 반환한다.
   * 
   * @param PreparedStatement ps
   * @return void
   */
  public static void close(PreparedStatement ps)
  {
    if (ps != null) {
      try {
        ps.close();
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * statement 객체를 반환한다.
   * 
   * @param Statement stmt
   * @return void
   */
  public static void close(Statement stmt)
  {
    if (stmt != null) {
      try {
        stmt.close();
      }
      catch (Exception e) {
      }
    }
  }

  /**
   * <pre>
   * resultSet 객체를 반환한다.
   * 
   * @param ResultSet rs
   * @return void
   */
  public static void close(ResultSet rs)
  {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (Exception e) {
      }
    }
  }

}
