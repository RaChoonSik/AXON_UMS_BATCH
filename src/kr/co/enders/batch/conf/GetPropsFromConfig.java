package kr.co.enders.batch.conf;

/**
 * @author JUN HEE KIM
 * @version 1.0
 */

public final class GetPropsFromConfig
{

	/*
	 * Don't let anyone instantiate this class
	 */
	private GetPropsFromConfig() {
	}

	public static String get(String target)
	{
		String path = "/";

		try {
			Config conf = new Configuration();
			path = conf.get(target);
			return path;
		} catch (Exception e) {
			return path;
		}
	}
}
