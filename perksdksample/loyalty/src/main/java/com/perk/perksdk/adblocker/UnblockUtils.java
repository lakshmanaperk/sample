package com.perk.perksdk.adblocker;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class UnblockUtils {

	/*
	 * The emulator and ADP1 device both have a su binary in /system/xbin/su,
	 * but it doesn't allow apps to use it (su app_29 $ su su: uid 10029 not
	 * allowed to su).
	 * 
	 * Cyanogen used to have su in /system/bin/su, in newer versions it's a
	 * symlink to /system/xbin/su.
	 * 
	 * The Archos tablet has it in /data/bin/su, since they don't have write
	 * access to /system yet.
	 */
	static final String[] BinaryPlaces = { "/data/bin/", "/system/bin/",
			"/system/xbin/", "/sbin/", "/data/local/xbin/", "/data/local/bin/",
			"/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" };

	/**
	 * Determine the path of the su executable.
	 * 
	 * Code from https://github.com/miracle2k/android-autostarts, use under
	 * Apache License was agreed by Michael Elsdörfer
	 *
	 * @return String super user path
	 */
	public static String getSuPath() {
		for (String p : BinaryPlaces) {
			File su = new File(p + "su");
			if (su.exists()) {
				Log.d(RootCommands.TAG, "su found at: " + p);
				return su.getAbsolutePath();
			} else {
				Log.v(RootCommands.TAG, "No su in: " + p);
			}
		}
		Log.d(RootCommands.TAG, "No su found in a well-known location, "
				+ "will just use \"su\".");
		return "su";
	}

	/**
	 * This code is adapted from java.lang.ProcessBuilder.start().
	 * 
	 * The problem is that Android doesn't allow us to modify the map returned
	 * by ProcessBuilder.environment(), even though the docstring indicates that
	 * it should. This is because it simply returns the SystemEnvironment object
	 * that System.getenv() gives us. The relevant portion in the source code is
	 * marked as "// android changed", so presumably it's not the case in the
	 * original version of the Apache Harmony project.
	 * 
	 * Note that simply passing the environment variables we want to
	 * Process.exec won't be good enough, since that would override the
	 * environment we inherited completely.
	 * 
	 * We needed to be able to set a CLASSPATH environment variable for our new
	 * process in order to use the "app_process" command directly. Note:
	 * "app_process" takes arguments passed on to the Dalvik VM as well; this
	 * might be an alternative way to set the class path.
	 * 
	 * Code from https://github.com/miracle2k/android-autostarts, use under
	 * Apache License was agreed by Michael Elsdörfer
	 *
	 * @param baseDirectory {@link com.perk.perksdk.adblocker.UnblockUtils}
	 * @param command {@link com.perk.perksdk.adblocker.UnblockUtils}
	 * @param customAddedEnv {@link com.perk.perksdk.adblocker.UnblockUtils}
	 * @throws IOException {@link com.perk.perksdk.adblocker.UnblockUtils}
	 * @return Process {@link com.perk.perksdk.adblocker.UnblockUtils}
	 */
	public static Process runWithEnv(String command,
			ArrayList<String> customAddedEnv, String baseDirectory)
			throws IOException {

		Map<String, String> environment = System.getenv();
		String[] envArray = new String[environment.size()
				+ (customAddedEnv != null ? customAddedEnv.size() : 0)];
		int i = 0;
		for (Map.Entry<String, String> entry : environment.entrySet()) {
			envArray[i++] = entry.getKey() + "=" + entry.getValue();
		}
		if (customAddedEnv != null) {
			for (String entry : customAddedEnv) {
				envArray[i++] = entry;
			}
		}

		Process process;
		if (baseDirectory == null) {
			process = Runtime.getRuntime().exec(command, envArray, null);
		} else {
			process = Runtime.getRuntime().exec(command, envArray,
					new File(baseDirectory));
		}
		return process;
	}

}
