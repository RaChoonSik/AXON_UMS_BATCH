package kr.co.enders.batch.utils;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import kr.co.enders.batch.utils.DateUtil;
import kr.co.enders.batch.utils.PropManager;

public class FileDelete {
	private static final Logger logger = Logger.getLogger(FileDelete.class);

	public static void main(String[] args) throws ParseException {
		executeMain();
	}
	
	public static void executeMain() {
		int delFileTypeCnt = Integer.parseInt(PropManager.getStrValue("DEL_FILE_TYPE_CNT"));
		for (int i = 0; i < delFileTypeCnt; i++) {
			try {
				fileDelete(i);	
			} catch (Exception e) {
				logger.error(e);
				System.exit(1);
			}
		}
	}

	/**
	 * 파일삭제 처리
	 * 1. log등 날짜에 의한 특별한 규격이 정해진 파일을 삭제한다.
	 * 2. 설정파일에 설정된 내용 [DEL_FILE_SET_X]와, [DEL_FILE_TYPE_CNT]을 사용한다.
	 * 3. DEL_FILE_SET_X의 내용은 [파일경로, 정규식, 날짜형식, 삭제주기, 파일확장자]로 구성된다.
	 * 4. 사용방법
	 *    4.1. 디렉토리에 날짜가 존재하며 하위 파일을 모두 삭제하는 경우
	 *         샘플 = DEL_FILE_SET_0=D:/Development/workspace/IAS/IAS_BATCH/Test/des/yyyyMMdd,.*,yyyyMMdd,-10,NOT,0
	 *         
	 *         - 파일경로에서 날짜가 들어가는 부분에 날짜형식과 동일한 값을 추가 (해당 부분은 반드시 경로의 마지막에 와야 함)
	 *         - 파일확장자는 *등 특수문자를 제외한 아무 문자나 입력 (파일 확장자에 관계없이 모두 삭제하므로)
	 *    
	 *    4.2. 디렉토리에 날짜가 존재하며 하위 파일에도 날짜가 존재하는 경우
	 *         샘플 = DEL_FILE_SET_0=D:/Development/workspace/IAS/IAS_BATCH/Test/des/yyyyMMdd,.*\\d{4}\\d{2}\\d{2},yyyyMMdd,-10,.log,0
	 *       
	 *         - 파일경로에서 날짜가 들어가는 부분에 날짜형식과 동일한 값을 추가 (해당 부분은 반드시 경로의 마지막에 와야 함)
	 *         - 정규식 및 파일확장자는 삭제하고자 하는 형식에 맞춰 입력
	 *         ※ 모든 파일이 삭제되지 않을 경우 디렉토리 역시 삭제안됨
	 *         
	 *    4.3. 디렉토리가 고정이며 하위 파일에만 날짜가 존재하는 경우
	 *         샘플 = DEL_FILE_SET_0=D:/Development/workspace/IAS/IAS_BATCH/logs,.*log.\\d{4}\\d{2}\\d{2},yyyyMMdd,-10,.log,0
	 *         
	 *         - 파일경로 / 정규식 / 파일확장자는 삭제하고자 하는 형식에 맞춰 입력
	 *         
	 *    4.4. 파일의 최종수정일을 이용해서 삭제하는 경우
	 *         샘플 = DEL_FILE_SET_0=D:/Development/workspace/IAS/IAS_BATCH/logs,.*log.\\d{4}\\d{2}\\d{2},yyyyMMdd,-10,.log,1
	 *         
	 *         - 파일명 뒤에 붙은 날짜를 무시하고, 파일자체의 최종수정일을 기준으로 파일을 삭제
	 * 
	 * ※ 샘플
	 * DEL_FILE_TYPE_CNT=3
	 * DEL_FILE_SET_0=D:/logs,.*log.\\d{4}\\d{2}\\d{2},yyyyMMdd,-30,.log,0
	 * DEL_FILE_SET_1=D:/logs,.*log.\\d{4}-\\d{2}-\\d{2},yyyy-MM-dd,-30,.log,0
	 * DEL_FILE_SET_2=D:/logs,.*log.*,,-30,.log,1
	 * 
	 * ※ 디렉토리 삭제는 날짜형식이 존재하는 것을 전제함
	 * @param index
	 */
	public static void fileDelete(int index) {
		final String[] delFileSets = PropManager.getStrValue("DEL_FILE_SET_" + index).split(",");
		final String path = delFileSets[0];        // 파일경로
		final String nameRegExp = delFileSets[1];  // 삭제대상 파일명을 필터링하기 위한 정규식
		final String dateFormat = delFileSets[2];  // 파일명에 붙은 날짜의 형식 (yyyyMMdd / yyyy-MM-dd 등)
		final int delTerm = Integer.parseInt(delFileSets[3]); // 삭제주기 (-XX일)
		final String fileExt = delFileSets[4];  // 파일확장자 (.log / .dat 등)
		final int useLastMod = Integer.parseInt(delFileSets[5]);  // 파일최종수정일 이용 (0:이용안함 / 1:이용함)
		final int delDirOnly = Integer.parseInt(delFileSets[6]);  // 디렉토리 삭제 (0:이용안함 / 1:이용함)
		final File dir = new File(path.replaceAll(dateFormat, ""));
		final Date limitDate = DateUtil.addDate(new Date(), delTerm, Calendar.DATE);
		final boolean isDirDate = path.matches(".*" + dateFormat);

		logger.info(new StringBuilder(">>> PATH=[").append(path)
				.append("] FileName RegExp=[").append(nameRegExp)
				.append("] / DATE FORMAT=[").append(dateFormat)
				.append("] / DELETE TERM=[").append(delTerm)
				.append("] / FILE EXT=[").append(fileExt)
				.append("] / USE LASTMODIFY=[").append(useLastMod).append("]")
				.append("] / USE DIR DELETE=[").append(delDirOnly).append("]"));

		if (delDirOnly > 0) {
			executeDirDelete(dir, nameRegExp, fileExt, dateFormat, limitDate, isDirDate, useLastMod);
		} else {
			executeFileDelete(dir, nameRegExp, fileExt, dateFormat, limitDate, isDirDate, useLastMod);	
		}
	}

