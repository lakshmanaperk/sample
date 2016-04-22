package com.perk.perksdk;

/**
 * <h1>Utils</h1>
 * Contains all the basic device related functions,
 * like resolution of the device, getting advertisingID and
 * also checking host files for any adblock installed on the device.
 *
 * @author Perk.com
 * @version 1.0
 * @since 2014-12-01
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.perk.perksdk.appsaholic.Constants;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

public class Utils {


    /**
     * "hosts" file possible paths
     */
    private static final String[] HOSTS_FILES = {"/etc/hosts",
            "/system/etc/hosts", "/data/data/hosts"};
    /**
     * Pattern to search in hosts file
     */
    private static final String[] HOSTS_FILE_PATTERNS = {"aerserv", "facebook"};
    public static String TAG = "Perk";
    /**
     * Strings used across the application. These strings update according to
     * per session of the SDK.
     */
    public static ProgressDialog pdia;
    public static String m_strAppKey = "";
    public static String m_strAdvertisingId = "";
    public static String m_strNotificationText = "";
    public static int m_strEventPoints = 0;
    public static String m_strEventExtra = "";
    public static String m_strSdkStatus = "";
    public static String m_strAdBlockStatus = "";
    public static String m_strSubId = "";
    public static String m_strUserName = "";
    /**
     * Hardcoded Strings
     */
    public static String m_strSdkVersion = "3.1";
    public static String m_strEventAdCheck = "";
    public static String m_surveyIncomplete = "";
    /**
     * SDK Boolean Values
     */
    public static boolean m_bPortalDestination = true;
    public static boolean mCustomNotification = false;
    public static boolean m_bIsUnclaimed = false;
    public static boolean m_bIsBlockingDialog = false;
    public static boolean m_bPrePortalAd = true;
    public static boolean m_dataPointsRequest = false;
    public static boolean m_pointEarned = false;
    public static boolean m_suppressNotifications = false;
    public static String[] m_Countries;
    public static Context m_objContext;
    public static Context m_objMainContext;
    public static String m_strDeviceId;
    public static DisplayMetrics m_objMetrics;
    public static PackageInfo m_objPInfo;
    public static Context _context;
    public static String m_sDeviceInfo = "";
    public static boolean isRewardsCalled = false;
    public static PerkCustomInterface perkInterface;

    /**
     * AdBlocker Stuff following
     */
    public static String m_sUserAgent = "";

    public static void setContext(Context context) {
        m_objContext = context;
        m_strDeviceId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);

        try {
            m_objMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(m_objMetrics);
        }
        catch (Exception e) {
        }

        try {
            m_objPInfo = m_objContext.getPackageManager().getPackageInfo(m_objContext.getPackageName(), PackageManager.GET_PERMISSIONS);
        }
        catch (Exception e) {
        }

        setDeviceInfo();
    }

    public static void setMainContext(Context context) {
        m_objMainContext = context;
    }

    public static String getResolution(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics.widthPixels + "x" + metrics.heightPixels;
    }

    public static boolean detectAdBlockers(Context c) {
        _context = c;

        return detectInHostFile();
    }

    private static boolean detectInHostFile() {
        // search a readable hosts file
        File hostsFile = null;
        for (final String fileName : HOSTS_FILES) {
            hostsFile = new File(fileName);
            if (hostsFile.canRead()) {
                break;
            }
        }
        // and read it
        if (hostsFile != null && hostsFile.canRead()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(hostsFile));
                String ln;
                while ((ln = in.readLine()) != null) {
                    if (ln.length() > 0 && ln.charAt(0) != '#') {
                        for (final String pattern : HOSTS_FILE_PATTERNS) {

                            if (ln.contains(pattern)) {

                                return true;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                return false;
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (IOException e) {
                }
            }
        }
        return false;
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */

    public static void setDeviceInfo() {

        try {

            int width = 0;
            int height = 0;
            String device_id = "";

            String app_name = "";

            try {

                width = m_objMetrics.widthPixels;
                height = m_objMetrics.heightPixels;

                device_id = Secure.getString(Utils.m_objContext
                        .getContentResolver(), Secure.ANDROID_ID);
                if (device_id == null || device_id.equals("9774d56d682e549c")
                        || device_id.length() < 15) {

                    final SecureRandom random = new SecureRandom();
                    device_id = new BigInteger(64, random).toString(16);
                }


            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try {


                try {

                    PackageManager p = Utils.m_objContext.getPackageManager();
                    app_name = p.getApplicationLabel(m_objPInfo.applicationInfo).toString();

                }
                catch (Exception e) {
                }


            }
            catch (Exception e) {
            }

            StringBuffer sBufferDeviceInfo = new StringBuffer(
                    "app_name=" + app_name + ";");
            sBufferDeviceInfo.append("app_version=");
            sBufferDeviceInfo.append(m_objPInfo.versionName);
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("app_bundle_id=");
            sBufferDeviceInfo.append(m_objPInfo.packageName);
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("product_identifier=");
            sBufferDeviceInfo.append(Constants.DEVICE_TYPE);
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("device_manufacturer=");
            sBufferDeviceInfo.append(Build.MANUFACTURER);
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("device_model=");
            sBufferDeviceInfo.append(Build.MODEL);
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("device_resolution=");
            sBufferDeviceInfo.append(String.valueOf(height) + "x"
                    + String.valueOf(width));
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("os_name=Android;");
            sBufferDeviceInfo.append("os_version=");
            sBufferDeviceInfo.append(Build.VERSION.RELEASE);
            /*sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("open_udid=");
			sBufferDeviceInfo.append(device_id);*/
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("android_advertising_id=");
            sBufferDeviceInfo.append(m_strAdvertisingId);
            sBufferDeviceInfo.append(";");
            /*sBufferDeviceInfo.append("android_id=");
            sBufferDeviceInfo.append(device_id);
			sBufferDeviceInfo.append(";");*/
            sBufferDeviceInfo.append("appsaholic_sdk_version=");
            sBufferDeviceInfo.append(m_strSdkVersion);
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("api_key=");
            sBufferDeviceInfo.append(m_strAppKey);

            //Added for Dixita and FYBER integration in build 3.1
            sBufferDeviceInfo.append(";");
            sBufferDeviceInfo.append("tracking_enabled=");
            sBufferDeviceInfo.append(AdvertisingIdClient.getAdvertisingIdInfo(Utils.m_objMainContext).isLimitAdTrackingEnabled());

            m_sDeviceInfo = sBufferDeviceInfo.toString();

            StringBuffer sBuffer = new StringBuffer(app_name);
            sBuffer.append(m_objPInfo.versionName);
            sBuffer.append(" (");
            sBuffer.append(Build.MANUFACTURER);
            sBuffer.append("; ");
            sBuffer.append(Build.MODEL);
            sBuffer.append("; ");
            sBuffer.append(String.valueOf(height) + "x" + String.valueOf(width));
            sBuffer.append("; ");
            sBuffer.append("Android");
            sBuffer.append("; ");
            sBuffer.append(Build.VERSION.RELEASE);
            sBuffer.append(") PALSDKA");

            m_sUserAgent = sBuffer.toString();
        }
        catch (Exception e) {
        }
    }
}
