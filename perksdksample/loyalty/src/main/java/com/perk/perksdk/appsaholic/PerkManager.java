package com.perk.perksdk.appsaholic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.appsaholic.CommercialBreakSDK;
import com.appsaholic.adsdks.CommercialBreakSDKInitCallback;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.gson.Gson;
import com.perk.perksdk.Functions;
import com.perk.perksdk.PerkConfig;
import com.perk.perksdk.PerkCustomInterface;
import com.perk.perksdk.PerkListener;
import com.perk.perksdk.PerkSDKActivity;
import com.perk.perksdk.Utils;
import com.perk.perksdk.appsaholic.app.PerkAppInterface;
import com.perk.perksdk.appsaholic.v1.Country.Country;
import com.perk.perksdk.appsaholic.v1.claims.Claim;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEventManager;
import com.perk.perksdk.appsaholic.v1.events.Event;
import com.perk.perksdk.appsaholic.v1.events.TrackEvent;
import com.perk.perksdk.appsaholic.v1.id.Id;
import com.perk.perksdk.appsaholic.v1.init.DummyInit;
import com.perk.perksdk.appsaholic.v1.init.Init;
import com.perk.perksdk.appsaholic.v1.init.InitInterface;
import com.perk.perksdk.appsaholic.v1.publisher.Publisher;
import com.perk.perksdk.appsaholic.v1.status.Status;
import com.perk.perksdk.appsaholic.v1.unclaimed.Unclaimed;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <h1>PerkManager</h1>
 *
 * @author Perk.com
 * @version %I%, %U%
 * @since 1.0
 */
public class PerkManager implements PerkConfig {

    public static final int DEFAULT_MAX_ADS = 1;

    // todo:PerkManager should implement PerkListener
    private static AdvertisingIdClient.Info adInfo;
    private static InitInterface mInit = new DummyInit();
    private static PerkAppInterface mApp;
    private static String mApiKey;
    private static String mDeviceInfo;
    private static boolean mNotify = true;
    private static PerkManager sInstance;

//    public int PERK_MANAGER_EVENT_START = 0;
//    public int PERK_MANAGER_EVENT_ON_CB_AD_COMPLETE = 1;
//    public int PERK_MANAGER_EVENT_ON_CB_AD_START = 2;
//    public int PERK_MANAGER_EVENT_ON_CB_COMPLETE = 3;
//    public int PERK_MANAGER_EVENT_ON_CB_START = 4;
//    public int PERK_MANAGER_EVENT_ON_CB_ADS_COMPLETE = 5;
//    public int PERK_MANAGER_EVENT_REWARD_FOR_AD = 6;
//    public int PERK_MANAGER_EVENT_REWARD_FAILED_FOR_AD = 7;
//    public int PERK_MANAGER_EVENT_END_EVENT_FAIL = 8;
//    public int PERK_MANAGER_EVENT_AD_UNAVAILABLE = 9;
//    public int PERK_MANAGER_EVENT_LEAVING_APP = 10;
//    public int PERK_MANAGER_EVENT_LOADING_DATA_POINTS = 11;
//    public int PERK_MANAGER_EVENT_LOADING_PORTAL = 12;
//    public int PERK_MANAGER_EVENT_SDK_INIT_FAILED = 13;
//    public int PERK_MANAGER_EVENT_TOGGLE_SDK_STATUS = 14;
//    public int PERK_MANAGER_EVENT_USER_LOGGED_IN = 15;
//    public int PERK_MANAGER_EVENT_USER_LOGGED_OUT = 16;
//    public int PERK_MANAGER_EVENT_CLAIM_EVENT_SUCCESS = 17;
//    public int PERK_MANAGER_EVENT_CLAIM_EVENT_FAIL = 18;
//    public int PERK_MANAGER_EVENT_TRACK_EVENT_SUCCESS = 19;
//    public int PERK_MANAGER_EVENT_TRACK_EVENT_FAIL = 20;
//    public int PERK_MANAGER_EVENT_PUBLISHER_BALANCE_SUCCESS = 21;
//    public int PERK_MANAGER_EVENT_PUBLISHER_BALANCE_FAIL = 22;
//    public int PERK_MANAGER_EVENT_UNCLAIMED_EVENT_SUCCESS = 23;
//    public int PERK_MANAGER_EVENT_UNCLAIMED_EVENT_FAIL = 24;
//    public int PERK_MANAGER_EVENT_USER_INFO_SUCCESS = 25;
//    public int PERK_MANAGER_EVENT_USER_INFO_FAIL = 26;
//    public int PERK_MANAGER_EVENT_END = 100;


