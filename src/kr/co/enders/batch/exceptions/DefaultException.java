package kr.co.enders.batch.exceptions;

/**
 * @author HOJUNG CHOI
 * @version 1.0
 * 
 *          Exception�� Serializable�� ���
 */
public class DefaultException extends Exception implements java.io.Serializable
{

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private static Exception  exception;

  private String            errorCode;            // ����� ���ǵ� �����ڵ�

  private String            errorMessage;         // ����� ���ǵ� ���� �޼���

  public DefaultException() {
    super();
  }

  public DefaultException(String error) {
    super(error);
  }

  public DefaultException(String errorCode, String error) {
    super(error);
    this.errorCode = errorCode;
  }

  public DefaultException(String errorCode, String errorMessage, String error) {
    super(error);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }

  public String getErrorCode()
  {
    return this.errorCode;
  }

  public void setErrorMessage(String errorMessage)
  {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  /*
   * //jdk1.4�������� ���� �̸��� method�� �־ �ּ�ó����
   * public String getStackTrace() { ByteArrayOutputStream
   * out = new ByteArrayOutputStream(); printStackTrace(new PrintWriter(out)); return
   * out.toString(); }
   */

  public static Exception getRootCause()
  {
    if (exception instanceof DefaultException) {
      // return ( (DefaultException) exception).getRootCause();
      return DefaultException.getRootCause();
    }
    return exception == null ? null : exception;
  }
}
