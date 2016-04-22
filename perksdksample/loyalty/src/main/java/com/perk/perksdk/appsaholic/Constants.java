package com.perk.perksdk.appsaholic;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.perk.perksdk.PerkConfig;
import com.perk.perksdk.appsaholic.app.PerkAppInterface;
import com.perk.perksdk.appsaholic.v1.Domains;
import com.perk.perksdk.appsaholic.v1.claims.Claim;
import com.perk.perksdk.appsaholic.v1.common.Provider;

import java.util.ArrayList;

/**
 * <h1>Constants</h1>
 * Appsaholic API endpoints, parameters and strings
 */
public class Constants {

    public static final String HTTPS = "https";

    public static final String USER_ACCESS_TOKEN = "_loginAccessToken";
    public static final String USER_LOGIN_ID = "_loginUserID";
    public static final String TOTAL_POINTS = "total_perks";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_LAST_NAME = "user_last_name";
    public static final String USER_AVAILABLE_POINTS = "user_available_points";
    public static final String USER_PENDING_POINTS = "user_pending_points";
    public static final String USER_UNCLAIMED_EVENTS = "user_unclaimed_events";
    public static final String USER_PROFILE_IMAGE = "user_profile_image";
    public static final String PUBLISHER_AVAILABLE_POINTS = "publisher_available_points";
    // search
    public static String account = Domains.search_domainName + "/perk/account";
    public static String reset_new_password = Domains.search_domainName + "/accounts/reset_new_password?mobile=1";
    public static String viewallnotifications = Domains.search_domainName + "/perk/viewallnotifications?from=appsaholic";
    public static String logout = "/accounts/logout?from=appsaholic";

    // API Constants

    //MEMO:  These are ported to new endpoints.
    public static String init = Domains.api_domainName + "/v1/sdks/init.json";
    public static String events = Domains.api_domainName + "/v1/events.json";
    public static String status = Domains.api_domainName + "/v1/sdks/status.json";
    public static String unclaimed = Domains.api_domainName + "/v1/events/unclaimed.json?";
    public static String ads = Domains.api_domainName + "/v1/ads.json";
    public static String points = Domains.api_domainName + "/v1/users/appsaholic_id/points.json";
    public static String claims = Domains.api_domainName + "/v1/claims.json";
    public static String appsaholic_id = Domains.api_domainName + "/v1/users/appsaholic_id.json";
    public static String whitelisted = Domains.api_domainName + "/v1/countries/whitelisted.json?";
    public static String track = Domains.api_domainName + "/v1/ads/track.json";
    public static String end = Domains.api_domainName + "/v1/ads/end.json";
    public static String pubisher = Domains.api_domainName + "/v1/publishers/api_key.json";

//    // webview v3
//    public static String perk_sdk_start = Domains.appsaholic_domainName + "/dash/v3/perk/perk_sdk_start";
//    public static String unclaimed_events = Domains.appsaholic_domainName + "/dash/v3/perk/unclaimed_events";
//    public static String trailpay_single_offer = Domains.appsaholic_domainName + "/dash/v3/perk/trailpay_single_offer";
//    public static String pubnative_ads = Domains.appsaholic_domainName + "/dash/v3/perk/pubnative_ads?";
//    public static String tapsense_ads = Domains.appsaholic_domainName + "/dash/v3/perk/tapsense_ads?";
//    public static String take_survey = Domains.appsaholic_domainName + "/dash/v3/perk/take_survey?";
//    public static String perk_account = Domains.appsaholic_domainName + "/dash/v3/perk/perk_account"; //sign up/sign in/login
//    public static String success = Domains.appsaholic_domainName + "/dash/v3/perk/perk_account#success";
//    public static String datapoint_survey = Domains.appsaholic_domainName + "/dash/v3/perk/datapoint_survey";
//    public static String start_datapoint_survey = Domains.raw_appsaholic_domainName + "/dash/v3/perk/start_datapoint_survey";
//    public static String rewards_page = Domains.appsaholic_domainName + "/dash/v3/perk/perk_gift_card";
//    public static String redeem_coupon =  Domains.search_domainName + "/perk/waystospend/giftcard/";

    // webview v4
    public static String perk_sdk_start = Domains.appsaholic_domainName + "/dash/v4/perk/perk_sdk_start";
    public static String unclaimed_events = Domains.appsaholic_domainName + "/dash/v4/perk/unclaimed_events";
    public static String trailpay_single_offer = Domains.appsaholic_domainName + "/dash/v4/perk/trailpay_single_offer";
    public static String pubnative_ads = Domains.appsaholic_domainName + "/dash/v4/perk/pubnative_ads?";
    public static String tapsense_ads = Domains.appsaholic_domainName + "/dash/v4/perk/tapsense_ads?";
    public static String take_survey = Domains.appsaholic_domainName + "/dash/v4/perk/take_survey?";
    public static String perk_account = Domains.appsaholic_domainName + "/dash/v4/perk/perk_account"; //sign up/sign in/login
    public static String success = Domains.appsaholic_domainName + "/dash/v4/perk/perk_account#success";
    public static String datapoint_survey = Domains.appsaholic_domainName + "/dash/v4/perk/datapoint_survey";
    public static String start_datapoint_survey = Domains.raw_appsaholic_domainName + "/dash/v4/perk/start_datapoint_survey";
    public static String rewards_page = Domains.appsaholic_domainName + "/dash/v4/perk/perk_gift_card";
    public static String redeem_coupon =  Domains.search_domainName + "/perk/waystospend/giftcard/";

