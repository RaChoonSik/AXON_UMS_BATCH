package kr.co.enders.batch.exceptions;

/**
 * @author HO JUNG CHOI
 * @version 1.0
 */

public class ConfigurationException extends DefaultException
{
  /**
   * 
   */
  private static final long serialVersionUID = 1395717335755779626L;

  public ConfigurationException() {
    super();
  }

  public ConfigurationException(String s) {
    super(s);
  }

  public ConfigurationException(String errorCode, String error) {
    super(error);
    super.setErrorCode(errorCode);
  }

  public ConfigurationException(String errorCode, String errorMessage, String error) {
    super(error);
    super.setErrorCode(errorCode);
    super.setErrorMessage(errorMessage);
  }
}