    public static String PERK_MANAGER_SDK_CLIAM_CLOSE_BANNER = "SDKClaimBannerCloseNotification";
    public static String PERK_MANAGER_SDK_POINTS_AWARDED_WITHOUT_AD= "SDKPointAwardedForNoAdSupport";
    public static String PERK_MANAGER_SDK_PORTAL_CLOSE= "SDKPortalCloseNotification";
    public static String PERK_MANAGER_SDK_LOGIN_PORTAL_CLOSE= "SDKLoginPortalCloseNotification";
    public static String PERK_MANAGER_SDK_UNCLAIMED_PORTAL_CLOSE= "UnclaimedNotificationPortalClose";
    public static String PERK_MANAGER_NOTIFICATION_STRING = "PERK_MANAGER_NOTIFICATION";

    public static AdvertisingIdClient.Info getAdInfo() {
        return adInfo;
    }


    private PerkManager() {
    }

    /**
     * <a href="https://github.com/Appsaholic/example-loyalty-app-android-extended/issues/39">github issue</a>
     *
     * @param activity
     */
    private static void getAdvertisingIdInfo(final Activity activity) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(activity);
                    Utils.m_strAdvertisingId = adInfo.getId();
                }
                catch (Exception e) {
                    //todo:prompt user to install google play services {NullPointerException}
                    PerkManager.notifyApp(e.getMessage());
                    e.printStackTrace();
                }

                mDeviceInfo = Constants.getDeviceInfo(activity,mApp);
                Utils.setContext(Utils.m_objContext);
                Init.get(activity, new RequestListener<Init>() {
                    @Override
                    public void success(Init init) {
                        PerkManager.notifyApp("PerkManager successfully initialized");
                        mInit = init;
                        if(mInit.isSdkEnabled() ==  true) {
                            Utils.m_strSdkStatus = "0";
                        }
                        else {
                            Utils.m_strSdkStatus = "1";
                        }
                        getApp().onInit(mInit.isSdkEnabled(), "");
                    }

                    @Override
                    public void failure(String message) {
                        mInit = new DummyInit();
                        PerkManager.notifyApp("[Init.onLoadListener] failed with message: " + message);
                    }
                });
            }
        }).start();
    }

    /**
     * <p/>
     * Initialize the Perk SDK and set the main context of the activity.
     * <p/>
     * <pre>
     * Deprecated in v4.0. Use {@link PerkManager#startSession(Activity)}
     * </pre>
     *
     * @param activity application context
     * @param key      application identifier
     */
    @Deprecated
    public static void startSession(Activity activity, String key) {

        Utils.setContext(activity);
        Utils.setMainContext(activity);
        getAdvertisingIdInfo(activity);
    }

    /**
     * <p/>
     * Initialize the Perk SDK, establish the application's key and set the main
     * context of the activity.
     * <p/>
     * <pre>
     * Deprecated in v4.0. Use {@link PerkManager#startSession(Activity)}
     * </pre>
     *
     * @param activity
     * @param key               application identifier
     * @param perkEventListener custom event listener for passing PerkSDK events to the application
     */
    @Deprecated
    public static void startSession(Activity activity, String key, PerkListener perkEventListener) {

        Utils.setMainContext(activity);
        Utils.setContext(activity);
        getAdvertisingIdInfo(activity);
//        getApp() = perkEventListener;
    }

    /**
     * Initialize the Perk SDK
     *
     * @param activity application context where context implements {@link PerkAppInterface}
     * @param appInterface Interface that receives callbacks (can be same activity or other class)
     * @param apiKey API KEY of the app which is created on perk dash board
     * onInit(boolean statusCode, String statusMessage) Callback will be called upon startsession method completion
     */
    public static void startSession(final Activity activity,PerkAppInterface appInterface,String apiKey) {
        mApp = appInterface;
        mApiKey = apiKey;
        Utils.setMainContext(activity);
        Utils.setContext(activity);
        getAdvertisingIdInfo(activity);

        CommercialBreakSDK.init(activity, getAppKey(), new CommercialBreakSDKInitCallback() {
            @Override
            public void onCommercialBreakSDKInit() {
                notifyApp("[onCommercialBreakSDKInit]: no message");
            }
        });
    }

    /**
     * Retrieve the user access token if it is available.
     *
     * @param context Application Context
     */
    @Deprecated
    public static void refreshLogin(Context context) {
        String accessToken = Functions.getCookie(Constants.perk_sdk_start, "token");
        String userID = Functions.getCookie(Constants.perk_sdk_start, "uid");
        PerkManager.setUserAccessToken(context, accessToken);
        PerkManager.setUserLoginId(context, userID);

        //Retrieve the user ID.
        if (PerkManager.getPerkUserID().isEmpty()) {
            getUserInfo(context);
        }
    }

    /**
     * Retrieve the user information if it is available.
     * Callback onUserInformation(boolean statusCode, PerkUserInfo userinfo ) will be called upon user info  fetched.
     * @param context Application Context
     *
     */
    public static boolean getUserInfo(final Context context) {
       return getUserInformation(context,true);
    }

    /**
     * Retrieve the internal user information if it is available.
     *
     * @param context Application Context
     */

    public static boolean getUserInformation (final Context context, final boolean isFromApp) {
        String acc_tok = PerkManager.getUserAccessToken(context);
        if (acc_tok.length() > 0) {

            Id.get(context, new RequestListener<Id>() {
                @Override
                public void success(Id id) {
                    PerkManager.setUserId(context, id);
                    Gson gson = new Gson();
                    if(isFromApp == true) {
                        PerkUserInfo info = gson.fromJson(gson.toJson(id).toString(),PerkUserInfo.class);
                        PerkManager.getApp().onUserInformation(true, info);
                    }
                }

                @Override
                public void failure(String message) {
                    if(isFromApp == true)
                        PerkManager.getApp().onUserInformation(false, null);
                }
            });
            return true;
        }
        return false;
    }
    /**
     * Retrieve the publisher information if it is available.
     * Callback onPublisherBalance(boolean statusCode, Integer prepaidPoints ) will be called upon prepaid points fetched.
     * @param context Application Context
     *
     */
    public static boolean getPublisherAvailablePoints(final Context context) {
        Publisher.get(context, new RequestListener<Publisher>() {
            @Override
            public void success(Publisher publisher) {
                PerkManager.setPublisherAvailablePoints(context, publisher.getPoints());
                PerkManager.getApp().onPublisherBalance(true, publisher.getPoints());
            }

            @Override
            public void failure(String message) {
                PerkManager.getApp().onPublisherBalance(false, updatePublisherAvailablePoints(context));
            }
        });
        return true;
    }
    /**
     * Retrieve the available countries information if it is available.
     * Callback onCountryList( String CountryList ) will be called upon country list  fetched.
     * @param context Application Context
     */

    public static boolean getCountriesList(final Context context) {
        Country.get(context, new RequestListener<Country>() {
            @Override
            public void success(Country country) {
                PerkManager.getApp().onCountryList(true, country.getCountries());
            }

            @Override
            public void failure(String message) {
            }
        });
        return true;
    }

    /**
     * sends the broadcast notification to application
     *
     * @param notification  notification string for multiple events
     */


    public static void sendNotification(String notification) {
        Intent perknotificaiton = new Intent(PerkManager.PERK_MANAGER_NOTIFICATION_STRING);
        perknotificaiton.putExtra("notification", notification);
        if (Utils.m_objContext != null) {
            Utils.m_objContext.sendBroadcast(perknotificaiton);
        }
    }

    /**
     * Get SDK status.
     *
     * @return true if SDK is enabled or false
     */
    public static boolean GetSDKStatus() {
        return Utils.m_strSdkStatus.equals("0");
    }

    /**
     * Send a message to the application via the defined listener.  May be used by applications
     * to send messages to their own systems as well.
     *
     * @param reason: String used to specify the reason the event was fired.
     */
    //todo:this method should have a single event parameter containing a code and a message
    public static void notifyApp(String reason) {
        if (reason == null) {
            return;
        }

        if (getApp() != null) {
            getApp().onPerkEvent(reason);
        }


        if (reason.equalsIgnoreCase("Point Earned")) {
            Utils.m_pointEarned = true;
        }
    }

    public static  void setSDKInitStaus(boolean sdkStatus) {
        mInit.setSdkStatus(sdkStatus);
    }

    /**
     * Toggle SDK status notifying the app of change in configuration
     * Callback onSdkStatus( boolean statuscode, boolean sdkStatus ) will be called upon sdk status  fetched.
     * @param context application context
     *
     *
     */
    public static void toggleSdkStatus(final Context context) {
        Utils.m_objContext = context;
        Status.toggle(context, new RequestListener<Status>() {
            @Override
            public void success(final Status status) {
                mInit.setSdkStatus(status.data.status);
                if (status.data.status == true) {
                    Utils.m_strSdkStatus = "0";
                }
                else {
                    Utils.m_strSdkStatus = "1";
                }
                if (getApp() != null) {
                    getApp().onSdkStatus(true, status.data.status);
                }
            }

            @Override
            public void failure(String message) {
                if (getApp() != null) {
                    getApp().onSdkStatus(false, Boolean.getBoolean(Utils.m_strSdkStatus));
                }
            }
        });
    }

    /**
     * Launch a webview containing the main Perk portal for the context provided.
     *
     * @param context application context
     *
     */
    public static void showPortal(Context context) {

        try {
            Utils.setContext(context);

            if (isNetworkAvailable(Utils.m_objContext)) {

                String[] permission = Utils.m_objPInfo.requestedPermissions;
                boolean hasInternet = false;

                //todo:but why?
                for (String aPermission : permission) {
                    if (aPermission.equals("android.permission.INTERNET")) {
                        hasInternet = true;
                    }
                }

                if (!hasInternet) {

                    Toast.makeText(context,
                            "Mandatory permissions not declared",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(PerkManager.getUserAccessToken(context).length() > 0) {
                    CookieManager cookieManager = CookieManager
                            .getInstance();
                    cookieManager.setAcceptCookie(true);
                    cookieManager.setCookie(
                            Constants.perk_sdk_start,
                            "token=" + PerkManager.getUserAccessToken(context)
                                    + "; expires='"
                                    + Functions.getGMT() + " GMT'");
                    cookieManager.setCookie(Constants.perk_sdk_start, "id="
                            + PerkManager.getUserLoginId(context)
                            + "; expires='" + Functions.getGMT()
                            + " GMT'");
                }

                Intent intent = new Intent(context, PerkSDKActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
            else {
                Functions.networkIssueDialog();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return number of unread notifications for this Perk user.
     *
     * @param context Application Context
     * @return string containing the number of unread notifications
     */
    public static int getUnreadNotificationsCount(Context context) {
        return PerkManager.getUserUnclaimedEvents(context);
    }

    /**
     * Return number of unclaimed notifications for this Perk user.
     *
     * @param context Application Context
     * @return int unclaimed notifications pending for Perk user
     */

    private static int getUserUnclaimedEvents(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.USER_UNCLAIMED_EVENTS, 0);
    }

    private static void setUserUnclaimedEventCount(Context context, int count) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(Constants.USER_UNCLAIMED_EVENTS, count)
                .apply();
    }



    /**
     * Launch a webview containing the Unclaimed Points section of the Perk Portal.
     *
     * @param context application context
     */
    public static void claimNotificationPage(Context context) {
        Uri uri = Uri.parse(Constants.unclaimed_events + "?api_key=" + PerkManager.getAppKey() + "&advertising_id=" + PerkManager.getAdInfo().getId());
        Intent intent = new Intent(PerkSDKActivity.VIEW, uri, context, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putBoolean(PerkSDKActivity.ARG_REQUEST_NATIVE, true);
        args.putBoolean(PerkSDKActivity.ARG_SHOW_RETURN, true);
        intent.putExtras(args);
        context.startActivity(intent);
    }

    /**
     * Override a manual interface for the point claim.
     *
     * @param context application context
     * @param eventId PerkSDK event ID
     */
    public static void claimPoints(Context context, String eventId) {

        try {
            Utils.setContext(context);

            String[] permission = Utils.m_objPInfo.requestedPermissions;
            boolean hasInternet = false;

            //todo:???
            for (String aPermission : permission) {
                if (aPermission.equals("android.permission.INTERNET")) {
                    hasInternet = true;
                }
            }
            if (!hasInternet) {

                Toast.makeText(context, "Mandatory permissions not declared",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Utils.setContext(context);
            Functions.onClaimButtonEvent(context, eventId);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Launch a tracked event with a locally defined notification
     * or the default notification included in the Perk SDK.
     *
     * @param context            application context
     * @param eventid            event identifier
     * @param customNotification shows the close button on the add if true
     * @param perkInterface      perkInterface for the notification
     */
    public static void trackEvent(Context context,
                                  final String eventid, boolean customNotification, @Nullable PerkCustomInterface perkInterface) {
        trackEvent(context, eventid, customNotification, perkInterface, false);
    }

    /**
     * Launch a tracked event with a locally defined notification
     * or the default notification included in the Perk SDK.
     *
     * @param context        application context
     * @param eventid        event identifier
     * @param customNotification    shows the close button on the add if true
     * @param perkInterface  perkInterface for the notification
     * @param returnToPortal return to the Perk Portal following this event when true
     */
    private static void trackEvent(final Context context,
                                  final String eventid, final boolean customNotification, final PerkCustomInterface perkInterface, boolean returnToPortal) {

        try {
            Utils.perkInterface = perkInterface;
            Utils.mCustomNotification = customNotification;
            Utils.m_objContext = context;
            Utils.m_bPortalDestination = returnToPortal;
            Event.get(context, eventid, new RequestListener<Event>() {
                @Override
                public void success(Event event) {
                    PerkManager.notifyApp(event.getText());
                    if (isNotificationEnabled()) {
                        if (perkInterface != null && Utils.mCustomNotification == true) {
                            Utils.m_strEventPoints = event.getPoints();
                            Utils.m_strEventExtra = String.valueOf(event.getPoints());
                            Utils.m_strNotificationText = event.getStub();
                            perkInterface.showEarningDialog();
                        }
                        else {
                            Functions.showEarningMsg(context, event, eventid);
                        }
                    }
                }

                @Override
                public void failure(String message) {
                    PerkManager.notifyApp(message);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch a webview containing an Perk Datapoints survey.
     *
     * @param context application context
     */
    public static void launchDataPoints(final Activity context, final String eventId) {

        // track the event
        final TrackEvent event = AppsaholicEventManager.getTrackEvent(context, eventId);

        // if the event is tracked successfully load the ad
        event.setSuccessAction(new Runnable() {
            @Override
            public void run() {
                ArrayList<Provider> waterfall = event.getSurveyWaterfall();
                Functions.loadDataPoints(context, event, waterfall, true);
            }
        }).setFailureAction(new Runnable() {
            @Override
            public void run() {
                //show the failure message
                Functions.showSurveyFail(context, event);
            }
        }).start();
    }

    /**
     * Launch a webview containing the PerkSDK login page.
     *
     * @param context application context
     * @param eventId EventID associated with this
     * @return true on successful login, false on failed login or user is already loggedin
     */
    //todo:does this need to return a value?
    public static boolean launchLoginPage(Context context, String eventId) {
        if (PerkManager.getConfig().isUserLoggedIn(context)) {
            PerkManager.notifyApp("User is already logged in");
            return false;
        }

        //Otherwise, go ahead and do it.
        Utils.setContext(context);
        Utils.isRewardsCalled = false;
        Functions.launchLoginPage(context, eventId);

        return true;
    }

    /**
     * Clear session data and removes webview cookies for the currently logged in user.
     *
     * @param context Application Context
     */
    public static void logoutUser(Context context) {
        if (PerkManager.getConfig().isUserLoggedIn(context)) {
            PerkManager.notifyApp("User logged out");
        }
        else {
            PerkManager.notifyApp("User not logged in");
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        PerkManager.setUserAccessToken(context, "");
        wipeUserPreferences(context);
    }

    private static void wipeUserPreferences(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Constants.USER_ID, "")
                .putString(Constants.USER_EMAIL, "")
                .putString(Constants.USER_FIRST_NAME, "")
                .putString(Constants.USER_LAST_NAME, "")
                .putString(Constants.USER_PROFILE_IMAGE, "")
                .putInt(Constants.USER_AVAILABLE_POINTS, 0)
                .putInt(Constants.USER_PENDING_POINTS, 0)
                .apply();
    }

    /**
     * Get the number of points earned for an event.
     *
     * @return number of points to be awarded for the event
     */
    public static int getEventPoints() {
        return Utils.m_strEventPoints;
    }

    /**
     * Get custom notifications text.
     *
     * @return event accomplishment string
     */
    public static String getNotificationText() {
        return Utils.m_strNotificationText;
    }

    /**
     * Get the user's Perk ID.
     *
     * @return user's Perk ID string
     */
    public static String getPerkUserID() {
        return (mInit.getAppsaholicUserID() != null) ? mInit.getAppsaholicUserID() : "";
    }

    /**
     * Get the user's available points  locally stored.
     *
     * @param context Application Context
     * @return available points
     */
    public static int getPerkAvailablePoints(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.USER_AVAILABLE_POINTS, 0);
    }

    public static void setPerkAvailablePoints(Context context, int points) {
        if (PerkManager.getConfig().isUserLoggedIn(context)) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt(Constants.USER_AVAILABLE_POINTS, points)
                    .apply();
        }
        else {
            PerkManager.updatePerkPendingPoints(context, points);
        }
    }

    /**
     * Get the publisher's available points locally stored.
     *
     * @param context Application Context
     * @return available points
     */
    public static int updatePublisherAvailablePoints(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.PUBLISHER_AVAILABLE_POINTS, 0);
    }

    public static void setPublisherAvailablePoints(Context context, int points) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(Constants.PUBLISHER_AVAILABLE_POINTS, points)
                .apply();
    }

    /**
     * Get a user's pending points locally stored.
     *
     * @param context Application Context
     * @return pending points
     */
    public static int getPerkPendingPoints(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.USER_PENDING_POINTS, 0);
    }

    public static void setPerkPendingPoints(Context context, int points) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(Constants.USER_PENDING_POINTS, points)
                .apply();
    }

    public static void updatePerkPendingPoints(Context context, int points) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(Constants.USER_PENDING_POINTS,
                        getPerkPendingPoints(context) + points)
                .apply();
    }

    /**
     * Get the Perk user's status.
     *
     * @return Perk user status
     */

    public static  boolean getIsUserLoggedIn(Context context) {
        return getConfig().isUserLoggedIn(context);
    }
    /**
     * Get the Perk user's name.
     *
     * @return Perk username
     */
    public static String getPerkUsername() {
        return Utils.m_strUserName;
    }

    /**
     * Set this method to prevent users from claiming rewards while using an adblocker.
     *
     * @param status a string equalling "OFF" for no ad block usage or "ON" for ad blocks in progress.\
     */
    // todo:this function should take a boolean parameter
    public static void setAdBlockStatus(String status) {
        Utils.m_strAdBlockStatus = status;
    }

    /**
     * Get the app key.
     *
     * @return the application key
     */
    public static String getAppKey() {
        return mApiKey;
    }

    /**
     * Get the number of points earned for an event.
     *
     * @return String used in creating the special-case notification for custom interfaces.
     */
    public static String getPointsText() {
        return Utils.m_strEventExtra;
    }

    /**
     * Set enabling or Disabling of notifications.
     */
    public static void toggleNotifications() {
        mNotify ^= true;
    }

    /**
     * Launches an alert with Perk is unavailable.
     *
     * @param context application context
     */
    public static void showAlertInfo(Context context) {
        Utils.setContext(context);
        Functions.showAlertInfo(Utils.m_objContext);
    }



    /**
     * Determine if a county is supported by Perk based on telephone country code.
     *
     * @param countryCode The country to check against.
     * @return true if the country is supported, false if it is not a supported country.
     */
    //todo:use an existing pattern here. There is no validation occurring.
    public static boolean getPerkAvailable(String countryCode) {
        //todo:an associative array makes more sense here.
        for (int i = 0; i < Utils.m_Countries.length; i++) {
            if (Utils.m_Countries[i].equalsIgnoreCase(countryCode)) {
                return true;
            }
        }

        //Otherwise.
        return false;
    }

    /**
     * Display an ad unit obtained from the unclaimed waterfall provided on initialization.
     *
     * @param context the application context
     * @param eventId event identifier
     */
    public static void showAdUnit(final Context context, final String eventId) {
        // track the event
        final TrackEvent event = AppsaholicEventManager.getTrackEvent(context, eventId);

        // if the event is tracked successfully load the ad
        event.setSuccessAction(new Runnable() {
            @Override
            public void run() {
                ArrayList<Provider> waterfall = event.getLoadAdWaterfall();
                AppsaholicEventManager.loadAd(context, event, waterfall, true, false, false, true, false);
            }
        }).setFailureAction(new Runnable() {
            @Override
            public void run() {
                //show the failure message
                Functions.showAlertFail(context, true, false, false);
            }
        }).start();
    }

    /**
     * Launch a webview containing the rewards if user is logged in, else  login page.
     *
     * @param context application context
     */

    public static void showPrizePage(Context context, String url) {

        Utils.isRewardsCalled = true;
        Uri uri = Uri.parse(url);
        CookieManager cookieManager = CookieManager
                .getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(
                url,
                "token=" + PerkManager.getUserAccessToken(context)
                        + "; expires='"
                        + Functions.getGMT() + " GMT'");
        cookieManager.setCookie(url, "id="
                + PerkManager.getUserLoginId(context)
                + "; expires='" + Functions.getGMT()
                + " GMT'");
        Intent intent = new Intent(PerkSDKActivity.VIEW, uri, context, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void showPrizePage(Context context) {

        Utils.isRewardsCalled = true;
        Uri uri = Uri.parse(Constants.rewards_page);
        if(PerkManager.getUserAccessToken(context).length() > 0) {
            CookieManager cookieManager = CookieManager
                    .getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(
                    Constants.rewards_page,
                    "token=" + PerkManager.getUserAccessToken(context)
                            + "; expires='"
                            + Functions.getGMT() + " GMT'");
            cookieManager.setCookie(Constants.rewards_page, "id="
                    + PerkManager.getUserLoginId(context)
                    + "; expires='" + Functions.getGMT()
                    + " GMT'");
        }
        Intent intent = new Intent(PerkSDKActivity.VIEW, uri, context, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Displays an ad unit obtained from the video waterfall provided on initialization.
     *
     * @param context the application context
     * @param eventId event identifier
     */
    public static void showVideoAdUnit(final Context context, final String eventId) {
        // track the event
        final TrackEvent event = AppsaholicEventManager.getTrackEvent(context, eventId);

        // if the event is tracked successfully load the ad
        event.setSuccessAction(new Runnable() {
            @Override
            public void run() {
                ArrayList<Provider> waterfall = event.getVideoWaterfall();
                AppsaholicEventManager.loadAd(context, event, waterfall, true, false, false, true, false);
            }
        }).setFailureAction(new Runnable() {
            @Override
            public void run() {
                //show the failure message
                Functions.showAlertFail(context, true, false, false);
            }
        }).start();
    }

    /**
     * Displays an ad unit obtained from the video waterfall provided on initialization.
     *
     * @param eventId event identifier
     * @param context the application context
     */
    public static void showDisplayAdUnit(final Context context, final String eventId) {
        // track the event
        final TrackEvent event = AppsaholicEventManager.getTrackEvent(context, eventId);

        // if the event is tracked successfully load the ad
        event.setSuccessAction(new Runnable() {
            @Override
            public void run() {
                ArrayList<Provider> waterfall = event.getDisplayWaterfall();
                AppsaholicEventManager.loadAd(context, event, waterfall, true, false, false, true, false);
            }
        }).setFailureAction(new Runnable() {
            @Override
            public void run() {
                //show the failure message
                Functions.showAlertFail(context, true, false, false);
            }
        }).start();

    }

    /**
     * Displays an ad unit obtained from the video waterfall provided on initialization.
     *
     * @param context the application context
     */
    public static void loadVideoAdUnit(final Context context) {

    }
    /**
     * Gives UnreadNotification Count through onNotifiationsCount callback to the Application.
     *
     * @param context the application context
     */

    public static void fetchNotificationsCount(final Context context) {
        Unclaimed.get(context, new RequestListener<Unclaimed>() {
            @Override
            public void success(Unclaimed unclaimed) {
                PerkManager.setUserUnclaimedEventCount(context, unclaimed.data.unclaimed_events);
                PerkManager.getApp().onNotifiationsCount(true, unclaimed.data.unclaimed_events);
            }

            @Override
            public void failure(String message) {
                PerkManager.getApp().onNotifiationsCount(false, getUnreadNotificationsCount(context));
            }
        });
    }

    /**
     * Sets the subId used to pass through to trackEvent()
     *
     * @param subId ???
     */
    public static void setSubId(String subId) {
        //Blank string ID eliminates the return notification.
        Utils.m_strSubId = subId;
    }

    public static PerkAppInterface getApp() {
        return mApp;
    }

    /**
     * Retrieve this device's Device-Info header string.
     *
     * @return a string device info header
     */
    public static String getDeviceInfo() {
        return mDeviceInfo;
    }

    public static void enableEventNotifications(boolean mNotify) {
        PerkManager.mNotify = mNotify;
        String message = "Event notifications " + ((mNotify) ? "enabled" : "disabled");
        PerkManager.notifyApp(message);
    }

    /**
     * gives if Notifications Enabled
     *
     * @return a boolean tells notifications status
     */
    public static boolean isNotificationEnabled() {
        return PerkManager.mNotify;
    }


    public static void setUserId(Context context, Id id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        int pnt =  id.getAvailablePoints();
        editor.putString(Constants.USER_ID, id.getId())
                .putString(Constants.USER_EMAIL, id.getEmail())
                .putString(Constants.USER_FIRST_NAME, id.getFirstName())
                .putString(Constants.USER_LAST_NAME, id.getLastName())
                .putInt(Constants.USER_AVAILABLE_POINTS, id.getAvailablePoints())
                .putInt(Constants.USER_PENDING_POINTS, id.getPendingPoints())
                .putString(Constants.USER_PROFILE_IMAGE, id.getProfileImage())
                .apply();
        Utils.m_strUserName = id.getFirstName() + " " + id.getLastName();
    }

    public static void setUserLoginId(Context context, String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER_ID, id)
                .apply();
    }

    public static String getUserAccessToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.USER_ACCESS_TOKEN, "");
    }

    public static void setUserAccessToken(Context context, String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(Constants.USER_ACCESS_TOKEN, accessToken)
                .apply();
    }

    public static String getUserLoginId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.USER_LOGIN_ID, "");
    }

    public static String getProfileImageUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.USER_PROFILE_IMAGE, "");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static PerkConfig getConfig() {
//        if (!mInit.isSdkEnabled()) {
//            // something went wrong but it's okay because we won't try to do anything critical
//            //new Constants.DummyPerkManager();
//        }
        if (sInstance == null) {
            sInstance = new PerkManager();
        }
        return sInstance;
    }

    /**
     * Check if the user is currently authenticated.
     *
     * @param context the application context
     * @return true if the user is authenticated or false
     */
    @Override
    public boolean isUserLoggedIn(Context context) {
        boolean loggedin = PerkManager.getUserAccessToken(context).isEmpty();
        return !loggedin;
    }

    @Override
    public boolean isSdkInitialized() {
        return mInit.isSdkEnabled();
    }

    @Override
    public boolean isPassivePlayEnabled() {
        return mInit.isPassivePlayEnabled();
    }

    @Override
    public int getPassiveCountdownSeconds() {
        return mInit.getPassiveCountdownSeconds();
    }

    @Override
    public String getAppsaholicId() {
        return mInit.getAppsaholicId();
    }

    @Override
    public String getApiKey() {
        return PerkManager.getAppKey();
    }

    /**
     * Note: Unsupported functionality included for backwards compatibility.
     * <p/>
     * <pre>
     *     Retrieve the ad tag from this initialization object.
     * </pre>
     *
     * @return the tag for either AER or VGL
     * @deprecated
     */
    @Override
    public String getAdTag() {
        return mInit.getAdTag();
    }

    /**
     * Get the waterfall data from a specific claimed event
     *
     * @param claim non-null claim data
     * @return The specific waterfall for this event
     */
    @Override
    public ArrayList<Provider> getClaimWaterfall(@NonNull Claim claim) {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(claim.data.waterfall));
        return waterfall;
    }

    /**
     * Retrieves the video waterfall from Init data
     *
     * @return the default video waterfall
     */
    @Override
    public ArrayList<Provider> getVideoWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(mInit.getVideoAdTags()));
        return waterfall;
    }

    /**
     * Retrieves the video waterfall from Init data
     *
     * @return the default notification waterfall
     */
    @Override
    public ArrayList<Provider> getNotificationWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(mInit.getNotificationAdTags()));
        return waterfall;
    }

    /**
     * Retrieves the video waterfall from Init data
     *
     * @return the default survey waterfall
     */
    @Override
    public ArrayList<Provider> getSurveyWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(mInit.getSurveyAdTags()));
        return waterfall;
    }

    /**
     * Retrieves the video waterfall from Init data
     *
     * @return the default display ad waterfall
     */
    @Override
    public ArrayList<Provider> getDisplayAdWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(mInit.getDisplayAdTags()));
        return waterfall;
    }

    @Override
    public boolean getSdkStatus() {
        return Utils.m_strSdkStatus.equals("0");
    }

    @Override
    public String getDeviceIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Activity.WIFI_SERVICE);
        return BigInteger.valueOf(wifiManager.getDhcpInfo().netmask).toString();
    }
}
