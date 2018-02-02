package com.ec.common.util;

import java.net.InetAddress;

public class Ping {
	public static boolean ping(String ipAddress, int timeOut) throws Exception {
		return InetAddress.getByName(ipAddress).isReachable(timeOut);
	}
	
	public static boolean ping(String ipAddress) throws Exception {
		return InetAddress.getByName(ipAddress).isReachable(1000);
	}
}
