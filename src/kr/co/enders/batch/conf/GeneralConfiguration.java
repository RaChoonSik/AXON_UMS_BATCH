package kr.co.enders.batch.conf;

import kr.co.enders.batch.exceptions.ConfigurationException;
import kr.co.enders.batch.utils.StrConvertUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public abstract class GeneralConfiguration implements Config
{

	protected static Object lock         = new Object();

	protected Properties    _props;

	protected static long   lastModified = 0L;

	public GeneralConfiguration() throws ConfigurationException {
		_props = null;
	}

	public String get(String s)
	{
		return getString(s);
	}

	public boolean getBoolean(String s)
	{
		boolean flag = false;
		try {
			flag = (new Boolean(_props.getProperty(s))).booleanValue();
		} catch (Exception exception) {
			throw new IllegalArgumentException("Illegal Boolean Key : " + s);
		}
		return flag;
	}

	public int getInt(String s)
	{
		int i = -1;
		try {
			i = Integer.parseInt(_props.getProperty(s));
		} catch (Exception exception) {
			throw new IllegalArgumentException("Illegal Integer Key : " + s);
		}
		return i;
	}

	public long getLong(String s)
	{
		long l = -1L;
		try {
			l = Long.parseLong(_props.getProperty(s));
		} catch (Exception exception) {
			throw new IllegalArgumentException("Illegal Long Key : " + s);
		}
		return l;
	}

	public Properties getProperties()
	{
		return _props;
	}

	public String getString(String s)
	{
		String s1 = null;
		try {
			String s2 = _props.getProperty(s);
			if (s2 == null) { throw new Exception(); }
			s1 = StrConvertUtil.E2K(s2);
		} catch (Exception exception) {
			throw new IllegalArgumentException("Illegal String Key : " + s);
		}
		return s1;
	}

	protected abstract void initialize() throws ConfigurationException;

	public long lastModified()
	{
		return lastModified;
	}

	protected synchronized void loadConfigFile(File file) throws ConfigurationException
	{
		try {
			FileInputStream fileinputstream = new FileInputStream(file);
			_props.load(new BufferedInputStream(fileinputstream));
			fileinputstream.close();
		} catch (Exception exception) {
			_props = null;
			throw new ConfigurationException(getClass().getName() + " - Can't read configuration file:"
						+ file.getName() + " " + exception);
		}
	}
}
