package kr.co.enders.batch.exceptions;

/**
 * @author HOJUNG CHOI
 * @version 1.0
 */
public class DBException extends DefaultException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DBException() {
    super();
  }

  public DBException(String error) {
    super(error);
  }

  public DBException(String errorCode, String error) {
    super(error);
    super.setErrorCode(errorCode);
  }

  public DBException(String errorCode, String errorMessage, String error) {
    super(error);
    super.setErrorCode(errorCode);
    super.setErrorMessage(errorMessage);
  }

}
