package kr.co.enders.batch.run.batch;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import kr.co.enders.batch.common.AbstractDbLogic;
import kr.co.enders.batch.common.BatchDBManager;
import kr.co.enders.batch.dbconn.SqlLoader;
import kr.co.enders.batch.utils.PropManager;


/**
 * 설명 : BATCH 호출처리
 * < history > 2022. 11. 17. 최초작성 
 */
public class DbConnTest extends AbstractDbLogic {
    private static final Logger       logger  = Logger.getLogger("DAILY");
    private static final DbConnTest dbLogic = new DbConnTest();
    
    public static boolean             isDebug = false;
    
    Timestamp curTimeStamp = null;
    
    private static Properties loadProperties() throws IOException {
        Properties pro = new Properties();
        String s_config = "config.properties";
        InputStream in = ClassLoader.getSystemResourceAsStream(s_config);
        if (in != null) {
            pro.load(in);
        }
        return pro;
    }
    
    private DbConnTest() {
    }
    
    public static DbConnTest getInstance() {
        return dbLogic;
    }
    
    protected void testDbConnBatch(String psOragnCd, String useDBType) throws Exception {  
        
        Date today = new Date(); 
        SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String workDateTime = dayFormat.format(today); 
        
        logger.info("  1.작업이 시작되었습니다[" + workDateTime + "]" );
        
        curTimeStamp = new Timestamp(System.currentTimeMillis());
        
        doProcess("testDbConnBatch", curTimeStamp,  workDateTime.substring(0, 8), "0", 0, "", "", useDBType);
        
        logger.info("  9.작업이 종료되었습니다[" + workDateTime + "]" );
    }      
   
    private void doProcess(String workTarget, Timestamp currentTimeStamp, String workYmd, String status , int workCnt, String errCode, String errDescription, String useDBType ) throws Exception{
         
        String selectSql = ""; 
        String recordCount = "0"; 
        
        Connection conn = null;     
        
        try{
            //conn = BatchDBManager.getMySqlConn();
            logger.info(" 사용DB : " +  useDBType);
            
            conn = BatchDBManager.getDBTypeConn(useDBType);
            selectSql = "SELECT COUNT(*) AS REC_CNT " ; 
            selectSql = selectSql + "FROM AXONEIS_USER";  
            
            recordCount = SqlLoader.oneSelectSql(selectSql, conn);
            logger.info("  조회결과 : " +  recordCount);
        } catch (ClassNotFoundException e) { 
            logger.error("  9.배치 처리 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error("  9.배치 처리 오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);    
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error("  9.배치 처리 오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error("  9.배치 처리 오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
    }
    
}
