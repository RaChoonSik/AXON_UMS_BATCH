package kr.co.enders.batch.run.batch;

import org.apache.log4j.Logger;

import kr.co.enders.batch.utils.DateUtil;
import kr.co.enders.batch.utils.PropManager;

/**
 * MAIN EXECUTE
 */
public class BatchAction {
	
	private static final Logger logger = Logger.getLogger(BatchAction.class);
	
	/**
	 * ■ 처리흐름 각 인자에 따라 해당 SP 호출
	 * 
	 * @param workDate
	 * @throws Exception
	 */	
	private static void callBatch(String psBatchGubun, String psAddCond) throws Exception {
		String errMsg = "";
		
		try {
			
			String useDBType =PropManager.getDBStrValue("USE_DB"); 
			String targetDBInfo="";
			switch(useDBType) {
			case "MSSQL":
				targetDBInfo = "Type: " + useDBType + " / Driver: " + PropManager.getDBStrValue("MSSQL_DRV_NAME") + "/Path : " + PropManager.getDBStrValue("MSSQL_JDBC_PATH");
				break;
			case "ORACLE":
				targetDBInfo = "Type: " + useDBType + " / Driver: " + PropManager.getDBStrValue("ORA_DRV_NAME") + "/Path : " + PropManager.getDBStrValue("ORA_JDBC_PATH");
				break;
			default:
				targetDBInfo = "Type: " + useDBType + " / Driver: " + PropManager.getDBStrValue("MYSQL_DRV_NAME") + "/Path : " + PropManager.getDBStrValue("MYSQL_JDBC_PATH");
				break;
			}
			
			logger.info("========== [DB Conn Info]" + targetDBInfo);
			
			if (psBatchGubun.equals("Conn")) {
				logger.info("========== [DB Conn Test]................");
				DbConnTest.getInstance().testDbConnBatch(psAddCond, useDBType);
			} else if (psBatchGubun.equals("Log")) {
				logger.info("========== [Log Proecedure]................");
				ActionLogSummary.getInstance().doProcess(useDBType);
			} else if (psBatchGubun.equals("Sche")) {
				logger.info("========== [Schedule Process]................");
				ScheduleProcess.getInstance().doProcess(useDBType);
			} else if (psBatchGubun.equals("Survey")) { //설문조사 상태 변경 
				logger.info("========== [Survey Process]................");
				SurveyProcess.getInstance().doProcess();
			} else if (psBatchGubun.equals("SmsLog")) { //smslog 배치작업 
				logger.info("========== [SmsLog Process]................");
				SmsLogProcess.getInstance().doProcess();
			} else if (psBatchGubun.equals("Test")) {
				logger.info("========== [테스트]................");
			} else {
				logger.info("========== [Nothing...]................");
			}
			
		} catch (Exception e) {
			errMsg = e.getMessage();
			if (errMsg.length() > 1500) {
				errMsg = errMsg.substring(0, 1500);
			}
			logger.error("========== [예외오류]+ " + errMsg + ":" ,e);
		}
	}

    /**
     * ■ 처리흐름 1. 배치처리 날짜 설정 2. 배치 메인처리 호출
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
		String sBatchGubun = "";
		String sAddCond = "";
		String sAddOption = "";
		String workDate = null; 
		
		if (args.length == 1 ) {
			sBatchGubun = args[0];
		} else if(args.length == 2) {
			sBatchGubun = args[0];
			sAddCond = args[1];
		} else if(args.length == 3) {
			sBatchGubun = args[0];
			sAddCond = args[1];
			sAddOption = args[2];
		}	
		else { 
			sBatchGubun = "BATCH_TEST";
		}
		
		workDate = DateUtil.simpleGetCurrentDate("yyyy-MM-dd");
		logger.info("=====================================================================");
		logger.info("========== 배치처리 시작 [처리일:" + workDate + " 구분: " + sBatchGubun + "]===");
		logger.info(" [DB:" + PropManager.getDBStrValue("MSSQL_JDBC_PATH"));
		logger.info("=====================================================================");
		
		if ( sBatchGubun == "BATCH_TEST"){
			logger.info("======BATCH_TEST=====================================================");
		}
		else {
			try {			
				callBatch(sBatchGubun, sAddCond);
			} catch (Exception e) {					   
				logger.error("========== 배치처리 종료 [처리일:" + workDate + " 구분:" + sBatchGubun + "]예외오류발생 : ",e);
				System.exit(1);
			}	
		}
		
		logger.info("=====================================================================");
		logger.info("========== 배치처리 종료[처리일:" + workDate + " 구분: " + sBatchGubun + "]====");
		logger.info("========== [BATCH : " + sBatchGubun + "]==============================");
		logger.info("======================================================================");
	}
}