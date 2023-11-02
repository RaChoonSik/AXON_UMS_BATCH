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
import kr.co.enders.batch.utils.EncryptUtil;
import kr.co.enders.batch.utils.PageAuth;
import kr.co.enders.batch.utils.PropManager;
import kr.co.enders.batch.utils.StringUtil;

/**
 * 설명 : Send Mail By Schedule
 * < history > 2022. 11. 17. 최초작성 
 */
public class ScheduleProcess extends AbstractDbLogic {
    private static final Logger       logger  = Logger.getLogger("DAILY");
    private static final ScheduleProcess dbLogic = new ScheduleProcess();
    
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
    
    private ScheduleProcess() {
    }
    
    public static ScheduleProcess getInstance() {
        return dbLogic;
    }
    
    protected void doProcess(String psUseDBType) throws Exception {  
        
        Date today = new Date(); 
        SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyyMMddHHmmss"); 
        String workDateTime = dayFormat.format(today); 
        useDBType = psUseDBType;
        
        logger.info("  1.작업이 시작되었습니다[" + workDateTime + "]" );
        
        logger.info("  2.메일 발송" );
        sendMail(workDateTime);
        
        logger.info("  3.스케쥴 완료 처리 -  발송엔진 완료 처리   "  );
        mailQueueComplete();
        
        logger.info("  4.스케쥴 완료 처리 - 다음 스케쥴 처리  "  );
        nextSchedule();
        
        today = new Date();
        workDateTime = dayFormat.format(today); 
        logger.info("  9.작업이 종료되었습니다[" + workDateTime + "]" );
    }

