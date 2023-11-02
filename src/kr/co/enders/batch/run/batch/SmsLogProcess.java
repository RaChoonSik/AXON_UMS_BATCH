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
 * 설명 : 문자로그 테이블 데이터 적재 작업 (neo_smslog to NEO_UMS_SMSLOG)   
 * < history > 2023. 10. 16. 최초작성 
 */
public class SmsLogProcess extends AbstractDbLogic {
    private static final Logger       logger  = Logger.getLogger("DAILY");
    private static final SmsLogProcess dbLogic = new SmsLogProcess();
    
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
    
    private SmsLogProcess() {
    }
    
    public static SmsLogProcess getInstance() {
        return dbLogic;
    }
    
    protected void doProcess() throws Exception {  
        
        Date today = new Date(); 
        SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String workDateTime = dayFormat.format(today); 
        
        
        logger.info("  1.작업이 시작되었습니다[" + workDateTime + "]" );
        
        logger.info("  2.smslog 배치작업" );
        smslogBatch(workDateTime);
        today = new Date();
        workDateTime = dayFormat.format(today); 
        logger.info("  9.작업이 종료되었습니다[" + workDateTime + "]" );
    }

    protected boolean smslogBatch(String workDateTime) {
        boolean result = false;
        String procSmsLog = null; 
        
        try {
            Connection conn = DBManager.getMSSqlConn();
            int nRet = 0;
            
            procSmsLog = "{call SP_NEO_UMS_SMSLOG()}";
            nRet = SqlLoader.execProc(procSmsLog, conn);

            if (nRet > -1) {
            	result = true;
            	logger.info("  8. smslog 배치작업이 성공하였습니다." );
            } else {
            	logger.info("  8. smslog 배치작업이 실패하였습니다." );
            }
        } 
        catch (Exception e) { 
            logger.error(e+"\n");
        }
        return result;
    }
}
