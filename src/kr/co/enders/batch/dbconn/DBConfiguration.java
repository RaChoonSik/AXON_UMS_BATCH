package kr.co.enders.batch.dbconn;

/**
 * @(#) DBConfiguration.java Copyright(c) 2000-2001 by Sun Professional Services, Sun Microsystems
 *      Korea, Ltd. All Rights Reserved NOTICE ! You can copy or redistribute this code freely, but
 *      you should not remove the information about the copyright notice and the author.
 * @author SooKyung Lim, sookyung.lim@korea.sun.com
 */

import kr.co.enders.batch.conf.Config;
import kr.co.enders.batch.conf.Configuration;
import kr.co.enders.batch.conf.GeneralConfiguration;
import kr.co.enders.batch.exceptions.ConfigurationException;
import kr.co.enders.batch.utils.StringUtil;

import java.io.File;

import org.apache.log4j.Logger;

public class DBConfiguration extends GeneralConfiguration
{
	/**
	 * 로거 정의
	 */
	static Logger         logger           = Logger.getLogger(DBConfiguration.class);

	private static long   db_last_modified = 0;

	private static String file_name        = null;

	public DBConfiguration() throws ConfigurationException {
		super();
		Config config = new Configuration();
		file_name = config.get("common.config.file");
		initialize();
	}
	
	protected void initialize() throws ConfigurationException
	{
		synchronized (lock) {
			try {
				File file = new File(file_name);
				if (!file.canRead()) { throw new ConfigurationException(
                                                                this.getClass().getName()
                                                                + " - Can't open db configuration file: "
                                                                + file_name); }

				// needUpdate
				if ((db_last_modified != file.lastModified()) || (_props == null)) {
					_props = new java.util.Properties();
					loadConfigFile(file);
					db_last_modified = file.lastModified();
					lastModified = System.currentTimeMillis();
				} // end if
			} catch (ConfigurationException e) {
				lastModified = 0;
				db_last_modified = 0;
				throw e;
			} catch (Exception e) {
				lastModified = 0;
				db_last_modified = 0;
				throw new ConfigurationException(this.getClass().getName()
                                         + " - Can't load configuration file: " + e.getMessage());
			}
		} // end of sunchronized(lock);
	}

	public static void main(String[] args)
	{
		while (true) {
			try {
				Config config = new DBConfiguration();
				logger.info("db.url=" + config.get("db.url"));
				Thread.sleep(5000);
			} catch (Exception e) {
				logger.error("db conn exception: " + StringUtil.stackTraceToString(e));
			}
		}
	}
}
