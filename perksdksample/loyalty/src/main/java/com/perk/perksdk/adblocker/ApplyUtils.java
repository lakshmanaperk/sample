package com.perk.perksdk.adblocker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;

public class ApplyUtils {
	/**
	 * Check if there is enough space on partition where target is located
	 * 
	 * @param size
	 *            size of file to put on partition
	 * @param target
	 *            path where to put the file
	 * 
	 * @return true if it will fit on partition of target, false if it will not
	 *         fit.
	 */
	public static boolean hasEnoughSpaceOnPartition(String target, long size) {
		try {
			// new File(target).getFreeSpace() (API 9) is not working on data
			// partition

			// get directory without file
			String directory = new File(target).getParent().toString();

			StatFs stat = new StatFs(directory);
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			long availableSpace = availableBlocks * blockSize;

			Log.i(Constants.TAG, "Checking for enough space: Target: " + target
					+ ", directory: " + directory + " size: " + size
					+ ", availableSpace: " + availableSpace);

			if (size < availableSpace) {
				return true;
			} else {
				Log.e(Constants.TAG, "Not enough space on partition!");
				return false;
			}
		} catch (Exception e) {
			// if new StatFs(directory) fails catch IllegalArgumentException and
			// just return true as
			// workaround
			Log.e(Constants.TAG,
					"Problem while getting available space on partition!", e);
			return true;
		}
	}

	/**
	 * Copy hosts file from private storage of AdAway to internal partition
	 * using RootTools
	 * 
	 * @throws NotEnoughSpaceException
	 *             RemountException CopyException
	 */
	public static void copyHostsFile(Context context, String target, Shell shell)
			throws NotEnoughSpaceException, RemountException, CommandException {
		Log.i(Constants.TAG, "Copy hosts file with target: " + target);
		String privateDir = context.getFilesDir().getAbsolutePath();
		String privateFile = privateDir + File.separator
				+ Constants.HOSTS_FILENAME;

		// if the target has a trailing slash, it is not a valid target!
		if (target.endsWith("/")) {
			Log.e(Constants.TAG,
					"Custom target ends with trailing slash, it is not a valid target!");
			throw new CommandException();
		}

		if (!target.equals(Constants.ANDROID_SYSTEM_ETC_HOSTS)) {
			/*
			 * If custom target like /data/etc/hosts is set, create missing
			 * directories for writing this file
			 */
			createDirectories(target, shell);
		}

		/* check for space on partition */
		long size = new File(privateFile).length();
		Log.i(Constants.TAG, "Size of hosts file: " + size);
		if (!hasEnoughSpaceOnPartition(target, size)) {
			throw new NotEnoughSpaceException();
		}

		Toolbox tb = new Toolbox(shell);

		/* Execute commands */
		try {
			// remount for write access
			Log.i(Constants.TAG, "Remounting for RW...");
			if (!tb.remount(target, "RW")) {
				Log.e(Constants.TAG,
						"Remounting as RW failed! Probably not a problem!");
			}

			// remove before copying when using /system/etc/hosts
			if (target.equals(Constants.ANDROID_SYSTEM_ETC_HOSTS)) {
				SimpleCommand command = new SimpleCommand(Constants.COMMAND_RM
						+ " " + target);
				shell.add(command).waitForFinish();
			}

			// copy file
			if (!tb.copyFile(privateFile, target, false, false)) {
				throw new CommandException();
			}

			// execute commands: chown, chmod
			SimpleCommand command = new SimpleCommand(Constants.COMMAND_CHOWN
					+ " " + target, Constants.COMMAND_CHMOD_644 + " " + target);
			shell.add(command).waitForFinish();
		} catch (Exception e) {
			Log.e(Constants.TAG, "Exception!", e);

			throw new CommandException();
		} finally {
			if (target.equals(Constants.ANDROID_SYSTEM_ETC_HOSTS)) {
				// after all remount system back as read only
				Log.i(Constants.TAG, "Remounting back to RO...");
				if (!tb.remount(target, "RO")) {
					Log.e(Constants.TAG,
							"Remounting failed in finally! Probably not a problem!");
				}
			}
		}
	}

