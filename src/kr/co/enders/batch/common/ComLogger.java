package kr.co.enders.batch.common;

import org.apache.log4j.Logger;

public class ComLogger {
	protected static final Logger logger = Logger.getLogger(ComLogger.class);
	public static void debug(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}
}
