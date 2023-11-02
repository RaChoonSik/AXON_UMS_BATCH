package kr.co.enders.batch.utils;

import kr.co.enders.batch.conf.GetPropsFromConfig;
import kr.co.enders.batch.conf.PropertyFileReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/*
 * FTPTransfer ftputil = new FTPTransfer();
 * boolean uploadok = ftputil.FtpPut("FTP 서버아이피", "FTP 서버의 FTP포트", "FTP 아이디", "FTP 패스워드",
 *                                   "FTP 서버의 업로드할 홈 디렉토리", "FTP서버의 홈 디렉토리 하위에 만들 디렉토리", 
 *                                   "FTP서버의 홈 디렉토리 하위에 만들 디렉토리에 업로드할 파일 리스트");
 * FTP 서버의 FTP포트 : int 형입니다.
 * FTP서버의 홈 디렉토리 하위에 만들 디렉토리에 업로드할 파일 리스트 : 업로드할 파일들의 리스트입니다. 파일들은 풀패스로 구성해야됩니다. /aaa/bbb/ccc/ddd.hwp  이런식으로요 
 */
public class FTPUtil
{
  /**
   * 로거 정의
   */
  static Logger logger = Logger.getLogger(FTPUtil.class);

  // 여러개의 파일을 전송한다.
  public static boolean FtpPut(String subdir, String localdir, List<String> files)
  {
    boolean result = false;
    FTPClient ftp = null;
    int reply = 0;
    String ip = GetPropsFromConfig.get("ftp.svrurl");
    int port = Integer.parseInt(GetPropsFromConfig.get("ftp.port"));
    String id = GetPropsFromConfig.get("ftp.user");
    String pwd = GetPropsFromConfig.get("ftp.passwd");
    String mod = GetPropsFromConfig.get("ftp.mode");
    String connmod = GetPropsFromConfig.get("ftp.connmode");
    String remotedir = GetPropsFromConfig.get("ftp.remoteupdir");
    //log for ftp connection info
    logger.debug("ftp server=[" + ip + "]/ftp port=[" + port + "]/ftp user=[" + id + "]/ftp mod=["
        + mod + "]/conmod=[" + connmod + "]");

    try {
      ftp = new FTPClient();
      ftp.connect(ip, port);
      logger.debug("The FTP Connection to " + ip + " is " + ftp.isConnected());

      reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        logger.error("FTP server refused connection.");
        ftp.disconnect();
        return result;
      }
    }
    catch (IOException e) {
      if (ftp.isConnected()) {
        try {
          ftp.disconnect();
        }
        catch (IOException f) {
          // do nothing
        }
      }
      logger.error("Could not connect to server.");
      e.printStackTrace();
      System.exit(1);
    }