	/**
	 * Create symlink from /system/etc/hosts to /data/data/hosts
	 * 
	 * @throws RemountException
	 *             CommandException
	 */
	public static void createSymlink(String target) throws RemountException,
			CommandException {
		Shell rootShell = null;
		try {
			rootShell = Shell.startRootShell();
		} catch (Exception e) {
			throw new CommandException("Problem opening root shell!");
		}
		Toolbox tb = new Toolbox(rootShell);

		/* remount /system/etc for write access */
		if (!tb.remount(Constants.ANDROID_SYSTEM_ETC_HOSTS, "RW")) {
			throw new RemountException();
		}

		/* Execute commands */
		try {
			// create symlink
			SimpleCommand command = new SimpleCommand(Constants.COMMAND_RM
					+ " " + Constants.ANDROID_SYSTEM_ETC_HOSTS,
					Constants.COMMAND_LN + " " + target + " "
							+ Constants.ANDROID_SYSTEM_ETC_HOSTS,
					Constants.COMMAND_CHOWN + " " + target,
					Constants.COMMAND_CHMOD_644 + " " + target);

			rootShell.add(command).waitForFinish();
		} catch (Exception e) {
			throw new CommandException();
		} finally {
			// after all remount system back as read only
			tb.remount(Constants.ANDROID_SYSTEM_ETC_HOSTS, "RO");

			try {
				rootShell.close();
			} catch (IOException e) {
				throw new CommandException("Problem closing root shell!");
			}
		}
	}

	/**
	 * Checks whether /system/etc/hosts is a symlink and pointing to the target
	 * or not
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isSymlinkCorrect(String target, Shell shell) {
		Log.i(Constants.TAG,
				"Checking whether /system/etc/hosts is a symlink and pointing to "
						+ target + " or not.");

		Toolbox tb = new Toolbox(shell);
		String symlink = null;
		try {
			symlink = tb.getSymlink(Constants.ANDROID_SYSTEM_ETC_HOSTS);
		} catch (Exception e) {
			Log.e(Constants.TAG, "Problem getting symlink!", e);
			return false;
		}

		Log.d(Constants.TAG, "symlink: " + symlink + "; target: " + target);

		if (symlink != null && symlink.equals(target)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create directories if missing, if /data/etc/hosts is set as target, this
	 * creates /data/etc/ directories. Needs RW on partition!
	 * 
	 * @throws CommandException
	 */
	public static void createDirectories(String target, Shell shell)
			throws CommandException {
		/* Execute commands */
		try {
			// get directory without file
			String directory = new File(target).getParent().toString();

			// create directories
			try {
				SimpleCommand mkdirCommand = new SimpleCommand(
						Constants.COMMAND_MKDIR + " " + directory);

				shell.add(mkdirCommand).waitForFinish();
			} catch (Exception e) {
				Log.e(Constants.TAG, "Mkdir Exception", e);
			}
		} catch (Exception e) {
			Log.e(Constants.TAG, "Exception!", e);

			throw new CommandException();
		}
	}

	/**
	 * Returns true when an APN proxy is set. This means data is routed through
	 * this proxy. As a result hostname blocking does not work reliable because
	 * images can come from a different hostname!
	 * 
	 * @param context
	 * @return true if proxy is set
	 */
	public static boolean isApnProxySet(Context context) {
		boolean result = false; // default to false!

		try {
			final Uri defaultApnUri = Uri
					.parse("content://telephony/carriers/preferapn");
			final String[] projection = new String[] { "_id", "name", "proxy" };
			// get cursor for default apns
			Cursor cursor = context.getContentResolver().query(defaultApnUri,
					projection, null, null, null);

			// get default apn
			if (cursor != null) {
				// get columns
				int nameColumn = cursor.getColumnIndex("name");
				int proxyColumn = cursor.getColumnIndex("proxy");

				if (cursor.moveToFirst()) {
					// get name and proxy
					String name = cursor.getString(nameColumn);
					String proxy = cursor.getString(proxyColumn);

					Log.d(Constants.TAG, "APN " + name + " has proxy: " + proxy);

					// if it contains anything that is not a whitespace
					if (!proxy.matches("\\s*")) {
						result = true;
					}
				}

				cursor.close();
			} else {
				Log.d(Constants.TAG, "Could not get APN cursor!");
			}
		} catch (Exception e) {
			Log.e(Constants.TAG, "Error while getting default APN!", e);
			// ignore exception, result = false
		}

		return result;
	}
}