package kr.co.enders.batch.exceptions;

/**
 * @author HOJUNG CHOI
 * @version 1.0
 */

public class TimeoutException extends DefaultException
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public TimeoutException() {
    super();
  }

  public TimeoutException(String error) {
    super(error);
  }

  public TimeoutException(String errorCode, String error) {
    super(error);
    super.setErrorCode(errorCode);
  }

  public TimeoutException(String errorCode, String errorMessage, String error) {
    super(error);
    super.setErrorCode(errorCode);
    super.setErrorMessage(errorMessage);
  }

}
