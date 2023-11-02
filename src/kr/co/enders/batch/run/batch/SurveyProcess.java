package kr.co.enders.batch.run.batch;

import java.io.IOException;
import java.io.InputStream; 
import java.sql.Connection; 
import java.sql.Timestamp;
import java.text.SimpleDateFormat; 
import java.util.Date;  
import java.util.Properties; 

import org.apache.log4j.Logger;

import kr.co.enders.batch.common.AbstractDbLogic; 
import kr.co.enders.batch.dbconn.SqlLoader;
import kr.co.enders.batch.utils.DBManager;  

/**
 * 설명 : 설문조사 종료일 기준으로 상태 완료로 처리 
 * < history > 2023. 10. 16. 최초작성 
 */
public class SurveyProcess extends AbstractDbLogic {
	private static final Logger	   logger  = Logger.getLogger("DAILY");
	private static final SurveyProcess dbLogic = new SurveyProcess();
	
	public static boolean			 isDebug = false;
	
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
	
	private SurveyProcess() {
	}
	
	public static SurveyProcess getInstance() {
		return dbLogic;
	}
	
	protected void doProcess() throws Exception {  
		
		Date today = new Date(); 
		SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyyMMddHHmmss"); 
		String workDateTime = dayFormat.format(today); 
		
		
		logger.info("  1.작업이 시작되었습니다[" + workDateTime + "]" );
		
		logger.info("  2.설문조사 상태 업데이트" );
		updateSurveyStatus(workDateTime); 
		today = new Date();
		workDateTime = dayFormat.format(today); 
		logger.info("  9.작업이 종료되었습니다[" + workDateTime + "]" );
	}

	protected boolean updateSurveyStatus(String workDateTime) {
		boolean result = false;
		String procSurvey   = null; 
		
		try { 
			Connection conn = DBManager.getMSSqlConn();
			int nRet = 0;
			
			procSurvey = "{CALL SP_SCH_NEO_SURV_BASIC()}";
			nRet = SqlLoader.execProc(procSurvey, conn);

			if (nRet > -1) {
				result = true;
				logger.info("  8. 설문조사 상태 업데이트 배치작업이 성공하였습니다." );
			} else {
				logger.info("  8. 설문조사 상태 업데이트 배치작업이 실패하였습니다." );
			}
		} 
		catch (Exception e) { 
			logger.error(e+"\n");
		}
		return result;
	}
}