    protected boolean sendMail(String workDateTime) {
        boolean result = false;
        
        StringBuffer sbSql = null;
        String sql         = null;
        String updateSql   = null;
        String scheduleId  = null;
        String subId       = null;
         
        String mailTitleTxt = null;
        String mailDescTxt  = null; 
        
        String sendmailTitleTxt = null;
        String sendmailDescTxt  = null;
        
        String sendStatus   = null;
        String sendErrorMsg = null;
        
        String strNow = "";
        switch(useDBType) {
        case "MSSQL":
        	strNow = "getdate()";
        	break;
        case "ORACLE":
        	strNow = "SYSDATE";
        	break;
        default:
        	strNow = "now()";
        	break;
        }
        
        try {
            sbSql = new StringBuffer();
            sbSql.append("SELECT UC.SCHEDULE_ID")//.append("\n")
                 .append("     , UC.SUB_ID")//.append("\n")
                 .append("     , UC.MAIL_TITLE_TXT")//.append("\n")
                 .append("     , UC.MAIL_DESC_TXT")//.append("\n")
                 .append("  FROM AXONEIS_SUBSCHEDULE UC")//.append("\n")
                 .append(" WHERE UC.SENDSCHE_DT <= '").append(workDateTime).append("'")//.append("\n")
                 .append("   AND UC.WORK_STATUS_GB = 'NORMAL'")//.append("\n")
                 .append("   AND UC.SEND_STATUS    = 'STANDBY'");//.append("\n");
            sql = sbSql.toString();
            logger.debug(" 2-1.스케쥴리스트 조회 쿼리 [" + sql + "]" );
            
            Vector<HashMap<String, String>> scheduleList = getTargetDataList(sql);
            if (scheduleList.size() > 0) {
                for(int i=0; i<scheduleList.size();i++){
                    scheduleId    = scheduleList.get(i).get("SCHEDULE_ID");
                    subId         = scheduleList.get(i).get("SUB_ID");
                    mailTitleTxt  = scheduleList.get(i).get("MAIL_TITLE_TXT");
                    mailDescTxt   = scheduleList.get(i).get("MAIL_DESC_TXT"); 
                    sendStatus    = "INTRANSIT";
                    sendErrorMsg  = "";
                    
                    sbSql = new StringBuffer();
                    sbSql.append("SELECT RECEMAIL_ADDR ")//.append("\n")
                         .append("  FROM AXONEIS_RECEIVER ")//.append("\n")
                         .append(" WHERE SCHEDULE_ID = '").append(scheduleId).append("'")//.append("\n")
                         .append("   AND SUB_ID      = '").append(subId).append("'").append("\n");
                    sql = sbSql.toString();
                    
                    logger.debug(" 2-2.스케쥴의 수신자 조회 쿼리 [" + sql + "]" );
                    
                    Vector<HashMap<String, String>> receiverList = getTargetDataList(sql);
                    
                    if (receiverList.size() > 0) {
                    
                        sbSql = new StringBuffer();
                        sbSql.append("SELECT SC.SQL_TXT")//.append("\n")
                             .append("     , SC.MERGE_ALIAS")//.append("\n")
                             .append("     , SC.COND_COL")//.append("\n")
                             .append("     , SC.OPER_CD")//.append("\n")
                             .append("     , SC.OPER_VALUES_TXT")//.append("\n")
                             .append("     , CD.KOR_ABBR")//.append("\n")
                             .append("     , DB.DBKIND_CD")//.append("\n")
                             .append("     , DB.JNDI_TXT")//.append("\n")
                             .append("     , DB.DB_DRIVER")//.append("\n")
                             .append("     , DB.DBUSER_ID")//.append("\n")
                             .append("     , DB.DBUSERPW_NO")//.append("\n")
                             .append("  FROM AXONEIS_SCHE_CONDITION SC")//.append("\n")
                             .append("       INNER JOIN AXONEIS_SUBSCHEDULE UC ")
                             .append("          ON (SC.SCHEDULE_ID  = UC.SCHEDULE_ID )")//.append("\n")
                             .append("       LEFT OUTER JOIN AXONEIS_CODE CD ")//.append("\n")
                             .append("         ON (SC.OPER_CD     = CD.COMMON_CD ")//.append("\n")
                             .append("             AND CD.CLSS_CD = '00023')")//.append("\n")
                             .append("       INNER JOIN AXONEIS_DBINFO DB ")//.append("\n")
                             .append("          ON (SC.DB_ID  = DB.DB_ID)")//.append("\n")
                             .append(" WHERE SC.SCHEDULE_ID = '").append(scheduleId).append("'")//.append("\n")
                             .append("   AND UC.SUB_ID      = '").append(subId).append("'").append("\n");
                        sql = sbSql.toString();
                        
                        logger.debug(" 2-3-1.스케쥴의 조건 조회 쿼리 [" + sql + "]" );
                        
                        Vector<HashMap<String, String>> scheConditionList = getTargetDataList(sql);
                        
                        /*
                        Vector<HashMap<String, String>> getTargetDataList = new Vector<HashMap<String, String>>();
                        sendTargetDataList = SqlLoader.selectSql(sql, conn) ;
                        */
                        
                        //조건 정보가 있으면 치환 없으면 그대로 발송함 
                        if (scheConditionList.size() > 0) {
                            String condSql       = scheConditionList.get(0).get("SQL_TXT");
                            String mergeAlias    = scheConditionList.get(0).get("MERGE_ALIAS");
                            String condCol       = scheConditionList.get(0).get("COND_COL");
                            String operCd        = scheConditionList.get(0).get("OPER_CD");
                            String operValuesTxt = scheConditionList.get(0).get("OPER_VALUES_TXT");
                            String appr          = scheConditionList.get(0).get("KOR_ABBR");
                            String dbkindCd      = scheConditionList.get(0).get("DBKIND_CD");
                            String jndiTxt       = scheConditionList.get(0).get("JNDI_TXT");
                            String dbDriver      = scheConditionList.get(0).get("DB_DRIVER");
                            String dbuserId      = scheConditionList.get(0).get("DBUSER_ID");
                            String dbuserpwNo    = EncryptUtil.getJasyptDecryptedString(PropManager.getAccStr("JASYPT.ALGORITHM"), PropManager.getAccStr("JASYPT.KEYSTRING"), scheConditionList.get(0).get("DBUSERPW_NO"));
                            
                            Vector<HashMap<String, String>> conditionDataList = getTargetDataListDbInfo(condSql, dbDriver, jndiTxt, dbuserId, dbuserpwNo);
                            if (conditionDataList.size() > 0) {
                                
                                if ("".equals(operCd) || "".equals(operValuesTxt) || appr.length() < 1 ) {// 조건 없음 
                                    String[] arrMergeAlias = mergeAlias.split("\\,"); 
                                    String mergeVal = null;
                                    String targetMergeAlias = null;
                                    for (int a=0; a <arrMergeAlias.length ; a++) {
                                        logger.debug(" 2-3-2. 조건 값 없음 첫번째 데이터로 치환 " );
                                        mergeVal = conditionDataList.get(0).get(arrMergeAlias[a]);
                                        targetMergeAlias = "$:" +  arrMergeAlias[a] + ":$";
                                        mailTitleTxt = mailTitleTxt.replace(targetMergeAlias, mergeVal);
                                        mailDescTxt = mailDescTxt.replace(targetMergeAlias, mergeVal);
                                    } 
                                } 
                                else { //조건 있음
                                    String condColVal = null;
                                    boolean checkCond = false;
                                    int idxVal = -1;
                                    for (int j = 0; j < conditionDataList.size(); j++) {
                                        
                                        Map<String, String> rmap = (HashMap<String, String>) conditionDataList.elementAt(j);
                                        condColVal =  rmap.get(condCol);
                                        //조건 보유 여부 체크  
                                        if (condColVal != null && !"".equals(condColVal)) {
                                            if (StringUtil.isNumber(operValuesTxt)) {
                                                if (StringUtil.isNumber(condColVal)) {
                                                    int nOperValuesTxt =Integer.parseInt(operValuesTxt);
                                                    int nCondColVal =Integer.parseInt(condColVal);
                                                    switch(operCd) {
                                                        case "00001":  // = 
                                                            if(nCondColVal == nOperValuesTxt ) checkCond = true;
                                                            break; 
                                                        case "00002":  //>
                                                            if(nCondColVal >  nOperValuesTxt ) checkCond = true;
                                                                break;
                                                        case "00003":  //< 
                                                            if(nCondColVal < nOperValuesTxt ) checkCond = true;
                                                                break;
                                                        case "00004":  //>=
                                                            if(nCondColVal >= nOperValuesTxt ) checkCond = true;
                                                                break; 
                                                        case "00005":  //<=
                                                            if(nCondColVal <= nOperValuesTxt ) checkCond = true;
                                                                break; 
                                                        case "00006":  //!=
                                                            if(nCondColVal!= nOperValuesTxt ) checkCond = true;
                                                            break;
                                                    }
                                                    if(checkCond) idxVal = j;
                                                } else {
                                                    checkCond = false;
                                                }
                                            } else {
                                                switch(operCd) {
                                                    case "00001":  // = 
                                                        checkCond = condColVal.equals(operValuesTxt);
                                                        break;
                                                    case "00006":  //!=
                                                        checkCond = !condColVal.equals(operValuesTxt);
                                                        break;
                                                    case "00007":  //>LIKE
                                                        checkCond = condColVal.contains(operValuesTxt);
                                                        break; 
                                                    case "00008":  //>Prefix like
                                                        if( condColVal.indexOf(operValuesTxt) == 0 ) {
                                                            checkCond =true;    
                                                        }
                                                        break;
                                                    case "00009":  //>Suffix Like
                                                        int operValuesTxtLen = operValuesTxt.length();
                                                        int condColValLen = condColVal.length();
                                                        if ( condColValLen >= operValuesTxtLen ) {
                                                            if (condColVal.contains(operValuesTxt)) {
                                                                String lastCondColVal = Character.toString(condColVal.charAt(condColVal.length() -1));
                                                                String lastOperValuesTxt = Character.toString(operValuesTxt.charAt(operValuesTxt.length() -1));
                                                                if (lastCondColVal.equals(lastOperValuesTxt)) {
                                                                    checkCond =true;
                                                                } else {
                                                                    checkCond =false;
                                                                }
                                                            }
                                                        } else {
                                                            checkCond =true;
                                                        }
                                                        break;
                                                    case "00010":  //>IN
                                                        checkCond = condColVal.contains(operValuesTxt);
                                                        break;
                                                }
                                                
                                                if(checkCond) idxVal = j;
                                                if(checkCond) {
                                                    logger.debug(" 2-3-2. 조건 일치 하는 값 존재 일치하는 결과 데이터로 치환 " );
                                                    String[] arrMergeAlias = mergeAlias.split("\\,"); 
                                                    String mergeVal         = null;
                                                    String targetMergeAlias = null;
                                                    for (int a=0; a <arrMergeAlias.length ; a++) {
                                                        mergeVal = rmap.get(arrMergeAlias[a]);
                                                        targetMergeAlias = "$:" +  arrMergeAlias[a] + ":$";
                                                        mailTitleTxt = mailTitleTxt.replace(targetMergeAlias, mergeVal);
                                                        mailDescTxt = mailDescTxt.replace(targetMergeAlias, mergeVal);
                                                    }
                                                    break;
                                                }
                                            }
                                        } 
                                    }
                                    if (idxVal < 0) {
                                        logger.debug(" 2-3-2. 조건 일치 하는 값 없음 첫번째 결과 데이터로 치환 " ); 
                                        sendStatus    = "NOTCONDITION";
                                        sendErrorMsg  = "실행조건 불충족(조건 일치 데이터 없음)";
                                        String[] arrMergeAlias = mergeAlias.split("\\,"); 
                                        String mergeVal = null;
                                        String targetMergeAlias = null;
                                        for (int a=0; a <arrMergeAlias.length ; a++) {
                                            mergeVal = conditionDataList.get(0).get(arrMergeAlias[a]);
                                            targetMergeAlias = "$:" +  arrMergeAlias[a] + ":$";
                                            mailTitleTxt = mailTitleTxt.replace(targetMergeAlias, mergeVal);
                                            mailDescTxt = mailDescTxt.replace(targetMergeAlias, mergeVal);
                                        } 
                                    }
                                }//조건 있음 
                            } 
                            else {
                                logger.debug(" 2-3-2. 쿼리 조건에 데이터가 하나도 없음 치환데이터 공백으로 치환 처리  " );
                                sendStatus    = "NOTCONDITION";
                                sendErrorMsg  = "실행조건 불충족(조건 확인 데이터 0건)";
                                String[] arrMergeAlias = mergeAlias.split("\\,"); 
                                String mergeVal = null;
                                String targetMergeAlias = null;
                                for (int a=0; a <arrMergeAlias.length ; a++) {
                                    mergeVal = "";
                                    targetMergeAlias = "$:" +  arrMergeAlias[a] + ":$";
                                    mailTitleTxt = mailTitleTxt.replace(targetMergeAlias, mergeVal);
                                    mailDescTxt = mailDescTxt.replace(targetMergeAlias, mergeVal);
                                } 
                            }
                        } // if (scheConditionList.size() > 0) { 
                        
                        sbSql = new StringBuffer();
                        sbSql.append("SELECT PROC_SNO ")//.append("\n")
                             .append("     , REPORT_ID")//.append("\n")
                             .append("     , REPORT_PATH")//.append("\n")
                             .append("  FROM AXONEIS_RPT_ATTCH ")//.append("\n")
                             .append(" WHERE SCHEDULE_ID = '").append(scheduleId).append("'")//.append("\n")
                             .append("   AND SUB_ID      = '").append(subId).append("'")//.append("\n")
                             .append(" ORDER BY PROC_SNO").append("\n");
                        sql = sbSql.toString();
                        
                        Vector<HashMap<String, String>> reportList = getTargetDataList(sql);
                        if (reportList.size() > 0) {
                            StringBuffer sbTeml = new StringBuffer();
                            String reportUrl = PropManager.getStrValue("EIS_REPORT_URL");
                            String strLimit = PropManager.getStrValue("EIS_LIMIT_TIME");
                            
                            sbTeml.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: rgb(255, 255, 255); border-top: 1px solid rgb(199, 199, 199);\">").append("\n") 
                                  .append("    <tbody>").append("\n")
                                  .append("        <tr>").append("\n")
                                  .append("            <td style=\"padding: 3px 4px 2px; border-bottom: 1px solid rgb(199, 199, 199); background-color: rgb(255, 255, 255); color: rgb(102, 102, 102); width: 1693px; height: 18px;\">").append("\n")
                                  .append("                <p>* 첨부 보고서 링크 *</p>").append("\n")
                                  .append("            </td>").append("\n")
                                  .append("        </tr>").append("\n");
                            
                            for (HashMap<String, String> hashMap : reportList) {
                                String reportId   = hashMap.get("REPORT_ID");
                                String reportPath = hashMap.get("REPORT_PATH");
                                String reportNm   = reportPath.substring(reportPath.lastIndexOf(" > ")+3);
                                Long currentTime  = Instant.now().getEpochSecond();
                                Long limitTime    = currentTime+(60L*60*Integer.parseInt(strLimit));//24시간 유효 
                                //메뉴ID+레포트ID+스케쥴ID+서브ID+종료시간
                                String strParam   = new StringBuffer().append(reportId).append("^!").append(scheduleId).append("^!").append(subId).append("^!").append(limitTime).toString();
                                String authKey    = PageAuth.ecryptData(strParam, PropManager.getAccStr("JASYPT.ALGORITHM"));
                                sbTeml.append("         <tr>").append("\n")
                                      .append("             <td style=\"padding: 3px 4px 2px; border-bottom: 1px solid rgb(199, 199, 199); background-color: rgb(243, 243, 243); color: rgb(102, 102, 102); width: 1693px; height: 17px;\">").append("\n")
                                      .append("                 <p>&nbsp;=&gt;&nbsp;<a title=\"").append(reportNm).append("\" href=\"").append(reportUrl).append("akey=$:MAP1:$&skey=").append(authKey).append("\" target=\"_blank\" rel=\"noopener\">").append(reportPath).append("</a></p>").append("\n")
                                      .append("             </td>").append("\n")
                                      .append("         </tr>").append("\n");
                            }
                            sbTeml.append("        <tr>").append("\n")
                                  .append("            <td style=\"padding: 3px 4px 2px; border-bottom: 1px solid rgb(199, 199, 199); background-color: rgb(255, 255, 255); color: rgb(102, 102, 102); width: 1693px; height: 18px;\">").append("\n")
                                  .append("                <p>&nbsp;</p>").append("\n")
                                  .append("            </td>").append("\n")
                                  .append("        </tr>").append("\n")
                                  .append("    </tbody>").append("\n")
                                  .append("</table>").append("\n");
                                  
                            sbTeml.insert(0, mailDescTxt);
                            mailDescTxt = sbTeml.toString();
                        }
                        /* SSJ 추가 보고서 연결 URL 등록 */
                        
                        sendmailTitleTxt = mailTitleTxt;
                        sendmailDescTxt  = mailDescTxt;
                        
                        sbSql = new StringBuffer();
                        sbSql.append("UPDATE AXONEIS_SUBSCHEDULE ")//.append("\n")
                             .append("   SET SENDMAIL_TITLE_TXT = '").append(sendmailTitleTxt).append("'")//.append("\n")
                             .append("     , SENDMAIL_DESC_TEXT = '").append(sendmailDescTxt).append("'")//.append("\n")
                             .append("     , SEND_STATUS        = '").append(sendStatus).append("'")//.append("\n")
                             .append("     , MODIFY_ID   = 'sys_batch'")//.append(\"\\n\")
                             .append("     , MODIFY_DT   =").append(strNow);
                             //.append("     , MODIFY_DT   = now()");//.append(\"\\n\")
                        if("NOTCONDITION".equals(sendStatus)) {
                            sbSql.append("     , ERRDESC_TXT        = '").append(sendErrorMsg).append("'");//.append("\n")
                        }
                        sbSql.append(" WHERE SCHEDULE_ID = '").append(scheduleId).append("'")//.append("\n")
                             .append("   AND  SUB_ID     = '").append(subId).append("'").append("\n");
                        updateSql = sbSql.toString();
                        sendMailProcess(updateSql);
                    } 
                    else {
                        logger.debug(" 2-2.스케쥴의 수신자 조회 대상 없음 [" + sql + "]" );
                        sendStatus    = "NOTCONDITION";
                        sendErrorMsg  = "실행조건 불충족(수신자 조회 대상 없음)";
                        sbSql = new StringBuffer();
                        sbSql.append("UPDATE AXONEIS_SUBSCHEDULE ")//.append("\n")
                             .append("   SET SEND_STATUS = '").append(sendStatus).append("'")//.append("\n")
                             .append("     , ERRDESC_TXT = '").append(sendErrorMsg).append("'")//.append("\n")
                             //.append("     , MODIFY_DT   = now()")//.append(\"\\n\")
                             .append("     , MODIFY_DT   =").append(strNow)
                             .append("     , MODIFY_ID   = 'sys_batch'")//.append(\"\\n\")
                             .append(" WHERE SCHEDULE_ID = '").append(scheduleId).append("'")//.append("\n")
                             .append("   AND  SUB_ID     = '").append(subId).append("'").append("\n");
                        updateSql = sbSql.toString();
                        sendMailProcess(updateSql);
                    }
                }// for(int i=0; i<scheduleList.size();i++){
            } // if (scheduleList.size() > 0) {
        } 
        catch (Exception e) {
            if( "".equals(scheduleId) && "".equals(subId) ) {
                //오류발생시 ERROR로 상태 변경 
                sbSql = new StringBuffer();
                sbSql.append("UPDATE AXONEIS_SUBSCHEDULE ")//.append("\n")
                     .append("   SET SEND_STATUS = 'ERROR'")//.append("\n")
                     .append("     , ERRDESC_TXT = '").append(e.toString()).append("'")//.append("\n")
                     //.append("     , MODIFY_DT   = now()")//.append(\"\\n\")
                     .append("     , MODIFY_DT   =").append(strNow)
                     .append("     , MODIFY_ID   = 'sys_batch'")//.append(\"\\n\")
                     .append(" WHERE SCHEDULE_ID = '").append(scheduleId).append("'")//.append("\n")
                     .append("   AND  SUB_ID     = '").append(subId).append("'").append("\n");
                try {
                    sendMailProcess(sbSql.toString());
                }
                catch (Exception e1) {
                    logger.error(e+"\n"+e1);
                }
            }
            logger.error(e);
        }
        return result;
    }
    
    protected int sendMailProcess (String updateSql) throws Exception { 
        Connection conn = null; 
        
        int returnValue = 0;
        
        try{
            logger.debug(" 2-3.스케쥴의 상태변경 쿼리 [" + updateSql + "]" );
            
            conn = BatchDBManager.getDBTypeConn(useDBType);
            SqlLoader.execSql(updateSql, conn);
            conn.commit();
            returnValue = 1;
        } catch (ClassNotFoundException e) { 
            logger.error(" 4-8.스케쥴 완료 처리 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 4-8.스케쥴 완료 처리 오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);   
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 4-8.스케쥴 완료 처리 오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 4-8.스케쥴 완료 처리 오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
        return returnValue; 
    }
    
    
    protected int mailQueueComplete ( ) throws Exception { 
        Connection conn = null;
        CallableStatement cs = null ;
        
        int returnValue = 0;
        
        try{
            logger.info("  3-1.발송엔진 완료 처리 작업  axoneis.PROC_MAILQUEUE_CLS()" );
            
            conn = BatchDBManager.getDBTypeConn(useDBType);
            conn.setAutoCommit(false);
            //cs = conn.prepareCall("{call axoneis.PROC_MAILQUEUE_CLS()}"); 
            cs = conn.prepareCall("{call PROC_MAILQUEUE_CLS()}");
            cs.execute();
            conn.commit();
            returnValue = 1;
        } catch (ClassNotFoundException e) { 
            logger.error(" 3-8.발송엔진 완료 처리 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 3-8발송엔진 완료 처리 오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);   
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 3-8.발송엔진 완료 처리 오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 3-8.발송엔진 완료 처리 오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
         
        return returnValue;
    }
    
    protected int nextSchedule ( ) throws Exception { 
        Connection conn = null;
        CallableStatement cs = null ;
        
        int returnValue = 0;
        
        try{
            logger.info("  4-1.다음 스케쥴 처리   axoneis.PROC_SCHEDULE_CLS()" );

            conn = BatchDBManager.getDBTypeConn(useDBType);
            conn.setAutoCommit(false);
            //cs = conn.prepareCall("{call axoneis.PROC_SCHEDULE_CLS()}"); 
            cs = conn.prepareCall("{call PROC_SCHEDULE_CLS()}");
            cs.execute();
            conn.commit();
            returnValue = 1;
        } catch (ClassNotFoundException e) { 
            logger.error(" 4-8.다음 스케쥴 처리 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 4-8.다음 스케쥴 처리  오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);  
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 4-8.다음 스케쥴 처리  처리 오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 4-8.다음 스케쥴 처리  처리 오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
        return returnValue; 
    }
    
    protected Vector<HashMap<String, String>> getTargetDataList(String sql) throws Exception {
        Vector<HashMap<String, String>> sendTargetDataList = new Vector<HashMap<String, String>>();
        Connection conn = null;
        try { 
            //conn = BatchDBManager.getMySqlConn();

        	conn = BatchDBManager.getDBTypeConn(useDBType);
            sendTargetDataList = SqlLoader.selectSql(sql, conn) ;
            
        } catch (ClassNotFoundException e) { 
            logger.error(" 8-8.대상 추출 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 8-8.대상 추출  오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);  
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 8-8.대상 추출  오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 8-8.대상 추출  오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
        
        return sendTargetDataList;
    }
    
    protected Vector<HashMap<String, String>> getTargetDataListDbInfo(String sql, String dbDriver, String dbUrl, String loginId, String loginPwd ) throws Exception {

        Vector<HashMap<String, String>> sendTargetDataList = new Vector<HashMap<String, String>>();
        Connection conn = null;
        try { 
            conn = BatchDBManager.getDBConn(dbDriver, dbUrl, loginId,loginPwd );
            sendTargetDataList = SqlLoader.selectSql(sql, conn) ;
            
        } catch (ClassNotFoundException e) { 
            logger.error(" 7-8.조건 추출 오류  [ClassNotFoundException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn);   
        } catch (SQLException e) { 
            logger.error(" 7-8.조건 추출  오류  [SQLException : " + e.getErrorCode() + "] [ErrorMessage" + e.getMessage() + "]", e);  
            errorHandle(e, conn); 
        } catch (UnsupportedEncodingException e) { 
            logger.error(" 7-8.조건 추출  오류  [UnsupportedEncodingException : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        }  catch (Exception e) { 
            logger.error(" 7-8.조건 추출  오류  [Exception : " + e.getMessage() +"]" , e);
            errorHandle(e, conn); 
        } finally {  
            conn.close();
        }
        
        return sendTargetDataList;
    }
}
