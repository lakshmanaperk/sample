package com.perk.perksdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Browser;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.CookieManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.ads.Ad;
import com.perk.perksdk.appsaholic.v1.claims.Claim;
import com.perk.perksdk.appsaholic.v1.common.NotificationRequest;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;
import com.perk.perksdk.appsaholic.v1.end.End;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEventManager;
import com.perk.perksdk.appsaholic.v1.events.ClaimEvent;
import com.perk.perksdk.appsaholic.v1.events.Event;
import com.perk.perksdk.appsaholic.v1.events.TrackEvent;
import com.perk.perksdk.appsaholic.v1.points.Points;
import com.perk.perksdk.appsaholic.v1.track.Track;
import com.perk.perksdk.widget.LearnMoreActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Functions {

    public static final int DEFAULT_MAX_ADS = 1;
    static Dialog NetWorkdialog = null;
    static Dialog Notidialog = null;
    static CountDownTimer removeViewTimer;
    static int deviceHeight, deviceWidth;
    public final static String PORTAL_VIEW = "com.perk.perksdk.appsaholic.web.PortalView.VIEW";

    public static int getPixels(float dip) {

        final float scale = Utils.m_objMetrics.density;
        return (int) (dip * scale);
    }

    public static void networkIssueDialog() {

        if (NetWorkdialog != null && NetWorkdialog.isShowing()) {
            return;
        }
        LinearLayout.LayoutParams linearParam;
        LinearLayout networkLayout = new LinearLayout(Utils.m_objContext);
        networkLayout.setOrientation(LinearLayout.VERTICAL);
        networkLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        String deviceResolution = Utils.getResolution(Utils.m_objContext);

        String[] width = deviceResolution.split("x");

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) Utils.m_objContext).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        int density = metrics.densityDpi;

        if (density == DisplayMetrics.DENSITY_XHIGH) {
            linearParam = new LinearLayout.LayoutParams(
                    Integer.parseInt(width[0]), Integer.parseInt(width[1]));
        }
        else if (density == DisplayMetrics.DENSITY_XXHIGH) {
            linearParam = new LinearLayout.LayoutParams(
                    Integer.parseInt(width[0]), Integer.parseInt(width[1]));
        }
        else {
            linearParam = new LinearLayout.LayoutParams(
                    Integer.parseInt(width[0]), Integer.parseInt(width[1]));
        }

        networkLayout.setLayoutParams(linearParam);

        ImageView networkErrorLogo = new ImageView(Utils.m_objContext);
        networkErrorLogo.setImageBitmap(decodeBase64(Base64Images.networkErrorImage));
        LinearLayout.LayoutParams networkErrorLogoParam = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        networkErrorLogoParam.setMargins(100, 50, 100, 0);
        networkErrorLogo.setLayoutParams(networkErrorLogoParam);

        ImageView perkLogo = new ImageView(Utils.m_objContext);
        perkLogo.setImageBitmap(decodeBase64(Base64Images.perkLogo));
        LinearLayout.LayoutParams perkLogoParam = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        perkLogoParam.setMargins(100, 50, 100, 0);
        perkLogo.setLayoutParams(perkLogoParam);

        ImageView networkErrorText = new ImageView(Utils.m_objContext);
        networkErrorText.setImageBitmap(decodeBase64(Base64Images.networkText));
        LinearLayout.LayoutParams networkErrorTextParam = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        networkErrorTextParam.setMargins(100, 20, 100, 0);
        networkErrorText.setLayoutParams(networkErrorTextParam);

        ImageView okayBtn = new ImageView(Utils.m_objContext);
        okayBtn.setImageBitmap(decodeBase64(Base64Images.okayBtn));
        LinearLayout.LayoutParams okayBtnParam = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        okayBtnParam.setMargins(100, 50, 100, 50);
        okayBtn.setLayoutParams(okayBtnParam);

        networkLayout.addView(networkErrorLogo);
        networkLayout.addView(perkLogo);
        networkLayout.addView(networkErrorText);
        networkLayout.addView(okayBtn);

        // Dialog Initialization
        NetWorkdialog = new Dialog(Utils.m_objContext);
        NetWorkdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        NetWorkdialog.setContentView(networkLayout);

        Window window = NetWorkdialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.x = 0;
        wlp.y = 200;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        try {
            NetWorkdialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        okayBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                NetWorkdialog.dismiss();
            }
        });

    }

    public static void showAlertFail(final Context context, final boolean standalone, final boolean fromportal, final boolean fromwatchmore) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("No videos available.  Try again later.");
            builder.setCancelable(false);
            //TODO:  If these are going to be the same as Not Available, no need to separate them out.
