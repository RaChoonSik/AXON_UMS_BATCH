package kr.co.enders.batch.conf;

/**
 * @author Jun Hee Kim
 * @version 1.0
 */

import java.util.Properties;

public interface Config
{
  public abstract String get(String s);

  public abstract boolean getBoolean(String s);

  public abstract int getInt(String s);

  public abstract long getLong(String s);

  public abstract Properties getProperties();

  public abstract String getString(String s);

  public abstract long lastModified();
}