	/**
	 * 삭제처리 실행 메서드 (재귀)
	 * ■ 처리흐름
	 * 1. 디렉토리일 경우
	 *    1.1 하위 파일을 삭제하기 위해 자신(메서드)를 재귀호출
	 *    1.2 디렉토리 삭제 (config에 정의된 내용과 일치하는 경우에 한함)
	 *          ※ 디렉토리 하위 파일까지 모두 삭제되어야 하므로 조건이 까다로움 
	 * 2. 파일일 경우
	 *    2.1 파일 삭제 (config에 정의된 내용과 일치하는 경우에 한함)
	 * @param dir
	 * @param nameRegExp
	 * @param fileExt
	 * @param dateFormat
	 * @param limitDate
	 * @param isDirDate
	 */
	private static void executeFileDelete(
			File dir, String nameRegExp, String fileExt, String dateFormat, Date limitDate, boolean isDirDate, int useLastMod) {
		File[] fileList = dir.listFiles();
		Date lastModified = null;
		String dateStr = "";
		String tmpFileName = "";
		if (fileList == null) {
			logger.warn(">>> PATH is Not Exist"); // 해당 디렉토리에 파일이 존재하지 않을 경우
			return;
		}
		for (File file : fileList) {
			String name = file.getName();
			// 디렉토리일 경우
			if (file.isDirectory()) {
				int formatLength = name.length() - dateFormat.length();
				if (formatLength < 0) {
					continue;
				}
				if (!isDirDate) {
					continue;
				}
				dateStr = name.substring(formatLength);
				try {
					if (useLastMod > 0) {
						lastModified = new Date(file.lastModified());
					} else {
						lastModified = DateUtil.stringToDate(dateStr, dateFormat);	
					}
				} catch (ParseException e) {} // 날짜 변환에서 파싱에러가 발생할 경우, 해당 파일명에는 날짜 포멧이 없는 것으로 간주하여 무시
				// 파일명에 붙은 날짜가 삭제대상일 이전일 경우
				if (lastModified != null && lastModified.before(limitDate)) {
					executeFileDelete(file, nameRegExp, fileExt, dateFormat, limitDate, isDirDate, useLastMod);
					file.delete(); // 디렉토리 삭제
					logger.info(new StringBuilder(">>> Directory Deleted - ").append(name));
				}
				// 파일일 경우
			} else {
				if (name.matches(nameRegExp)) {
					tmpFileName = name.replaceAll(fileExt, ""); // 파일확장자 제거
					dateStr = tmpFileName.substring(tmpFileName.length() - dateFormat.length());
					try {
						if (useLastMod > 0) {
							lastModified = new Date(file.lastModified());
						} else {
							lastModified = DateUtil.stringToDate(dateStr, dateFormat);	
						}
					} catch (ParseException e) {} // 날짜 변환에서 파싱에러가 발생할 경우, 해당 파일명에는 날짜 포멧이 없는 것으로 간주하여 무시
					// 파일명에 붙은 날짜가 삭제대상일 이전일 경우
					if (lastModified != null && lastModified.before(limitDate)) {
						file.delete();
						logger.info(new StringBuilder(">>>>>> File Deleted - ").append(name));
					}
				}
			}
		} // End of for
	}
	/**
	 * ■ 처리흐름
	 * 1. 디렉토리일 경우
	 *    1.1 하위 파일을 삭제하기 위해 자신(메서드)를 재귀호출
	 *    1.2 디렉토리 삭제 (config에 정의된 내용과 일치하는 경우에 한함)
	 *          ※ 디렉토리 하위 파일까지 모두 삭제되어야 하므로 조건이 까다로움 
	 * 2. 파일일 경우
	 *    2.1 파일 삭제 (무조건)
	 * @param dir
	 * @param nameRegExp
	 * @param fileExt
	 * @param dateFormat
	 * @param limitDate
	 * @param isDirDate
	 * @param useLastMod
	 */
	private static void executeDirDelete(
			File dir, String nameRegExp, String fileExt, String dateFormat, Date limitDate, boolean isDirDate, int useLastMod) {

		File[] fileList = dir.listFiles();
		Date lastModified = null;
		String dateStr = "";
		if (fileList == null) {
			logger.warn(">>> PATH is Not Exist"); // 해당 디렉토리에 파일이 존재하지 않을 경우
			return;
		}
		for (File file : fileList) {
			String name = file.getName();
			// 디렉토리일 경우
			if (file.isDirectory()) {
				int formatLength = name.length() - dateFormat.length();
				if (formatLength < 0) {
					continue;
				}
				if (!isDirDate) {
					continue;
				}
				dateStr = name.substring(formatLength);
				try {
					if (useLastMod > 0) {
						lastModified = new Date(file.lastModified());
					} else {
						lastModified = DateUtil.stringToDate(dateStr, dateFormat);	
					}
				} catch (ParseException e) {} // 날짜 변환에서 파싱에러가 발생할 경우, 해당 파일명에는 날짜 포멧이 없는 것으로 간주하여 무시
				// 파일명에 붙은 날짜가 삭제대상일 이전일 경우
				if (lastModified != null && lastModified.before(limitDate)) {
					executeDirDelete(file, nameRegExp, fileExt, dateFormat, limitDate, isDirDate, useLastMod);
					if (!file.delete()) {
						logger.info(new StringBuilder(">>> Delete Fail - ").append(name));
						continue;
					}
					logger.info(new StringBuilder(">>> Directory Deleted - ").append(name));
				}
			} else {
				if (!file.delete()) {
					logger.info(new StringBuilder(">>> Delete Fail - ").append(name));
				}
			}
		} // End of for
	}
}
