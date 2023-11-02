/**
 * 
 */
package kr.co.enders.batch.utils;

import java.util.Hashtable;
 

import org.apache.log4j.Logger;

import kr.co.enders.batch.utils.DateUtil;

/**
 * @author Administrator
 *
 */
public class WorkTerm {
	private static final Logger logger = Logger.getLogger(WorkTerm.class);
	private Hashtable hashWorkDayList;	
	private int idxWorkDayList;
	private String strWorkTerm; 
	private String strWorkDay;
	String arrStrJobWorkDay[] = null;

	public WorkTerm(){
		hashWorkDayList = new Hashtable();
	}
	
	public boolean hasNext(){
		if (idxWorkDayList > hashWorkDayList.size() || hashWorkDayList.size() == 0){
			return false;
		}else{		
			return true;
		}
	}
	
	public String getWorkDay(){
		
		String strDay = "";
		try{
			strDay = (String)hashWorkDayList.get(String.valueOf(idxWorkDayList));
		}catch(Exception e){
			logger.error("[WorkTerm.getWorkDay] hashtable get error " + e.getMessage() );			
		}
		if (strDay == null){
			strDay = "";
		}
		idxWorkDayList ++;
		return strDay;
	}


	public void setWorkTerm(String strDay){
		int i = 0;

		idxWorkDayList = 0;
		
		strWorkTerm = strDay.replaceAll(" ","");

		// 정상적인 작업 일자를 구한다.  
		// 특정일의 작업이 아니라면 작업일을 시스템일자에서 얻어온다.		
		if (strWorkTerm.length() == 0 ){
			strWorkTerm = DateUtil.simpleGetCurrentDate("yyyyMMdd");
		}		

		// 일자 하나만 들어올 경우 
		if (strWorkTerm.length() == 8 ){
			hashWorkDayList.put("1",strWorkTerm);
		}
		
		// 특정 일자가 여러날자 선택된것으로  간주한다. 
		// 작업일 지정은 ","로 구분하여 여러날자를 넣을수도 있다. 하루 이상인경우를 처리한다.  
		if(strWorkTerm.indexOf(",")>0){
			// delimeter ","를 구분으로 배열에 값을 넣는다. 
			arrStrJobWorkDay = strWorkTerm.split(",",0);			
			if ( arrStrJobWorkDay != null){
				// 배열에 있는 값중에서 유효한 값에 있어서 반복해서 기입일 작업을 수행 한다.  
				for( i = 0 ; i < arrStrJobWorkDay.length; i++ ){
					if (arrStrJobWorkDay[i].trim().length() == 8){
						hashWorkDayList.put(String.valueOf(i+1),arrStrJobWorkDay[i].trim());
					}
				}
			}
		}
		/*
		// 작업일 지정은 "~"로 구분하여 기간을 넣을수도 있다. 
		if(strWorkTerm.indexOf("~")>0){
			
			arrStrJobWorkDay = strWorkTerm.split("~",0);
			
			// 시작일과 종료일로 구분하여 나눈다. 
			if (arrStrJobWorkDay.length == 2){

				try{
					// 두 날자사이의 기간을 구한다. 
					i = DateTime.daysBetween(arrStrJobWorkDay[0].trim(),arrStrJobWorkDay[1].trim());
					
					strWorkDay = arrStrJobWorkDay[0].trim();
					
					for (int j = 0; j <= i ; j++){
						hashWorkDayList.put(String.valueOf(j+1),strWorkDay);
						strWorkDay = DateTime.addDays(strWorkDay,1);
					}	
					
				}catch (Exception e){
					Viewer_.OutPutString(Viewer.LOG_LEVEL_ERROR,"[WorkTerm.setWorkTerm] 일자 계산 오류 " + e.getMessage() );			
				}
				
			}
		}
		*/
		
		if (hashWorkDayList.size() > 0){
			idxWorkDayList = 1;
			if( ! strDay.equals("")){
				logger.info(" ---------- 지정 작업 일자  = [" + strDay +"] ----------"  );	
			}
		}else{
			logger.error(" ---------- 작업 일자 지정 오류 = [" + strDay +"] ----------"  );
		}
	}	
}
