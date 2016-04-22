package com.perk.perksdk.adblocker;

public class Constants {

	public static boolean DEBUG;
	public static final boolean DEBUG_UPDATE_CHECK_SERVICE = false;
	public static final boolean DEBUG_DISABLE_ROOT_CHECK = false;

	public static final String TAG = "PerkTvAdBlocker";

	public static final String PREFS_NAME = "preferences";

	public static final String LOCALHOST_IPv4 = "127.0.0.1";
	public static final String LOCALHOST_IPv6 = "::1";
	public static final String WHITELIST_ENTRY = "white";
	public static final String BOGUS_IPv4 = "0.0.0.0";
	public static final String LOCALHOST_HOSTNAME = "localhost";

	public static final String DOWNLOADED_HOSTS_FILENAME = "hosts_downloaded";
	public static final String HOSTS_FILENAME = "hosts";
	public static final String LINE_SEPERATOR = System.getProperty(
			"line.separator", "\n");
	public static final String FILE_SEPERATOR = System.getProperty(
			"file.separator", "/");

	public static final String COMMAND_CHOWN = "chown 0:0";
	public static final String COMMAND_CHMOD_644 = "chmod 644";
	public static final String COMMAND_CHMOD_666 = "chmod 666";
	public static final String COMMAND_LN = "ln -s";
	public static final String COMMAND_RM = "rm -f";
	public static final String COMMAND_MKDIR = "mkdir -p";

	public static final String WEBSERVER_EXECUTEABLE = "blank_webserver";

	public static final String TCPDUMP_EXECUTEABLE = "tcpdump";
	public static final String TCPDUMP_LOG = "dns_log.txt";

	public static final String ANDROID_SYSTEM_PATH = System.getProperty(
			"java.home", "/system");
	public static final String ANDROID_SYSTEM_ETC_HOSTS = ANDROID_SYSTEM_PATH
			+ FILE_SEPERATOR + "etc" + FILE_SEPERATOR + HOSTS_FILENAME;
	public static final String ANDROID_DATA_DATA_HOSTS = FILE_SEPERATOR
			+ "data" + FILE_SEPERATOR + "data" + FILE_SEPERATOR
			+ HOSTS_FILENAME;

}