//            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    ((Activity) Utils.m_objContext).finish();
//                    Functions.loadVideoAd(context, standalone, fromportal, fromwatchmore, null);
//                }
//            });
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Clear this flag here just in case.
                    Utils.m_bIsUnclaimed = false;

                    ((Activity) context).finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSurveyFail(final Activity context, final TrackEvent event) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("No surveys available.  Try again later.");
            builder.setCancelable(false);
            //TODO:  If these are going to be the same as Not Available, no need to separate them out.
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Functions.loadDataPoints(context, event, null, true);
                    context.finish();
                    Utils.m_dataPointsRequest = true;
                }
            });
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Clear this flag here just in case.
                    Utils.m_bIsUnclaimed = false;

                    context.finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSurveyFailForClaim(final Activity context, final ClaimEvent event) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("No surveys available.  Try again later.");
            builder.setCancelable(false);
            //TODO:  If these are going to be the same as Not Available, no need to separate them out.
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Functions.loadDataPointsWithClaim(context, event, null, true);
                    context.finish();
                    Utils.m_dataPointsRequest = true;
                }
            });
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Clear this flag here just in case.
                    Utils.m_bIsUnclaimed = false;

                    context.finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //todo:this function needs to die. It's misleading and we should never\
    // fall into the case that causes it to be presented in the first place
    public static void showPerkUnavailableMessage(Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Perk currently unavailable. Try again shortly.");
            builder.setCancelable(false);
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Clear this flag here just in case.
                    Utils.m_bIsUnclaimed = false;
                    PerkManager.notifyApp("SDK Failed");
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * onClaimButtonEvent
     * Revamped function - new to 2.2.1 and new endpoints
     * Uses the waterfall parsed in the previous call.
     *
     * @param context the application context
     * @param eventId Appsaholic Event ID
     */
    public static void onClaimButtonEvent(final Context context, final String eventId) {
        final ClaimEvent event = AppsaholicEventManager.getClaimEvent(context, eventId);
        event.setOnEventCompleteListener(new ClaimEvent.OnEventCompleteListener() {

            @Override
            public void onEndEventSuccess(End end) {
                Functions.showReturnNotfication(context, end, end.getPoints(), false, "", "");
            }

            @Override
            public void onEndEventNotModified() {

            }

            @Override
            public void onEndEventFailure(String message) {
                Functions.showPerkUnavailableMessage(context);
            }
        });
        event.setSuccessAction(new Runnable() {
            @Override
            public void run() {
                if (event.isAdSupported()) {
                    AppsaholicEventManager.loadAd(context, event, event.getWaterfall(), true, true, false, false, false);
                }
                else {
                    event.end();
                }
            }
        });
        event.claim();
    }

    /**
     * Play an ad from the ad waterfall
     *
     * @param context
     * @param eventId             String ID of the event to track
     * @param waterfall
     * @param mCustomNotification true if app shows its own UI on success
     * @param showReturn          true if this ad should display the pop-up earning notification on completion
     * @param fromPortal
     */
    public static void loadAd(Context context, @Nullable final String eventId, ArrayList<Provider> waterfall, boolean mCustomNotification, boolean showReturn, boolean fromPortal, boolean fromwatchmore) {

        for (Provider tag : waterfall) {
            switch (tag.partner) {
                case "COM": {
                    Intent intent = new Intent(Utils.m_objContext, AdsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle args = new Bundle();
                    args.putString(AdsActivity.ARG_EVENT_ID, eventId);
                    args.putBoolean(AdsActivity.ARG_CUSTOM_NOTIFICATION, mCustomNotification);
                    args.putBoolean(AdsActivity.ARG_SHOW_RETURN_NOTIFICATION, showReturn);
                    args.putBoolean(AdsActivity.ARG_FROM_PORTAL, fromPortal);
                    args.putString(AdsActivity.ARG_AD_SOURCE, tag.partner);
                    args.putBoolean(AdsActivity.ARG_FROM_WATCHMORE, fromwatchmore);
                    intent.putExtras(args);
                    context.startActivity(intent);
                    PerkManager.notifyApp("Leaving application");
                }
                return;
                case "FAN": {
                    logAdStart(tag);
                    Intent intent = new Intent(Utils.m_objContext, FacebookAdNetworkActivity.class);
                    Bundle args = new Bundle();
                    args.putString(Constants.EVENT_ID, eventId);
                    args.putString(Constants.PLACEMENT_ID, tag.tag);
                    args.putBoolean(FacebookAdNetworkActivity.ARG_SHOW_RETURN, showReturn);
                    intent.putExtras(args);
                    Utils.m_objContext.startActivity(intent);
                    PerkManager.notifyApp("Leaving application");
                }
                return;
                case "TRP":
                    logAdStart(tag);
                    loadTrialpay();
                    return;
                case "PNV": {
                    logAdStart(tag);
                    Uri uri = Uri.parse(Constants.pubnative_ads + "api_key=" + PerkManager.getAppKey() + "&advertising_id=" + Utils.m_strAdvertisingId);
                    Intent intent = new Intent(PerkSDKActivity.VIEW, uri, Utils.m_objContext, PerkSDKActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra(Browser.EXTRA_HEADERS, bundle);
                    bundle.putString(Constants.DEVICE_INFO, PerkManager.getDeviceInfo());
                    context.startActivity(intent);
                    PerkManager.notifyApp("Leaving application");
                }
                return;
                case "TAP": {
                    logAdStart(tag);
                    String tapSenseLocation = Constants.tapsense_ads + "api_key=" + PerkManager.getAppKey() + "&appsaholic_id=" + PerkManager.getPerkUserID();
                    tapSenseLocation = tapSenseLocation.replace("https", "http");//why???
                    Uri uri = Uri.parse(tapSenseLocation);
                    Intent intent = new Intent(PerkSDKActivity.VIEW, uri, Utils.m_objContext, PerkSDKActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DEVICE_INFO, PerkManager.getDeviceInfo());
                    Locale.getDefault();
                    bundle.putString(Constants.ACCEPT_LANGUAGE, Locale.getDefault().toString());
                    bundle.putString(Constants.USER_AGENT, Utils.m_sUserAgent);
                    intent.putExtra(Browser.EXTRA_HEADERS, bundle);
                    Utils.m_objContext.startActivity(intent);
                    PerkManager.notifyApp("Leaving application");
                }
                return;
                case "PP":
                    logAdStart(tag);
                    End.get(context, eventId, null, null, null);// just reward the points
                    Points.legacyUpdateUserPoints(context);
                    return;
                default:
                    PerkManager.notifyApp(
                            String.format("Unrecognized ad source: partner = %s, tag = %s",
                                    tag.partner, tag.tag));
                    break;
            }
        }

        //TODO:  Add errors and warnings as appropriate for use cases.
        //Here's our worst case scenario.
        /**
         * use {@link #loadSurveyAd(Activity, String)} instead
         */
    }

    public static void logAdStart(Provider tag) {
        String message = String.format("[Load Ad] %s: %s", tag.partner, tag.tag);
        PerkManager.notifyApp(message);
    }

    /**
     * Designed to cycle through our video providers
     * Generates an error dialog if none available
     * Added in v2.2 - Modified in 3.0.1
     *
     * @param context the application context
     * @param eventId Appsaholic Event ID
     */
    public static void loadVideoAd(final Context context, final boolean standalone, boolean fromportal, final boolean fromwatchmore, final String eventId) {
        loadAd(context, eventId, PerkManager.getConfig().getVideoWaterfall(), standalone, false, fromportal, fromwatchmore);
    }

    /**
     * loadNotificationAd
     * Specific waterfall generated for our unclaimed notifications
     * Generates an error dialog if none available
     * Added in v3.0.1
     *
     * @param context the application context
     * @param eventId Appsaholic Event ID
     */
    public static void loadNotificationAd(final Context context, final String eventId) {
        Track.get(context, eventId, null);

        Claim.get(context, eventId, new RequestListener<Claim>() {
            @Override
            public void success(Claim claim) {
                Log.e("Claim.onLoadListener", "Got claim " + eventId);
                loadAd(context, eventId, PerkManager.getConfig().getClaimWaterfall(claim), true, false, false, false);
            }

            @Override
            public void failure(String message) {
                Log.e("Claim.onLoadListener",
                        "Failed to retrieve waterfall data for claim. Using default video waterfall.");
                loadAd(context, eventId, PerkManager.getConfig().getNotificationWaterfall(), true, false, false, false);
            }
        });
    }


    private static void surveyLoadError(Provider tag) {
        PerkManager.notifyApp(
                String.format("Unrecognized ad source: partner = %s, tag = %s",
                        tag.partner, tag.tag));
    }

    /**
     * loadDisplayAd()
     * Designed to cycle through our display ads for stand-alone ads
     * Generates an error dialog if none available
     * Added in v3.0.2
     *
     * @param context the application context
     * @param eventId Appsaholic Event ID
     */
    public static void loadDisplayAd(final Context context, final String eventId) {
        Track.get(context, eventId, null);

        Claim.get(context, eventId, new RequestListener<Claim>() {
            @Override
            public void success(Claim claim) {
                Log.e("Claim.onLoadListener", "Got claim " + eventId);
                loadAd(context, eventId, PerkManager.getConfig().getClaimWaterfall(claim), true, false, false, false);
            }

            @Override
            public void failure(String message) {
                Log.e("Claim.onLoadListener",
                        "Failed to retrieve waterfall data for claim. Using default video waterfall.");
                loadAd(context, eventId, PerkManager.getConfig().getDisplayAdWaterfall(), true, false, false, false);
            }
        });
    }

    /**
     * loadMainPage
     * Launches our portal page.
     *
     * @param context
     */
    public static void loadMainPage(Context context) {
        Intent intent = new Intent(context, PerkSDKActivity.class);
        Utils.m_strEventAdCheck = "eventAd";
        Utils.m_objContext.startActivity(intent);
        PerkManager.notifyApp("Entering Portal");
        Utils.m_bPrePortalAd = false;   //Clear this flag so we can fire the appropriate listener.
    }

    /**
     * deprecated v3.1
     * loadPollfish
     * Initializes the PollFish integration.
     */
    @Deprecated
    private static void loadPollfish() {

    }

    /**
     * loadTrialpay
     * Initializes the TrialPay integration.
     */
    private static void loadTrialpay() {
        Uri uri = Uri.parse(Constants.trailpay_single_offer + "?api_key=" + PerkManager.getAppKey());
        Intent intent = new Intent(PerkSDKActivity.VIEW, uri, Utils.m_objContext, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putBoolean(PerkSDKActivity.ARG_IS_EVENT_AD, false);
        intent.putExtras(args);
        Utils.m_objContext.startActivity(intent);
        PerkManager.notifyApp("Leaving application");
    }

    /**
     * DATAPOINTS test
     * Initializes the TrialPay integration.
     *
     * @param context
     * @param event
     * @param nativeRequest
     */
    public static void loadDataPoints(Context context, TrackEvent event, ArrayList<Provider> waterfall, boolean nativeRequest) {
        final String url = Constants.datapoint_survey
                + "?appsaholic_id=" + PerkManager.getConfig().getAppsaholicId()
                + "&api_key=" + PerkManager.getAppKey();
        if (waterfall != null) {
            for (Provider tag : waterfall) {
                event.setAdSource(tag.partner);
            }
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(PORTAL_VIEW, uri, context, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putParcelable(PerkSDKActivity.ARG_TRACK_EVENT, event);
        args.putBoolean(PerkSDKActivity.ARG_IS_EVENT_AD, false);
        args.putBoolean(PerkSDKActivity.ARG_REQUEST_NATIVE, nativeRequest);
        intent.putExtras(args);
        context.startActivity(intent);
        PerkManager.notifyApp("Leaving application");
        PerkManager.notifyApp("loadDataPoints");
    }

    public static void loadDataPointsWithClaim(Context context, ClaimEvent event, ArrayList<Provider> waterfall, boolean nativeRequest) {
        final String url = Constants.datapoint_survey
                + "?appsaholic_id=" + PerkManager.getConfig().getAppsaholicId()
                + "&api_key=" + PerkManager.getAppKey();
        if (waterfall != null) {
            for (Provider tag : waterfall) {
                event.setAdSource(tag.partner);
            }
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(PORTAL_VIEW, uri, context, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putParcelable(PerkSDKActivity.ARG_CLAIM_EVENT, event);
        args.putBoolean(PerkSDKActivity.ARG_IS_EVENT_AD, false);
        args.putBoolean(PerkSDKActivity.ARG_REQUEST_NATIVE, nativeRequest);
        intent.putExtras(args);
        context.startActivity(intent);
        PerkManager.notifyApp("Leaving application");
        PerkManager.notifyApp("loadDataPoints");
    }

    /**
     * Direct Login Function
     * Login outside of the normal flow.
     *
     * @param context
     * @param eventId
     */
    public static void launchLoginPage(Context context, String eventId) {
        Uri uri = Uri.parse(Constants.perk_account
                + "?advertising_id=" + PerkManager.getAdInfo().getId()
                + "&api_key=" + PerkManager.getAppKey()
                + "&event_id=" + eventId);
        Intent intent = new Intent(PerkSDKActivity.VIEW, uri, context, PerkSDKActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putString(PerkSDKActivity.ARG_EVENT_ID, eventId);
        intent.putExtras(args);
        context.startActivity(intent);
        PerkManager.notifyApp("Leaving application");
    }

    public static void showEarningMsg(final Context context, final NotificationRequest request, final String eventid) {

        //Check to see if we're even supposed to be doing this.
        if (Utils.m_suppressNotifications) {
            return;
        }

        final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        d.getMetrics(displaymetrics);

        deviceHeight = displaymetrics.heightPixels;
        deviceWidth = displaymetrics.widthPixels;

        //We're assuming portrait for the most part.  But just in case, adjust landscape height here.
        //If we leave it as is for landscape, the spacing is entirely off, so we'll fake it as a
        //square display.
        if (deviceWidth > deviceHeight) {
            deviceHeight = deviceWidth;
        }

        //TEMP TEMP TEMP CORONA
        if (deviceHeight == 0 || deviceWidth == 0) {
            deviceWidth = 720;
            deviceHeight = 1280;
        }


        //Define the margins here manually for translation.
        float margin = deviceWidth / (720.f / 20.f);  //This is set to 20p for 720p devices.
        float heightAdjustment = deviceHeight / (1280.f / 55.f);  //This is set to 55p for 1280p devices.
        float coinHeightAdjustment = deviceHeight / (1280.f / 35.f);  //This is set to 35p for 1280p devices.
        float placeholderCoinHeightAdjustment = deviceHeight / (1280.f / 50.f);  //This is set to 50p for 1280p devices.
        float crossHeightAdjustment = deviceHeight / (1280.f / 125.f);  //This is set to 125p for 1280p devices.
        int backgroundHeight = (int) (deviceHeight / (1280.f / 280.f));   //280p for a 1280p device.
        int dialogHeight = (int) (deviceHeight / (1280.f / 400.f));   //400p for a 1280p device.

        String topLevel = "You earned";
        String midLevel = getPointRewardString(request);
        String curval = "";
        String curtype = "";
        if (request instanceof Event) {
            Event thisevent = ((Event) request);
            if (thisevent != null && thisevent.data != null && thisevent.data.currency != null) {
                curval = thisevent.data.currency.currency_awarded;
                curtype = thisevent.data.currency.currency_name;
            }
        }

        String bottomLevel = request.getStub();

        Typeface robotoBlack = null;
        try {
            //todo:this is definitely causing leaks...
            InputStream stream = Utils.m_objMainContext.getAssets().open("fonts/Roboto-Black.ttf");
            robotoBlack = Typeface.createFromAsset(Utils.m_objContext.getAssets(), "fonts/Roboto-Black.ttf");
            stream.close();
        }
        catch (FileNotFoundException e) {
            Log.w("AssetLoad", "assetExists failed: " + e.toString());
        }
        catch (IOException e) {
            Log.w("IOexception", "assetExists failed: " + e.toString());
        }

        final FrameLayout claimLayoutParent = new FrameLayout(context);
        ViewGroup.LayoutParams topLevelLayout = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        claimLayoutParent.setLayoutParams(topLevelLayout);

        final FrameLayout claimCombinationParent = new FrameLayout(context);
        //claimCombinationParent.setOrientation(LinearLayout.HORIZONTAL);
        //LinearLayout.LayoutParams layoutInfo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //claimCombinationParent.setLayoutParams(layoutInfo);
        //claimCombinationParent.setBackground(drawableBitmap);

        final LinearLayout claimTextParent = new LinearLayout(context);
        claimTextParent.setOrientation(LinearLayout.VERTICAL);

        final RelativeLayout bottomLevelButtons = new RelativeLayout(context);
        RelativeLayout.LayoutParams bottomLevelLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bottomLevelButtons.setLayoutParams(bottomLevelLayout);

        //Top line
        final TextView claimNotificationTextTop = new TextView(context);
        claimNotificationTextTop.setText(topLevel);
        claimNotificationTextTop.setTextSize(16);
        claimNotificationTextTop.setGravity(Gravity.LEFT);
        claimNotificationTextTop.setTextColor(Color.parseColor("#FFFFFF"));

        if (robotoBlack != null) {
            claimNotificationTextTop.setTypeface(robotoBlack);
        }

        //Middle line
        final StrokeTextView claimNotificationTextMid = new StrokeTextView(context);
        claimNotificationTextMid.setTextSize(16);
        claimNotificationTextMid.setStrokeColor(Color.parseColor("#0f94c9"));
        claimNotificationTextMid.setStrokeWidth(8);
        claimNotificationTextMid.setGravity(Gravity.CENTER);
        claimNotificationTextMid.setTextColor(Color.parseColor("#FFFFFF"));
        if (curval != null && curval.length() > 0 && curtype != null && curtype.length() > 0) {
            midLevel = midLevel + " & " + curval;
            midLevel = midLevel + "  " + curtype;
            claimNotificationTextMid.setSingleLine(false);
            claimNotificationTextMid.setLines(2);
        }
        claimNotificationTextMid.setText(midLevel);

        if (robotoBlack != null) {
            claimNotificationTextMid.setTypeface(robotoBlack);
        }

        //Bottom line
        final TextView claimNotificationTextBottom = new TextView(context);
        claimNotificationTextBottom.setText(bottomLevel);


        //Dyanmically resize the text as necessary.
        if (bottomLevel.length() < 30) {
            claimNotificationTextBottom.setTextSize(14);
        }
        else if (bottomLevel.length() < 60) {
            claimNotificationTextBottom.setTextSize(12);
        }
        else {
            claimNotificationTextBottom.setTextSize(11);
        }

        claimNotificationTextBottom.setGravity(Gravity.CENTER);
        claimNotificationTextBottom.setTextColor(Color.parseColor("#FFFFFF"));
        claimNotificationTextBottom.setWidth((int) (deviceWidth * .4));            //Width is hard-coded to half the screen.

        if (robotoBlack != null) {
            claimNotificationTextBottom.setTypeface(robotoBlack);
        }

        claimTextParent.addView(claimNotificationTextTop);
        claimTextParent.addView(claimNotificationTextMid);
        claimTextParent.addView(claimNotificationTextBottom);
        LinearLayout claimTextParentLayout = new LinearLayout(context);
        claimTextParentLayout.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        claimTextParentLayout.addView(claimTextParent);
        claimTextParentLayout.setTranslationX(margin);
        claimTextParentLayout.setTranslationY(heightAdjustment);

        ImageView backgroundImage = new ImageView(context);
        backgroundImage.setImageBitmap(Bitmap.createScaledBitmap(decodeBase64(Base64Images.claimBG),
                deviceWidth, backgroundHeight, false));
        RelativeLayout backgroundImageLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams backgroundImageLayoutParams = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        backgroundImageLayout.addView(backgroundImage);
        //backgroundImageLayout.setLayoutParams(backgroundImageLayoutParams);
        backgroundImageLayout.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        claimLayoutParent.addView(backgroundImageLayout);

        ImageView claimImage = new ImageView(context);
        claimImage.setImageBitmap(decodeBase64(Base64Images.claimIcon));
        LinearLayout claimImageLayout = new LinearLayout(context);
        claimImageLayout.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        claimImageLayout.addView(claimImage);
        claimImageLayout.setTranslationX(-margin);
        claimImageLayout.setTranslationY(heightAdjustment);

        ImageView cross = new ImageView(context);
        cross.setImageBitmap(decodeBase64(Base64Images.cross));
        cross.setScaleX(1.2f);
        cross.setScaleY(1.2f);
        LinearLayout crossLayout = new LinearLayout(context);
        crossLayout.setGravity(Gravity.TOP | Gravity.END);
        crossLayout.addView(cross);
        crossLayout.setTranslationX(-margin / 2);
        crossLayout.setTranslationY(crossHeightAdjustment);

        /*  Bottom row - contains info button and logo */
        ImageView perkLogo = new ImageView(context);
        perkLogo.setImageBitmap(decodeBase64(Base64Images.perkLogoNotification));
        perkLogo.setTranslationX(margin / 2);

        ImageView perkInfo = new ImageView(context);
        perkInfo.setImageBitmap(decodeBase64(Base64Images.perkInfo));
        perkInfo.setTranslationX(-margin / 2);

        RelativeLayout.LayoutParams rightAlign = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rightAlign.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        RelativeLayout.LayoutParams leftAlign = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        leftAlign.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        FrameLayout.LayoutParams bottomAlign = new FrameLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bottomAlign.gravity = Gravity.BOTTOM;

        FrameLayout.LayoutParams centerAlign = new FrameLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        centerAlign.gravity = Gravity.CENTER;

        FrameLayout.LayoutParams upperRightAlign = new FrameLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        upperRightAlign.gravity = Gravity.END | Gravity.TOP;

        FrameLayout.LayoutParams leftCenterAlign = new FrameLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        leftCenterAlign.gravity = Gravity.START;// | Gravity.CENTER_VERTICAL;

        FrameLayout.LayoutParams rightCenterAlign = new FrameLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rightCenterAlign.gravity = Gravity.END;// | Gravity.CENTER_VERTICAL;

        bottomLevelButtons.addView(perkInfo, rightAlign);
        bottomLevelButtons.addView(perkLogo, leftAlign);
        bottomLevelButtons.setMinimumWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomLevelButtons.setGravity(Gravity.BOTTOM);
        /*  End bottom Row implementation */

        ImageView starburstGlow = new ImageView(context);
        starburstGlow.setImageBitmap(decodeBase64(Base64Images.starburstGlow));
        starburstGlow.setTranslationY(-coinHeightAdjustment);
        LinearLayout starburstGlowLayout = new LinearLayout(context);
        starburstGlowLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        starburstGlowLayout.addView(starburstGlow);

        ImageView starburst = new ImageView(context);
        starburst.setImageBitmap(decodeBase64(Base64Images.starburst));
        LinearLayout starburstLayout = new LinearLayout(context);
        starburstLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        starburstLayout.addView(starburst);

        ImageView coin = new ImageView(context);
        int resId = context.getResources().getIdentifier("perkpoint", "drawable", context.getPackageName());

        if (resId != 0) {
            coin.setBackgroundResource(resId);
            coin.setTranslationY(coinHeightAdjustment);

            AnimationDrawable frameAnimation = (AnimationDrawable) coin.getBackground();
            frameAnimation.start();
        }
        else {
            coin.setImageBitmap(decodeBase64(Base64Images.perkPlaceholderCoin));
            coin.setTranslationY(-placeholderCoinHeightAdjustment);
        }

        LinearLayout coinLayout = new LinearLayout(context);
        coinLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        coinLayout.addView(coin);


        perkInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertInfo(context);
            }
        });


        claimLayoutParent.addView(crossLayout);

        claimCombinationParent.addView(claimTextParentLayout);
        claimCombinationParent.addView(claimImageLayout);

        claimLayoutParent.addView(claimCombinationParent, centerAlign);
        claimLayoutParent.addView(bottomLevelButtons, bottomAlign);
        claimLayoutParent.addView(starburstGlowLayout);
        claimLayoutParent.addView(starburstLayout);
        claimLayoutParent.addView(coinLayout);

        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(claimLayoutParent);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);//(int)(dialog.getWindow().getAttributes().height*1.5));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        claimImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PerkManager.claimPoints(context, eventid);
                dialog.dismiss();
            }
        });

        cross.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.m_strEventExtra = "";
                PerkManager.sendNotification(PerkManager.PERK_MANAGER_SDK_CLIAM_CLOSE_BANNER);
                PerkManager.notifyApp(PerkManager.PERK_MANAGER_SDK_CLIAM_CLOSE_BANNER);
            }
        });

        backgroundImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.m_strEventExtra = "";
            }
        });

        dialog.show();
    }

    private static String getPointRewardString(NotificationRequest request) {
        return request.getPoints() + " Perk "
                + (request.getPoints() > 1 ? " Points! " : " Point! ");
    }

    public static void showReturnNotfication(Context context, @Nullable NotificationRequest request,
                                             int pointsEarned, boolean useGlobal, String currency, String currencyType) {

        //Check to see if we're even supposed to be doing this.
        if (Utils.m_suppressNotifications) {
            return;
        }

        String customdataponts = "";
        if (request instanceof End) {
            End end = (End) request;
            if (pointsEarned > 0) {
                customdataponts = String.valueOf(pointsEarned);
            }
            else if (end != null && end.getPoints() > 0) {
                customdataponts = Integer.toString(end.getPoints());
            }
        }
        else if (request instanceof Ad) {
            Ad ad = (Ad) request;
            if (pointsEarned > 0) {
                customdataponts = String.valueOf(pointsEarned);
            }
            else if (ad != null && ad.getPoints() > 0) {
                customdataponts = Integer.toString(ad.getPoints());
            }
        }

        if (customdataponts.length() > 0) {
            if (currency != null && currency.length() > 0 && currencyType != null && currencyType.length() > 0) {
                customdataponts = customdataponts + " & " + currency;
                customdataponts = customdataponts + " " + currencyType;
            }
        }


        if (Utils.mCustomNotification == true && Utils.perkInterface != null) {
            Utils.m_strNotificationText = request.getStub();
            Utils.m_strEventPoints = request.getPoints();
            Utils.perkInterface.showReturnNotfication();
            return;
        }

        if (Notidialog != null && Notidialog.isShowing()) {
            Notidialog.dismiss();
        }

        final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        d.getMetrics(displaymetrics);

        deviceHeight = displaymetrics.heightPixels;
        deviceWidth = displaymetrics.widthPixels;

        float heightAdjustment = deviceHeight / (1280.f / 35.f);  //This is set to 55p for 1280p devices.
        float textHeightAdjustment = deviceHeight / (1280.f / 15.f);  //This is set to 55p for 1280p devices.

        //Same background height here as the other notification.
        int backgroundHeight = (int) (deviceHeight / (1280.f / 280.f));   //280p for a 1280p device.
        Typeface robotoBlack = null;

        if (!PerkManager.getConfig().isUserLoggedIn(context)) {
            backgroundHeight = (int) (deviceHeight / (1280.f / 350.0f));   //280p for a 1280p device.
        }

        try {
            InputStream stream = Utils.m_objMainContext.getAssets().open("fonts/Roboto-Black.ttf");
            robotoBlack = Typeface.createFromAsset(Utils.m_objContext.getAssets(), "fonts/Roboto-Black.ttf");
            stream.close();
        }
        catch (FileNotFoundException e) {
            Log.w("AssetLoad", "assetExists failed: " + e.toString());
        }
        catch (IOException e) {
            Log.w("IOexception", "assetExists failed: " + e.toString());
        }

        String topLevel = "Congrats!";
        String midLevel = "";
        if (request instanceof End) {
            End end = (End) request;
            if (pointsEarned > 0) {
                midLevel = String.valueOf(pointsEarned);
            }
            else if (end != null && end.getPoints() > 0) {
                midLevel = Integer.toString(end.getPoints());
            }
        }
        else if (request instanceof Ad) {
            Ad end = (Ad) request;
            if (pointsEarned > 0) {
                midLevel = String.valueOf(pointsEarned);
            }
            else if (end != null && end.getPoints() > 0) {
                midLevel = Integer.toString(end.getPoints());
            }
        }

        midLevel = "You earned " + midLevel + " Perk" + ((Integer.parseInt(midLevel) > 1) ? " points!" : " point!");
        if (midLevel.length() > 0) {
            if (currency != null && currency.length() > 0 && currencyType != null && currencyType.length() > 0) {
                midLevel = midLevel + " & " + currency;
                midLevel = midLevel + " " + currencyType;
            }
        }
        //Trying this here - getting some weird 0 height/width bitmap crashes, potentially for an invalid context.
        Utils.m_objContext = Utils.m_objMainContext;

        final FrameLayout claimLayoutParent = new FrameLayout(Utils.m_objContext);

        final LinearLayout claimTextLayout = new LinearLayout(Utils.m_objContext);
        claimTextLayout.setOrientation(LinearLayout.VERTICAL);
        claimTextLayout.setGravity(Gravity.CENTER | Gravity.TOP);
        claimTextLayout.setTranslationY(textHeightAdjustment);

        final RelativeLayout bottomLevelButtons = new RelativeLayout(Utils.m_objContext);

        //Top line
        final StrokeTextView claimNotificationTextTop = new StrokeTextView(Utils.m_objContext);

        claimNotificationTextTop.setText(topLevel);
        claimNotificationTextTop.setTextSize(14);
        claimNotificationTextTop.setStrokeColor(Color.parseColor("#35a23b"));
        claimNotificationTextTop.setStrokeWidth(8);
        claimNotificationTextTop.setGravity(Gravity.CENTER);
        claimNotificationTextTop.setTextColor(Color.parseColor("#FFFFFF"));

        if (robotoBlack != null) {
            claimNotificationTextTop.setTypeface(robotoBlack);
        }
        //claimNotificationTextTop.setPadding(20,30,0,0);

        //Middle line
        final StrokeTextView claimNotificationTextMid = new StrokeTextView(Utils.m_objContext);

        claimNotificationTextMid.setText(midLevel);
        claimNotificationTextMid.setTextSize(16);
        claimNotificationTextMid.setStrokeColor(Color.parseColor("#35a23b"));
        claimNotificationTextMid.setStrokeWidth(8);
        claimNotificationTextMid.setGravity(Gravity.CENTER);
        claimNotificationTextMid.setTextColor(Color.parseColor("#FFFFFF"));

        if (robotoBlack != null) {
            claimNotificationTextMid.setTypeface(robotoBlack);
        }
        //claimNotificationTextMid.setPadding(20, 0, 0, 0);

        //Safeguard against potential crashes here.  There are instances where we may show
        //the return notification without initializing these variables, so do that here.
        if (deviceHeight == 0 || deviceWidth == 0 || backgroundHeight == 0) {
            //Note here that we'll kick out if the app is using custom notifications.
            return;
        }

        try {

            //This is basically serving as our flag for notifications.

            ImageView backgroundImage = new ImageView(Utils.m_objContext);
            Bitmap backgroundImageSource = Bitmap.createScaledBitmap(decodeBase64(Base64Images.claimBG),
                    deviceWidth, backgroundHeight, false);
            backgroundImage.setImageBitmap(backgroundImageSource);
            RelativeLayout backgroundImageLayout = new RelativeLayout(Utils.m_objContext);
            RelativeLayout.LayoutParams backgroundImageLayoutParams = new RelativeLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            backgroundImageLayout.addView(backgroundImage);
            //backgroundImageLayout.setLayoutParams(backgroundImageLayoutParams);
            backgroundImageLayout.setGravity(Gravity.CENTER | Gravity.BOTTOM);
            claimLayoutParent.addView(backgroundImageLayout);

            ImageView claimImage = new ImageView(Utils.m_objContext);
            ImageView secondaryClaimImage = new ImageView(Utils.m_objContext);
            claimImage.setImageBitmap(decodeBase64(Base64Images.earnEvenMore));

            LinearLayout claimImageLayout = new LinearLayout(Utils.m_objContext);
            claimImageLayout.setGravity(Gravity.CENTER);

            if (!PerkManager.getConfig().isUserLoggedIn(context)) {

                if (pointsEarned > 0) {
                    claimNotificationTextTop.setText(midLevel);
                    claimNotificationTextMid.setText("");
                }
                else if (request != null) {
                    String text = "You just got " + getPointRewardString(request);
                    claimNotificationTextTop.setText(text);
                    claimNotificationTextMid.setText("");

                }
                secondaryClaimImage.setImageBitmap(decodeBase64(Base64Images.logInToPerk));
                secondaryClaimImage.setPadding(0, 0, 0, 20);

                //Add the other button to our layout.
                claimImageLayout.setGravity(Gravity.CENTER);
                claimImageLayout.setOrientation(LinearLayout.VERTICAL);
                claimImageLayout.addView(secondaryClaimImage);
                claimImageLayout.setTranslationY(heightAdjustment);
                claimImageLayout.setPadding(0, 0, 0, 50);
            }

            claimImageLayout.addView(claimImage);
            claimImageLayout.setTranslationY(heightAdjustment);
            claimImageLayout.setPadding(0, 0, 0, 10);

            ImageView cross = new ImageView(Utils.m_objContext);
            cross.setImageBitmap(decodeBase64(Base64Images.cross));
            LinearLayout crossView = new LinearLayout(Utils.m_objContext);
            crossView.addView(cross);
            crossView.setTranslationY(10.f);
            crossView.setTranslationX(-30.f);
            crossView.setGravity(Gravity.END);

            ImageView perkLogo = new ImageView(Utils.m_objContext);
            perkLogo.setImageBitmap(decodeBase64(Base64Images.perkLogoNotification));

            ImageView perkInfo = new ImageView(Utils.m_objContext);
            perkInfo.setImageBitmap(decodeBase64(Base64Images.perkInfo));

            RelativeLayout.LayoutParams rightAlign = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rightAlign.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            RelativeLayout.LayoutParams leftAlign = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            leftAlign.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            bottomLevelButtons.addView(perkInfo, rightAlign);
            bottomLevelButtons.addView(perkLogo, leftAlign);
            bottomLevelButtons.setMinimumWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            bottomLevelButtons.setGravity(Gravity.BOTTOM);

            perkInfo.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAlertInfo(Utils.m_objMainContext);
                }

            });


            claimLayoutParent.addView(crossView);

            claimTextLayout.addView(claimNotificationTextTop);
            claimTextLayout.addView(claimNotificationTextMid);
            claimTextLayout.setPadding(0, 0, 0, 10);


            claimLayoutParent.addView(claimTextLayout);
            claimLayoutParent.addView(claimImageLayout);
            claimLayoutParent.addView(bottomLevelButtons);

            if (useGlobal == true) {
                Notidialog = new Dialog(Utils.m_objMainContext);
            }
            else {
                Notidialog = new Dialog(context);
            }

            Notidialog.setCanceledOnTouchOutside(false);

            Notidialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Notidialog.setContentView(claimLayoutParent);

            Notidialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            Notidialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, Notidialog.getWindow().getAttributes().height);
            Window window = Notidialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            claimImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PerkManager.showPortal(Utils.m_objContext);

                    Notidialog.dismiss();
                }
            });

            secondaryClaimImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PerkManager.launchLoginPage(Utils.m_objContext, "");

                    Notidialog.dismiss();
                }
            });

            cross.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notidialog.dismiss();
                    PerkManager.sendNotification(PerkManager.PERK_MANAGER_SDK_CLIAM_CLOSE_BANNER);
                    PerkManager.notifyApp(PerkManager.PERK_MANAGER_SDK_CLIAM_CLOSE_BANNER);
                }
            });

            backgroundImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notidialog.dismiss();
                }
            });

            Notidialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAdCountdown() {

        BitmapDrawable drawableBitmap = new BitmapDrawable(
                decodeBase64(Base64Images.claimBG));

        final LinearLayout claimLayoutParent = new LinearLayout(Utils.m_objContext);
        claimLayoutParent.setOrientation(LinearLayout.VERTICAL);
        claimLayoutParent.setBackgroundDrawable(drawableBitmap);
        claimLayoutParent.setGravity(Gravity.TOP);
        claimLayoutParent.setWeightSum(2f);
        claimLayoutParent.setPadding(20, 20, 0, 20);

        final TextView claimNotificationText = new TextView(Utils.m_objContext);
        claimNotificationText.setTextSize(12);
        claimNotificationText.setGravity(Gravity.CENTER);
        claimNotificationText.setPadding(0, 10, 0, 10);
        claimNotificationText.setTextColor(Color.parseColor("#FFFFFF"));

        String deviceResolution = Utils.getResolution(Utils.m_objContext);

        String[] width = deviceResolution.split("x");

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) Utils.m_objContext).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);


        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(Integer.parseInt(width[0]),
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        p.setMargins(0, 0, 10, 0);
        p.weight = 1;

        //claimLayoutParent.addView(perkStar);
        claimLayoutParent.setLayoutParams(p);
        claimLayoutParent.addView(claimNotificationText);
        //claimLayoutParent.addView(claimLayout);

        final Dialog dialog = new Dialog(Utils.m_objContext);
        dialog.setCanceledOnTouchOutside(false);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(claimLayoutParent);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.width = Integer.parseInt(width[0]);
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.flags &= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        window.setAttributes(wlp);

        Utils.m_bIsBlockingDialog = true;
        dialog.show();

        removeViewTimer = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                String message = "Your Perk Points are being transferred in " + (int) Math.ceil(millisUntilFinished / 1000);
                claimNotificationText.setText(message);
            }

            public void onFinish() {
                Utils.m_bIsBlockingDialog = false;
                dialog.dismiss();
            }

        }.start();

    }

    public static void fadeOutAndHideImage(final TextView txt) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(2000);

        fadeOut.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                txt.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        txt.startAnimation(fadeOut);
    }

    public static String getGMT() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 30);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMins));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        return sdf.format(c.getTime());

    }


    public static void showAlertInfo(Context context) {
        Intent intent = new Intent(context, LearnMoreActivity.class);
        context.startActivity(intent);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return resizedBitmap(BitmapFactory.decodeByteArray(decodedByte, 0,
                decodedByte.length));
    }

    public static Bitmap resizedBitmap(Bitmap bitmapOrg) {

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        float scaleWidth = Utils.m_objMetrics.scaledDensity;
        float scaleHeight = Utils.m_objMetrics.scaledDensity;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth / 2, scaleHeight / 2);

        // recreate the new Bitmap
        return Bitmap.createBitmap(bitmapOrg, 0, 0, width,
                height, matrix, true);
    }


    //Note here - moved from PerkSDKActivity for better access globally.
    public static String getCookie(String siteName, String CookieName) {

        String CookieValue = null;
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if (cookies == null) {
            return "";
        }
        String[] temp = cookies.split(";");

        if (temp.length > 0) {
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    if (temp1.length > 1 && temp1[1] != null) {
                        CookieValue = temp1[1];
                    }
                }
            }
        }

        return CookieValue;
    }

}