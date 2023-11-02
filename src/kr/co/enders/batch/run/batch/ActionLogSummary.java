package kr.co.enders.batch.run.batch;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import kr.co.enders.batch.common.AbstractDbLogic;
import kr.co.enders.batch.common.BatchDBManager;
import kr.co.enders.batch.utils.PropManager;

/**
 * 설명 : BATCH PROC_LOGON_LOG_CLS;
 * < history > 2022. 11. 17. 최초작성 
 */
public class ActionLogSummary extends AbstractDbLogic {
    private static final Logger       logger  = Logger.getLogger("DAILY");
    private static final ActionLogSummary dbLogic = new ActionLogSummary();
    
    public static boolean             isDebug = false;
    
    Timestamp curTimeStamp = null;
    
    private String useDBType = "MYSQL";
    
    private static Properties loadProperties() throws IOException {
        Properties pro = new Properties();
        String s_config = "config.properties";
        InputStream in = ClassLoader.getSystemResourceAsStream(s_config);
        if (in != null) {
            pro.load(in);
        }
        return pro;
    }
    
    private ActionLogSummary() {
    }
    
    public static ActionLogSummary getInstance() {
        return dbLogic;
    }
    
    protected void doProcess(String psUseDBType) throws Exception {  
        
        Date today = new Date(); 
        SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String workDateTime = dayFormat.format(today); 
        
        useDBType = psUseDBType;
        
        logger.info("  1.작업이 시작되었습니다[" + workDateTime + "]" );
        
        today = new Date();
        workDateTime = dayFormat.format(today); 
        
        int result = 0 ;
        logger.info("  2-1.로그 집계 작업이 시작되었습니다[" + workDateTime + "]" );
        result = logonSummary();
        
        today = new Date();
        workDateTime = dayFormat.format(today);
        if (result > 0 ) {
            logger.info("  2-9.로그 집계 작업이 성공하였습니다[" + workDateTime + "]" );
        } else {
            logger.info("  2-9.로그 집계 작업이 실패하였습니다[" + workDateTime + "]" );
        }
        
        today = new Date();
        workDateTime = dayFormat.format(today);
        
        logger.info("  3-1.보고서 집계 작업이 시작되었습니다[" + workDateTime + "]" );
        result =  reportTimeSummary();
        
        today = new Date();
        workDateTime = dayFormat.format(today);
        if (result  > 0 ) {
            logger.info("  3-9.보고서 집계 작업이 성공하였습니다[" + workDateTime + "]" );
        } else {
            logger.info("  3-9.보고서 집계 작업이 실패하였습니다[" + workDateTime + "]" );
        }
        
        logger.info("  9.작업이 종료되었습니다[" + workDateTime + "]" );
    }
    
    protected int logonSummary ( ) throws Exception { 
        Connection conn = null;
        CallableStatement cs = null ;
        
        int returnValue = 0;
        
        try{
            logger.info("  2-2.로그 집계 작업  axoneis.PROC_LOGON_LOG_CLS()" );
            //conn = BatchDBManager.getMySqlConn();
            conn = BatchDBManager.getDBTypeConn(useDBType);
            conn.setAutoCommit(false);
            //cs = conn.prepareCall("{call axoneis.PROC_LOGON_LOG_CLS()}"); 
            cs = conn.prepareCall("{call PROC_LOGON_LOG_CLS()}");
            cs.execute();
            conn.commit();
            returnValue = 1;
        } catch (ClassNotFoundException e) { 
            logger.error(" 2-8.배치 처리 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 2-8.배치 처리 오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);   
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 2-8.배치 처리 오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 2-8.배치 처리 오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
         
        return returnValue;
    }
    
    protected int reportTimeSummary ( ) throws Exception { 
        Connection conn = null;
        CallableStatement cs = null ;
        
        int returnValue = 0;
        
        try{
            logger.info("  3-2.로그 집계 작업  axoneis.PROC_REPORT_LOG_CLS()" );
            //conn = BatchDBManager.getMySqlConn();
            conn = BatchDBManager.getDBTypeConn(useDBType);
            conn.setAutoCommit(false);
            //cs = conn.prepareCall("{call axoneis.PROC_REPORT_LOG_CLS()}"); 
            cs = conn.prepareCall("{call PROC_REPORT_LOG_CLS()}");
            cs.execute();
            conn.commit();
            returnValue = 1;
        } catch (ClassNotFoundException e) { 
            logger.error(" 3-8.보고서 집계 처리 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 3-8.보고서 집계 처리 오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);   
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 3-8.보고서 집계 처리 오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 3-8.보고서 집계 처리 오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
        return returnValue; 
    }
}