    // request header constants
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String DEVICE_INFO = "Device-Info";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String USER_AGENT = "User-Agent";

    // url parameter constants
    public static final String DEVICE_TYPE = "appsaholic_aphone";
    public static final String APPSAHOLIC_ID = "appsaholic_id";
    public static final String EVENT_ID = "event_id";
    public static final String PLACEMENT_ID = "placement_id";
    public static final String API_KEY = "api_key";
    public static final String AD_SOURCE = "ad_source";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APP_NAME = "app_name=";
    public static final String APP_VERSION = "app_version=";
    public static final String APP_BUNDLE_ID = "app_bundle_id=";
    public static final String PRODUCT_IDENTIFIER = "product_identifier=";
    public static final String DEVICE_MANUFACTURER = "device_manufacturer=";
    public static final String DEVICE_MODEL = "device_model=";
    public static final String DEVICE_RESOLUTION = "device_resolution=";
    public static final String OS_NAME_ANDROID = "os_name=Android;";
    public static final String OS_VERSION = "os_version=";
    public static final String ANDROID_ADVERTISING_ID = "android_advertising_id=";
    public static final String APPSAHOLIC_SDK_VERSION = "appsaholic_sdk_version=";
    public static final String API_KEY1 = "api_key=";
    public static final String TRACKING_ENABLED = "tracking_enabled=";
    public static final String FAN_AD_SOURCE = "FAN";

    /**
     * Returns a formatted Device-Info header.
     * <br>
     *
     * @param context application context
     * @return {@link String} device info header
     */
    public static String getDeviceInfo(Context context,PerkAppInterface app) {


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info;

        try {
            info = packageManager.getPackageInfo(context.getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "android-unknown";
        }

        return APP_NAME + context.getString(context.getApplicationInfo().labelRes) + ";"
                + APP_VERSION + info.versionName + ";"
                + APP_BUNDLE_ID + info.packageName + ";"
                + PRODUCT_IDENTIFIER + DEVICE_TYPE + ";"
                + DEVICE_MANUFACTURER + Build.MANUFACTURER + ";"
                + DEVICE_MODEL + Build.MODEL + ";"
                + DEVICE_RESOLUTION + metrics.heightPixels + "x" + metrics.widthPixels + ";"
                + OS_NAME_ANDROID + OS_VERSION + Build.VERSION.RELEASE + ";"
                + ANDROID_ADVERTISING_ID + PerkManager.getAdInfo().getId() + ";"
                + APPSAHOLIC_SDK_VERSION + "3.1" + ";"
                + API_KEY1 + PerkManager.getAppKey() + ";"
                + TRACKING_ENABLED + PerkManager.getAdInfo().isLimitAdTrackingEnabled();
    }

    /**
     * <h1>DummyPerkManager</h1>
     * <p/>
     * Catchall class for handling unsupported operations. This class should only be available if
     * initialization has failed or initialization is not possible.
     */
    // You end up here when you're bad at life. Don't be bad at life.
    public static class DummyPerkManager implements PerkConfig {
        @Override
        public boolean isUserLoggedIn(Context context) {
            return false;
        }

        @Override
        public boolean isSdkInitialized() {
            return false;
        }

        @Override
        public boolean isPassivePlayEnabled() {
            return false;
        }

        @Override
        public int getPassiveCountdownSeconds() {
            return 0;
        }

        @Override
        public String getAppsaholicId() {
            return "";
        }

        @Override
        public String getApiKey() {
            return "";
        }

        @Override
        public String getAdTag() {
            return "";
        }

        @Override
        public ArrayList<Provider> getClaimWaterfall(@NonNull Claim claim) {
            return new ArrayList<>();
        }

        @Override
        public ArrayList<Provider> getVideoWaterfall() {
            return new ArrayList<>();
        }

        @Override
        public ArrayList<Provider> getNotificationWaterfall() {
            return new ArrayList<>();
        }

        @Override
        public ArrayList<Provider> getSurveyWaterfall() {
            return new ArrayList<>();
        }

        @Override
        public boolean getSdkStatus() {
            return false;
        }

        @Override
        public String getDeviceIp(Context context) {
            return "127.0.0.1";
        }

        @Override
        public ArrayList<Provider> getDisplayAdWaterfall() {
            return new ArrayList<>();
        }
    }
}
