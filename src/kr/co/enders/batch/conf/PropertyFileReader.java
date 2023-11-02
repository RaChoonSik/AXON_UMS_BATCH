package kr.co.enders.batch.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyFileReader
{

	static Logger		logger   = Logger.getLogger(PropertyFileReader.class);

	static public String OUT_DIR  = new String();
	static public String CMPL_URL = new String();
	static public String RCL_URL  = new String();
	static public String INV_URL  = new String();
	static public String KMA_FILE = new String();
	static public String FSE_FILE = new String();
	static public String CS_FILE  = new String();

	public static String getPropertyInfo(String propName) throws FileNotFoundException, IOException
	{
		String retValue = null;
		Properties props = null;
		FileInputStream fis = null;
		try {
			// Reading properties file in Java
			props = new Properties();
			fis = new FileInputStream("D:/Data/CONF/downloadsiteurl.properties");
			// loading properties from properties file
			props.load(fis);
			// reading property
			retValue = props.getProperty(propName);
		} catch (FileNotFoundException fnfex) {
			fnfex.printStackTrace();
			logger.error("[getPropertyInfo] error >> " + fnfex);
		} catch (IOException ioex) {
			ioex.printStackTrace();
			logger.error("[getPropertyInfo] error >> " + ioex);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("[getPropertyInfo] error >> " + ex);
		} finally {
			try {
				if (fis != null) fis.close();
			} catch (IOException ignore) {
			}
		}
	
		return retValue;
	}

	public static void getPropertyInfo() throws FileNotFoundException, IOException
	{
		Properties props = null;
		FileInputStream fis = null;
		try {
			// Reading properties file in Java
			props = new Properties();
			fis = new FileInputStream("D:/Data/CONF/downloadsiteurl.properties");
			// loading properties from properties file
			props.load(fis);
			// reading property
			OUT_DIR = props.getProperty("OUT_DIR");
			CMPL_URL = props.getProperty("CMPL");
			RCL_URL = props.getProperty("RCL");
			INV_URL = props.getProperty("INV");
			KMA_FILE = props.getProperty("KMA_FILE");
			FSE_FILE = props.getProperty("FSE_FILE");
			CS_FILE = props.getProperty("CS_FILE");
		} catch (FileNotFoundException fnfex) {
			fnfex.printStackTrace();
			logger.error("[getPropertyInfo] error >> " + fnfex);
		} catch (IOException ioex) {
			ioex.printStackTrace();
			logger.error("[getPropertyInfo] error >> " + ioex);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("[getPropertyInfo] error >> " + ex);
		} finally {
			try {
				if (fis != null) fis.close();
			} catch (IOException ignore) {
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		// TODO Auto-generated method stub
		try {
			PropertyFileReader.getPropertyInfo();
			logger.info("----------------------------");
			logger.info("LOCAL_DIR : " + OUT_DIR);
			logger.info("CMPL_URL  : " + CMPL_URL);
			logger.info("RCL_URL   : " + RCL_URL);
			logger.info("INV_URL   : " + INV_URL);
			logger.info("KMA_FILE  : " + KMA_FILE);
			logger.info("----------------------------");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("[main] error >> " + ex);
		}
	}
}
