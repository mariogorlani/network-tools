package com.nsn.audit.utils;

import java.text.SimpleDateFormat;

import org.snmp4j.mp.SnmpConstants;

public final class GenericDefinitions {

	public static final String VarbindSeparator = ";";
	public static final String RowSeparator = "|";
	public static final String Online = "Online";
	public static final String Disconnected = "Disconnected";
	public static final String Disabled = "Disabled";
	public static final String Offline = "Offline";
	
	// Connection parameters
	public static String ipAddress="127.0.0.1";
	public static String port="161";
	public static int snmpVersion = SnmpConstants.version2c;
	public static int retries = 3;
	public static String readonlyCommunity = "readonly";//no more in use, see devices.xml property
	public static String readwriteCommunity = "sysmanager";
	public static int timeout = 8000;

	public static String iniKey_readonlyCommunity = "readonly_community";
	public static String iniKey_readwriteCommunity = "readwrite_community";
	public static String iniKey_maxThreadPool = "maxThreadPool";
	public static String iniKey_retries = "retries";
	public static String iniKey_timeout = "timeout";

	public static String sysDescr = ".1.3.6.1.2.1.1.1.0";
	public static String systemName = "1.3.6.1.2.1.1.5.0";
	public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static int maxThreadPool;
}