    try {
      //ftp.login(id, pwd);
      if (!ftp.login(id, pwd)) {
        logger.debug("The login is " + !ftp.login(id, pwd));
        ftp.logout();
        return result;
      }
      logger.debug("Remote system is " + ftp.getSystemType());

      ftp.setFileType((mod.equals("BINARY") ? FTP.BINARY_FILE_TYPE : FTP.ASCII_FILE_TYPE));

      // Use passive mode as default because most of us are behind firewalls these days.
      if (connmod.equals("PASV")) ftp.enterLocalPassiveMode();
      else ftp.enterLocalActiveMode();

      ftp.changeWorkingDirectory(remotedir);
      if (!subdir.equals("")) {
        ftp.makeDirectory(subdir);
        ftp.changeWorkingDirectory(subdir);
      }

      logger.debug("The file size is " + files.size());
      for (int i = 0; i < files.size(); i++) {
        String sourceFile = files.get(i); // 디렉토리+파일명
        File uploadFile = new File(localdir + sourceFile);
        FileInputStream fis = null;
        try {
          fis = new FileInputStream(uploadFile);
          boolean isSuccess = ftp.storeFile(sourceFile, fis);
          if (isSuccess) {
            logger.debug("파일 FTP 업로드 성공! ==> [" + localdir + sourceFile + "] ");
          }
        }
        catch (IOException ioe) {
          logger.error(ExceptionUtils.getStackTrace(ioe));
        }
        finally {
          if (fis != null) {
            try {
              fis.close();
            }
            catch (IOException ioe) {
              System.out.println(ExceptionUtils.getStackTrace(ioe));
              logger.error(ExceptionUtils.getStackTrace(ioe));
              // ioe.printStackTrace();
            }
          }
        }
      }

      ftp.logout();
      result = true;

    }
    catch (SocketException se) {
      logger.error(ExceptionUtils.getStackTrace(se));
      // se.printStackTrace();
    }
    catch (IOException ioe) {
      logger.error(ExceptionUtils.getStackTrace(ioe));
      // ioe.printStackTrace();
    }
    catch (Exception e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      // e.printStackTrace();
    }
    finally {
      if (ftp != null && ftp.isConnected()) {
        try {
          ftp.disconnect();
        }
        catch (IOException e) {
        }
      }
    }
    return result;
  }

  // 파일을 받는다.
  public static boolean FtpGet(String subdir, String localdir, String fileName)
  {
    boolean result = false;
    FTPClient ftp = null;
    int reply = 0;

    String ip = GetPropsFromConfig.get("ftp.svrurl");
    int port = Integer.parseInt(GetPropsFromConfig.get("ftp.port"));
    String id = GetPropsFromConfig.get("ftp.user");
    String pwd = GetPropsFromConfig.get("ftp.passwd");
    String mod = GetPropsFromConfig.get("ftp.mode");
    String connmod = GetPropsFromConfig.get("ftp.connmode");
    String remotedir = GetPropsFromConfig.get("ftp.remotedndir");
    //String downdir = GetPropsFromConfig.get("IN_DIR");
    //log for ftp connection info
    logger.debug("ftp server=[" + ip + "]/ftp port=[" + port + "]/ftp user=[" + id + "]/ftp mod=["
        + mod + "]/conmod=[" + connmod + "]");

    try {
      ftp = new FTPClient();
      ftp.connect(ip, port);
      logger.debug("The FTP Connection to " + ip + " is " + ftp.isConnected());

      reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        logger.error("FTP server refused connection.");
        ftp.disconnect();
        return result;
      }
    }
    catch (IOException e) {
      if (ftp.isConnected()) {
        try {
          ftp.disconnect();
        }
        catch (IOException f) {
          // do nothing
        }
      }
      logger.error("Could not connect to server.");
      e.printStackTrace();
      System.exit(1);
    }

    try {
      if (!ftp.login(id, pwd)) {
        logger.debug("The login is " + !ftp.login(id, pwd));
        ftp.logout();
        return result;
      }

      ftp.setFileType((mod.equals("BINARY") ? FTP.BINARY_FILE_TYPE : FTP.ASCII_FILE_TYPE));

      //Use passive mode as default because most of us are behind firewalls these days.
      if (connmod.equals("PASV")) ftp.enterLocalPassiveMode();
      else ftp.enterLocalActiveMode();

      //change current directory
      ftp.changeWorkingDirectory(remotedir);
      logger.debug("Current directory is " + ftp.printWorkingDirectory());

      //get list of filenames
      FTPFile[] ftpFiles = ftp.listFiles();
      if (ftpFiles != null && ftpFiles.length > 0) {
        //loop thru files
        for (FTPFile file : ftpFiles) {
          if (!file.isFile()) {
            continue;
          }
          logger.debug("File is " + file.getName());
          //get output stream
          OutputStream output;
          if (file.getName().indexOf(subdir) > 0) {
            output = new FileOutputStream(localdir + subdir + "/" + file.getName());
            //get the file from the remote system
            boolean isSuccess = ftp.retrieveFile(file.getName(), output);
            if (isSuccess) {
              logger.debug("파일 FTP 업로드 성공! ==> [" + file.getName() + "]");
              //delete the file on server
              ftp.deleteFile(file.getName()); //테스트로 잠시 막아둠. 상용에서는 풀것!
            }
            else logger.debug("파일 FTP 업로드 실패! ==> [" + file.getName() + "]");
            //close output stream
            output.close();
          }
        }
      }
      ftp.logout();
      result = true;
    }
    catch (SocketException se) {
      logger.error(ExceptionUtils.getStackTrace(se));
    }
    catch (IOException ioe) {
      logger.error(ExceptionUtils.getStackTrace(ioe));
    }
    catch (Exception e) {
      logger.error(ExceptionUtils.getStackTrace(e));
    }
    finally {
      if (ftp != null && ftp.isConnected()) {
        try {
          ftp.disconnect();
        }
        catch (IOException e) {
          System.out.println(ExceptionUtils.getStackTrace(e));
        }
      }
    }
    return result;
  }

  public static int upload(String localFilePath, String remoteFilePath, String fileName)
      throws Exception
  {

    FTPClient ftp = null; // FTP Client 객체
    FileInputStream fis = null; // File Input Stream
    File uploadfile = new File(localFilePath); // File 객체

    String url = GetPropsFromConfig.get("ftp.svrurl");
    String port = GetPropsFromConfig.get("ftp.port");
    String id = GetPropsFromConfig.get("ftp.user");
    String pwd = GetPropsFromConfig.get("ftp.passwd");

    int result = -1;

    try {
      ftp = new FTPClient(); // FTP Client 객체 생성
      ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩
      ftp.connect(url, Integer.parseInt(port)); // 서버접속 " "안에 서버 주소 입력 또는 "서버주소", 포트번호
      ftp.login(id, pwd); // FTP 로그인 ID, PASSWORLD 입력
      ftp.enterLocalPassiveMode(); // Passive Mode 접속일때
      ftp.changeWorkingDirectory(remoteFilePath); // 작업 디렉토리 변경
      ftp.setFileType(FTP.BINARY_FILE_TYPE); // 업로드 파일 타입 셋팅

      try {
        fis = new FileInputStream(uploadfile); // 업로드할 File 생성
        boolean isSuccess = ftp.storeFile(fileName, fis); // File 업로드

        if (isSuccess) {
          result = 1; // 성공
        }
        else {
          System.out.println("파일 업로드를 할 수 없습니다.");
        }
      }
      catch (IOException ex) {
        System.out.println("IO Exception : " + ex.getMessage());
      }
      finally {
        if (fis != null) {
          try {
            fis.close(); // Stream 닫기
            return result;

          }
          catch (IOException ex) {
            System.out.println("IO Exception : " + ex.getMessage());
          }
        }
      }
      ftp.logout(); // FTP Log Out
    }
    catch (IOException e) {
      System.out.println("IO:" + e.getMessage());
    }
    finally {
      if (ftp != null && ftp.isConnected()) {
        try {
          ftp.disconnect(); // 접속 끊기
          return result;
        }
        catch (IOException e) {
          System.out.println("IO Exception : " + e.getMessage());
        }
      }
    }
    return result;
  }

  public static int download(String filePath, String fileName) throws Exception
  {

    FTPClient client = null;
    BufferedOutputStream bos = null;
    File fPath = null;
    File fDir = null;
    File f = null;

    String url = GetPropsFromConfig.get("ftp.svrurl");
    String port = GetPropsFromConfig.get("ftp.port");
    String id = GetPropsFromConfig.get("ftp.user");
    String pwd = GetPropsFromConfig.get("ftp.passwd");
    String downloadPath = PropertyFileReader.getPropertyInfo("FTP_PATH"); // 다운로드 경로

    int result = -1;

    try {
      // download 경로에 해당하는 디렉토리 생성
      downloadPath = downloadPath + filePath;
      fPath = new File(downloadPath);
      fDir = fPath;
      fDir.mkdirs();

      f = new File(downloadPath, fileName);

      client = new FTPClient();
      client.setControlEncoding("UTF-8");
      client.connect(url, Integer.parseInt(port));

      int resultCode = client.getReplyCode();

      if (FTPReply.isPositiveCompletion(resultCode) == false) {
        client.disconnect();
        System.out.println("FTP 서버에 연결할 수 없습니다.");
      }
      else {
        client.setSoTimeout(5000);
        boolean isLogin = client.login(id, pwd);

        if (isLogin == false) {
          System.out.println("FTP 서버에 로그인 할 수 없습니다.");
        }

        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.changeWorkingDirectory(filePath);

        bos = new BufferedOutputStream(new FileOutputStream(f));
        boolean isSuccess = client.retrieveFile(fileName, bos);

        if (isSuccess) {
          result = 1; // 성공
        }
        else {
          System.out.println("파일 다운로드를 할 수 없습니다.");
        }

        client.logout();
      } // if ~ else
    }
    catch (Exception e) {
      System.out.println("FTP Exception : " + e);
    }
    finally {
      if (bos != null) bos.close();
      if (client != null && client.isConnected()) client.disconnect();
    } // try ~ catch ~ finally
    return result;
  } // download()

  public static int delete(String localFilePath, String remoteFilePath, String fileName)
      throws Exception
  {

    FTPClient ftp = null; // FTP Client 객체
    FileInputStream fis = null; // File Input Stream

    String url = PropertyFileReader.getPropertyInfo("FTP_URL");
    String id = PropertyFileReader.getPropertyInfo("FTP_ID");
    String pwd = PropertyFileReader.getPropertyInfo("FTP_PWD");
    String port = PropertyFileReader.getPropertyInfo("FTP_PORT");

    int result = -1;

    try {
      ftp = new FTPClient(); // FTP Client 객체 생성
      ftp.setControlEncoding("UTF-8"); // 문자 코드를 UTF-8로 인코딩
      ftp.connect(url, Integer.parseInt(port)); // 서버접속 " "안에 서버 주소 입력 또는 "서버주소", 포트번호
      ftp.login(id, pwd); // FTP 로그인 ID, PASSWORLD 입력
      ftp.enterLocalPassiveMode(); // Passive Mode 접속일때
      ftp.changeWorkingDirectory(remoteFilePath); // 작업 디렉토리 변경
      ftp.setFileType(FTP.BINARY_FILE_TYPE); // 업로드 파일 타입 셋팅

      try {
        boolean isSuccess = ftp.deleteFile(fileName);// 파일삭제

        if (isSuccess) {
          result = 1; // 성공
        }
        else {
          System.out.println("파일을 삭제 할 수 없습니다.");
        }
      }
      catch (IOException ex) {
        System.out.println("IO Exception : " + ex.getMessage());
      }
      finally {
        if (fis != null) {
          try {
            fis.close(); // Stream 닫기
            return result;

          }
          catch (IOException ex) {
            System.out.println("IO Exception : " + ex.getMessage());
          }
        }
      }
      ftp.logout(); // FTP Log Out
    }
    catch (IOException e) {
      System.out.println("IO:" + e.getMessage());
    }
    finally {
      if (ftp != null && ftp.isConnected()) {
        try {
          ftp.disconnect(); // 접속 끊기
          return result;
        }
        catch (IOException e) {
          System.out.println("IO Exception : " + e.getMessage());
        }
      }
    }
    return result;
  }
}
