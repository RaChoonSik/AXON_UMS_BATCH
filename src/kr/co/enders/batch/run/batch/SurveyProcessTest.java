package kr.co.enders.batch.run.batch;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import kr.co.enders.batch.common.AbstractDbLogic;
import kr.co.enders.batch.common.BatchDBManager;
import kr.co.enders.batch.dbconn.SqlLoader;
import kr.co.enders.batch.utils.DBManager;
import kr.co.enders.batch.utils.EncryptUtil;
import kr.co.enders.batch.utils.PageAuth;
import kr.co.enders.batch.utils.PropManager;
import kr.co.enders.batch.utils.StringUtil;

/**
 * 설명 : 설문조사 종료일 기준으로 상태 완료로 처리 
 * < history > 2023. 10. 16. 최초작성 
 */
public class SurveyProcessTest extends AbstractDbLogic {
    private static final Logger       logger  = Logger.getLogger("DAILY");
    private static final SurveyProcessTest dbLogic = new SurveyProcessTest();
    
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
    
    private SurveyProcessTest() {
    }
    
    public static SurveyProcessTest getInstance() {
        return dbLogic;
    }
    
    protected void doProcess() throws Exception {  
        
        Date today = new Date(); 
        SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String workDateTime = dayFormat.format(today); 
        
        
        logger.info("  1.작업이 시작되었습니다[" + workDateTime + "]" );
        
        logger.info("  2.설문조사 상태 완료(004)" );
        updateSurveyStatus(workDateTime); 
     // selectSurveyStatus(workDateTime);
        today = new Date();
        workDateTime = dayFormat.format(today); 
        logger.info("  9.작업이 종료되었습니다[" + workDateTime + "]" );
    }

    protected boolean updateSurveyStatus(String workDateTime) {
        boolean result = false;
        
        StringBuffer sbSql = null; 
        String updateSql   = null; 
        
        try { 
            
            sbSql = new StringBuffer();
            sbSql.append("UPDATE NEO_SURV_BASIC ")//.append("\n")
                 .append("SET PRGSS_STTS = '002'");  
            
            updateSql = sbSql.toString();
            
            logger.info(updateSql);
            
            Connection conn = DBManager.getOraConn();
             SqlLoader.execSql(updateSql, conn);
            
        } 
        catch (Exception e) { 
            logger.error(e+"\n");
        }
        return result;
    }
    
    
    protected boolean selectSurveyStatus(String workDateTime) {
        boolean result = false;
        
        StringBuffer sbSql = null; 
        String selSql   = null; 
        
        try { 
            
            sbSql = new StringBuffer();
            sbSql.append("SELECT COUNT(*) AS REC_CNT FROM NEO_SURV_BASIC ");  
            
            selSql = sbSql.toString();
            
            Connection conn = DBManager.getOraConn();
            String recordCount =  SqlLoader.oneSelectSql(selSql, conn);
            logger.info("======selectSurveyStatus : " + recordCount);
        } 
        catch (Exception e) { 
            logger.error(e+"\n");
        }
        return result;
    }
}
