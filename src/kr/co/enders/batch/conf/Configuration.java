package kr.co.enders.batch.conf;

import kr.co.enders.batch.exceptions.ConfigurationException;
import kr.co.enders.batch.utils.StringUtil;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration extends GeneralConfiguration
{
  static Logger         logger            = Logger.getLogger(Configuration.class);

  private static long   target_last_modified = 0L;
  private static String target_file_name     = null;

  public Configuration() throws ConfigurationException {
    try {
      initialize();
    }
    catch (Exception exception) {
      logger.error("[Configuration] error >> " + StringUtil.stackTraceToString(exception));
    }
  }

  protected void initialize() throws ConfigurationException
  {
    synchronized (GeneralConfiguration.lock) {
      try {
        boolean flag = false;
        File file = new File(target_file_name);
        if (!file.canRead()) { throw new ConfigurationException(
                                                                getClass().getName()
                                                                + " - Can't open configuration file: "
                                                                + target_file_name); }
        if ((target_last_modified != file.lastModified()) || (_props == null)) {
          flag = true;
        }
        if (flag) {
          _props = new Properties();
          loadConfigFile(file);
          target_last_modified = file.lastModified();
          GeneralConfiguration.lastModified = System.currentTimeMillis();
        }
      }
      catch (ConfigurationException configurationexception) {
        GeneralConfiguration.lastModified = 0L;
        target_last_modified = 0L;
        throw configurationexception;
      }
      catch (Exception exception) {
        GeneralConfiguration.lastModified = 0L;
        target_last_modified = 0L;
        throw new ConfigurationException(getClass().getName()
                                         + " - Can't load configuration file: "
                                         + exception.getMessage());
      }
    }
  }

  static {
    File file = null;
    //file = new File("D:\\Data\\CONF", "booktown.properties");
    //file = new File("C:/eclipse/workspace/BookTown_IF/data/CONF", "booktown.properties");    
    file = new File("/gritis/cfg", "booktown.properties");
    target_file_name = System.getProperty("common.config.file", file.getAbsolutePath());
  }
}
