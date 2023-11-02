package kr.co.enders.batch.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ProcessInfoGetter {
	private static String pid = null;
	static {
		RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
		String[] info = rt.getName().split("@");
		pid = "PID=" + info[0] + " / HOSTNAME=" + info[1]+" ";
	}
	public static String getPid(){
		return pid;
	}
}
