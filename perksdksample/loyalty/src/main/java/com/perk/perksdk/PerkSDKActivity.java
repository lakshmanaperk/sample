package com.perk.perksdk;

/**
 * <h1>Open portal activity</h1>
 * Activity for opening up the portal URL and doing all related stuff.
 *
 * @author Perk.com
 * @version 1.0
 * @since 2014-12-01
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsaholic.commercialbreaksdk.BuildConfig;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.ads.Ad;
import com.perk.perksdk.appsaholic.v1.ads.AdEndInterface;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;
import com.perk.perksdk.appsaholic.v1.end.End;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEventManager;
import com.perk.perksdk.appsaholic.v1.events.ClaimEvent;
import com.perk.perksdk.appsaholic.v1.events.TrackEvent;
import com.perk.perksdk.appsaholic.v1.points.Points;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PerkSDKActivity extends Activity {

    public static final String VIEW = "com.perk.perksdk.PerkSDKActivity.ACTION_VIEW";
    public static final String ARG_TRACK_EVENT = "com.perk.perksdk.PerkSDKActivity.ARG_TRACK_EVENT";
    public static final String ARG_CLAIM_EVENT = "com.perk.perksdk.PerkSDKActivity.ARG_CLAIM_EVENT";
    public static final String ARG_EVENT_ID = "com.perk.perksdk.PerkSDKActivity.ARG_EVENT_ID";
    public static final String ARG_PLACEMENT_ID = "com.perk.perksdk.PerkSDKActivity.ARG_PLACEMENT_ID";
    public static final String ARG_IS_EVENT_AD = "com.perk.perksdk.PerkSDKActivity.ARG_IS_EVENT_AD";
    public static final String ARG_REQUEST_NATIVE = "com.perk.perksdk.PerkSDKActivity.ARG_REQUEST_NATIVE";
    public static final String ARG_SHOW_RETURN = "com.perk.perksdk.PerkSDKActivity.ARG_SHOW_RETURN";

    public static final String APSALAR_URL_ROOT = "http://ad.apsalar.com/api/v1/ad?re=0&a=perkmobile&i=";

    WebView webview;
    RelativeLayout.LayoutParams rlp;
    String mCommonParams = "";

    Map<String, String> extraHeaders = new HashMap<>();
    private String mEventId = "";
    private String mPlacementId = "";
    private boolean isFromCreate = true;
    private boolean mNativeRequest = false;
    private boolean mEventAd = false;
    private boolean mShowReturn = false;
    private TrackEvent mTrackEvent;
    private ClaimEvent mClaimEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        webview = new WebView(this);
        setContentView(webview);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            mTrackEvent = args.getParcelable(ARG_TRACK_EVENT);
            mClaimEvent = args.getParcelable(ARG_CLAIM_EVENT);
            mEventId = args.getString(ARG_EVENT_ID);
            mPlacementId = args.getString(ARG_PLACEMENT_ID);
            mEventAd = args.getBoolean(ARG_IS_EVENT_AD);
            mNativeRequest = args.getBoolean(ARG_REQUEST_NATIVE);
            mShowReturn = args.getBoolean(ARG_SHOW_RETURN);
        }

        isFromCreate = true;
        String url = "";
        Uri uri = getIntent().getData();
        if (uri != null) {
            url = uri.toString();
        }

        mCommonParams = "?api_key=" + PerkManager.getAppKey()
                + "&advertising_id=" + PerkManager.getAdInfo().getId()
                + "&sdk_version=" + BuildConfig.VERSION_NAME
                + "&access_token=" + PerkManager.getUserAccessToken(PerkSDKActivity.this)
                + "&event_id=" + mEventId;

        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);
        extraHeaders.put("Device-Info", PerkManager.getDeviceInfo());

        //Adding the A-L header for everything.
        extraHeaders.put("Accept-Language", Locale.getDefault().toString());
        extraHeaders.put("User-Agent", webview.getSettings().getUserAgentString());

        if (PerkManager.isNetworkAvailable(PerkSDKActivity.this)) {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setDomStorageEnabled(true);

            webview.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    setProgress(progress * 100);
                    if (progress == 100) {
                        setProgressBarIndeterminateVisibility(false);
                        setProgressBarVisibility(false);
                    }
                }

            });

            webview.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url,
                                          Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    String ipAddress = PerkManager.getConfig().getDeviceIp(PerkSDKActivity.this);
                    if (url.equals("watchtv:")) {
                        Functions.loadVideoAd(PerkSDKActivity.this, false, true, false, mEventId);
                    }
                    else if (url.contains(Constants.logout)) {
                        logoutUser();
                    }
                    else if (url.equals(Constants.reset_new_password)) {
                        CookieManager cookieManager = CookieManager
                                .getInstance();
                        cookieManager.setAcceptCookie(true);
                        cookieManager.setCookie(url, "from=" + "appsaholic");
                        view.loadUrl(url, extraHeaders);
                    }
                    else if (url.equals("perktv:")) {
                        //todo:clean this up. there are what look like IDs baked in here. We should parameterize all URLs
                        try {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=772759447203&h=3f7c16709e6bc7204d6ef8cd56fb20dd3dfe614d")));
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=772759447203&h=3f7c16709e6bc7204d6ef8cd56fb20dd3dfe614d")));
                        }
                    }
                    else if (url.equals("perklivetv:")) {
                        try {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=773632905966&h=50c318a75febfcc7a030beb437883859c65ea023")));
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=773632905966&h=50c318a75febfcc7a030beb437883859c65ea023")));
                        }
                    }
                    else if (url.equals("perkscratchandwin:") ) {
                        try {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=771885988440&h=897b95bb44b8379a98d7e49d398d5d9e91f18378")));
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=771885988440&h=897b95bb44b8379a98d7e49d398d5d9e91f18378")));
                        }
                    }
                    else if (url.equals("perkunlockandwin:")) {
                        try {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=774506364729&h=9f8b01ecabc38eade00550c07d117835ceaedede")));
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://ad.apsalar.com/api/v1/ad?re=0&st=774506364729&h=9f8b01ecabc38eade00550c07d117835ceaedede")));
                        }
                    }
                    else if (url.contains("closewindow://") || url.contains("appsaholic:") || url.contains("appsaholichome:")) {
                        String accessToken = Functions.getCookie(Constants.perk_account, "token");
                        String userID = Functions.getCookie(Constants.perk_account, "uid");
                        PerkManager.setUserAccessToken(PerkSDKActivity.this, accessToken);
                        PerkManager.setUserLoginId(PerkSDKActivity.this, userID);
                        PerkManager.getUserInformation(PerkSDKActivity.this, false);
                        loadHomePage();
                    }
                    else if (url.contains("closepoplogin://")) {
                        //TEMP TEMP TEMP TEMP TEMP for ZTE
                        loadHomePage();
                    }
                    else if (url.contains("geoclose:") || url.toLowerCase().contains("closehome:") || url.toLowerCase().contains("geookay:")) {
                        String accessToken = Functions.getCookie(Constants.perk_sdk_start, "token");
                        if(accessToken.length() > 0 && url.toLowerCase().contains("closehome:")) {
                            PerkManager.notifyApp("User Loggedin");
                        }
                        String userID = Functions.getCookie(Constants.perk_sdk_start, "uid");
                        PerkManager.setUserAccessToken(Utils.m_objContext, accessToken);
                        PerkManager.setUserLoginId(Utils.m_objContext, userID);
                        PerkManager.getUserInformation(Utils.m_objContext, false);
                        PerkManager.sendNotification(PerkManager.PERK_MANAGER_SDK_LOGIN_PORTAL_CLOSE);
                        PerkManager.notifyApp(PerkManager.PERK_MANAGER_SDK_LOGIN_PORTAL_CLOSE);
                        if(url.toLowerCase().contains("closehome:") && Utils.isRewardsCalled == true) {
                            String[] redirectedurl = url.split("closehome://");
                            if(redirectedurl.length > 1 && redirectedurl[1] != null && redirectedurl[1].length() > 0)
                                PerkManager.showPrizePage(PerkSDKActivity.this,Constants.redeem_coupon+redirectedurl[1]);
                            Utils.isRewardsCalled = false;
                        }
                        finish();
                    }
                    else if (url.startsWith("mailto:")) {
                        String namepass[] = url.split(":");
                        String pass = namepass[1];

                        String body = "Enter your Question, Enquiry or Feedback below:\n\n";
                        Intent mail = new Intent(Intent.ACTION_SEND);
                        mail.setType("application/octet-stream");
                        mail.putExtra(Intent.EXTRA_EMAIL, new String[]{pass});
                        mail.putExtra(Intent.EXTRA_SUBJECT,
                                "Query Regarding Appsaholic");
                        mail.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(mail);
                        return true;
                    }
                    else if (url.startsWith("market://")) {
                        String namepass[] = url.split("//");
                        String pass = namepass[1];

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("https://play.google.com/store/apps/"
                                        + pass)));
                    }
                    else if (url.equals("http://search.perk.com/")) {

                    }
                    else if (url.contains("eventid:")) {
                        showProgressBar("Loading");
                        String eventId = url.substring(url.indexOf(":"), url.lastIndexOf(":"));
                        eventId = eventId.replace(":", "");
                        final ClaimEvent event = AppsaholicEventManager.getClaimEvent(PerkSDKActivity.this, eventId);

                        // if the event is tracked successfully load the ad
                        event.setSuccessAction(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Provider> waterfall = event.getWaterfall();
                                if (event.isAdSupported()) {
                                    AppsaholicEventManager.loadAd(PerkSDKActivity.this, event, waterfall, false, true, false, false, false);
                                }
                                else {
                                    event.end();
                                }
                                hideProgressBar();
                            }
                        }).setFailureAction(new Runnable() {
                            @Override
                            public void run() {
                                //show the failure message
                                Functions.showAlertFail(PerkSDKActivity.this, false, true, false);
                            }
                        }).start();
                    }
                    else if (url.contains("sdkstatus:")) {
                        String str = url.toString();
                        str = str.replace("sdkstatus://", "");
                        str = str.replace("sdkstatus:", "");
                        Utils.m_strSdkStatus = str;
                        PerkManager.setSDKInitStaus(PerkManager.GetSDKStatus());
                    }
                    else if (url.contains("pubnative")) {
                        //TODO:  This needs to account for real no fills - this is temp.
                        if (url.contains("nofill")) {
                            webview.loadUrl(Constants.datapoint_survey, extraHeaders);
                            //Kill the countdown timer if necessary.
                            if (Functions.removeViewTimer != null) {
                                Functions.removeViewTimer.cancel();
                            }
                            finish();
                        }
                        else if (url.contains("close")) {
                            End.get(PerkSDKActivity.this, mEventId, null, null, null);
                            Points.legacyUpdateUserPoints(PerkSDKActivity.this);
                            finish();
                        }
                        else {
                            //This is our click-through.
                            view.loadUrl(url);
                        }
                    }
                    else if (url.contains("tapsense")) {
                        //TODO:  This needs to account for real no fills - this is temp.
                        if (url.contains("nofill")) {
                            webview.loadUrl(Constants.datapoint_survey, extraHeaders);
                            //Kill the countdown timer if necessary.
                            if (Functions.removeViewTimer != null) {
                                Functions.removeViewTimer.cancel();
                            }
                            finish();
                        }
                        else if (url.contains("close")) {
                            End.get(PerkSDKActivity.this, mEventId, null, null, null);
                            Points.legacyUpdateUserPoints(PerkSDKActivity.this);
                            finish();
                        }
                        else {
                            //This is our click-through.
                            view.loadUrl(url);
                        }
                    }
                    else if (url.contains("datapoints")) {
                        if (url.contains("nofill")) {
                            if (mTrackEvent != null) {
                                Functions.showSurveyFail(PerkSDKActivity.this, (TrackEvent) mTrackEvent);
                            }
                            else if (mClaimEvent != null) {
                                Functions.showSurveyFailForClaim(PerkSDKActivity.this, (ClaimEvent) mClaimEvent);
                            }
                        }
                        else if (url.contains("nothanks")) {
                            showIncompleteActionMessage();
                        }
                    }
                    else if (url.equals("survey://success")) {
                        //Award the points for datapoints survey and kill it.
                        if (mTrackEvent != null) {
                            mTrackEvent.end();
                        }
                        else if (mClaimEvent != null) {
                            AwardPointThrougAd((ClaimEvent) mClaimEvent);
                        }

                        finish();
                    }
                    else if (url.contains("opensurvey://")) {
                        if (mTrackEvent != null) {
                            Functions.loadDataPoints(PerkSDKActivity.this, (TrackEvent) mTrackEvent, null, false);
                        }
                        else if (mClaimEvent != null) {
                            Functions.loadDataPointsWithClaim(PerkSDKActivity.this, (ClaimEvent) mClaimEvent, null, false);
                        }
                        else {
                            ClaimEvent event = new AppsaholicEventManager().getClaimEvent(getApplicationContext(), "");
                            event.setAdSource("DPN");
                            Functions.loadDataPointsWithClaim(PerkSDKActivity.this, event, null, false);
                        }
                    }
                    else if (url.contains("closeportal")) {
                        //If we're closing this here, clear this flag.
                        Utils.m_bPortalDestination = false;
                        PerkManager.sendNotification(PerkManager.PERK_MANAGER_SDK_PORTAL_CLOSE);
                        PerkManager.notifyApp(PerkManager.PERK_MANAGER_SDK_PORTAL_CLOSE);
                        finish();
                    }
                    else if (url.contains("fyber") || url.contains("trialpay")) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }
                        catch (Exception e) {
                            // Chrome is probably not installed
                        }
                    }
                    else {
                        if (PerkManager.getUserAccessToken(PerkSDKActivity.this).length() > 0 && url.contains("perk.com")) {
                            CookieManager cookieManager = CookieManager
                                    .getInstance();
                            cookieManager.setAcceptCookie(true);
                            cookieManager.setCookie(
                                    url,
                                    "token=" + PerkManager.getUserAccessToken(PerkSDKActivity.this)
                                            + "; expires='"
                                            + Functions.getGMT() + " GMT'");
                            cookieManager.setCookie(Constants.account, "id="
                                    + PerkManager.getUserLoginId(PerkSDKActivity.this)
                                    + "; expires='" + Functions.getGMT()
                                    + " GMT'");
                        }
                        view.loadUrl(url, extraHeaders);
                    }

                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    if (url.equals(Constants.viewallnotifications)) {

                        CookieManager cookieManager = CookieManager
                                .getInstance();
                        cookieManager.setAcceptCookie(true);
                        cookieManager.setCookie(
                                Constants.account,
                                "token=" + PerkManager.getUserAccessToken(PerkSDKActivity.this)
                                        + "; expires='"
                                        + Functions.getGMT() + " GMT'");
                        cookieManager.setCookie(Constants.account, "id="
                                + PerkManager.getUserLoginId(PerkSDKActivity.this)
                                + "; expires='" + Functions.getGMT()
                                + " GMT'");
                    }

                    if (url.contains(Constants.perk_sdk_start)) {
                        webview.clearHistory();
                        try {
                            String token = Functions.getCookie(url, "token");
                            if(token !=null && token.length() > 0) {
                                PerkManager.setUserAccessToken(PerkSDKActivity.this, token);
                                PerkManager.getUserInformation(PerkSDKActivity.this, false);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        CookieManager cookieManager = CookieManager
                                .getInstance();
                        cookieManager.setAcceptCookie(true);
                        cookieManager.setCookie(Constants.account,
                                "token=" + PerkManager.getUserAccessToken(PerkSDKActivity.this)
                                        + "; expires='" + Functions.getGMT() + " GMT'");
                    }

//                    if (url.equals(Constants.perk_account)) {
//                        loadHomePage();
//                    }

                    if (url.equals(Constants.logout)) {
                        logoutUser();
                    }

                    if (url.equals("closewindow://") || url.equals("appsaholic:")) {
                        view.loadUrl(Constants.perk_sdk_start + "?api_key="
                                + PerkManager.getAppKey() + "&advertising_id="
                                + PerkManager.getAdInfo().getId(), extraHeaders);
                        webview.clearHistory();
                    }

                    if (mEventAd) {
                        if (Utils.m_surveyIncomplete.equals("true")) {
                            showIncompleteActionMessage();
                        }
                    }
                }

                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    if (errorCode == -2) {
                        finish();
                        Functions.networkIssueDialog();
                    }
                }

                //todo:why is this overridden only to return null?
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    return null;
                }
            });

            String userAgent = webview.getSettings().getUserAgentString();

            webview.getSettings().setUserAgentString(userAgent + " PALSDKA");

            if (!url.isEmpty()) {
                webview.loadUrl(url, extraHeaders);

                if (url.contains("pubnative") || url.contains("tapsense")) {
                    Functions.showAdCountdown();
                }
            }
            else {
                webview.loadUrl(Constants.perk_sdk_start + mCommonParams, extraHeaders);
            }
        }
        invalidateOptionsMenu();
    }

    ProgressDialog  locprogressbar = null;
    public void showProgressBar(String str) {
        if (locprogressbar == null) {
            locprogressbar = new ProgressDialog(PerkSDKActivity.this);
        }
        if (locprogressbar.isShowing()) {
            locprogressbar.setMessage(str);
        }
        else {
            locprogressbar.setMessage(str);
            locprogressbar.show();
        }
    }

    public void hideProgressBar() {
        if (locprogressbar != null && locprogressbar.isShowing()) {
            locprogressbar.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromCreate == false) {
            webview.reload();
        }
        else {
            isFromCreate = false;
        }
    }

    public void onBackPressed() {
        if (PerkManager.isNetworkAvailable(PerkSDKActivity.this)) {
            if (webview.canGoBack()) {
                finish();
                if (!mNativeRequest) {
                    Functions.loadMainPage(PerkSDKActivity.this);
                }
                else if (mShowReturn) {
                    End.get(PerkSDKActivity.this, mEventId, null, null, new RequestListener<End>() {

                        @Override
                        public void success(End result) {
                            Functions.showReturnNotfication(PerkSDKActivity.this, result, result.getPoints(), false, "", "");
                        }

                        @Override
                        public void failure(String message) {

                        }
                    });
                }
                return;
            }
            else {
                  //This should only happen at the portal
                if(webview.getUrl() != null && webview.getUrl().length() > 0 && webview.getUrl().contains("unclaimed_events")) {
                    PerkManager.sendNotification(PerkManager.PERK_MANAGER_SDK_UNCLAIMED_PORTAL_CLOSE);
                    PerkManager.notifyApp(PerkManager.PERK_MANAGER_SDK_UNCLAIMED_PORTAL_CLOSE);
                }
                finish();
            }
        }


        if (!Utils.m_bIsBlockingDialog) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                String url = webview.getUrl();

                if (url.startsWith(Constants.perk_sdk_start)) {
                    finish();
                }
                else {
                    loadHomePage();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void AwardPointThrougAd(ClaimEvent event) {
        Ad.get(PerkSDKActivity.this, event.getId(), "DPN", false, false, new AdEndInterface() {
            @Override
            public void notModified() {

            }

            @Override
            public void success(Ad ad) {
                PerkManager.notifyApp("Rewarded for watching portal video");
            }

            @Override
            public void failure(String message) {
                PerkManager.notifyApp("Failed to reward user for portal video");
            }
        });
    }

    public void showIncompleteActionMessage() {

        LinearLayout parentLayout = new LinearLayout(PerkSDKActivity.this);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.setBackgroundColor(Color.WHITE);
        String deviceResolution = Utils.getResolution(PerkSDKActivity.this);

        String[] width = deviceResolution.split("x");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;

        if (density == DisplayMetrics.DENSITY_XHIGH) {
            rlp = new RelativeLayout.LayoutParams(Integer.parseInt(width[0]),
                    200);
        }
        else if (density == DisplayMetrics.DENSITY_XXHIGH) {
            rlp = new RelativeLayout.LayoutParams(Integer.parseInt(width[0]),
                    300);
        }
        else {
            rlp = new RelativeLayout.LayoutParams(Integer.parseInt(width[0]),
                    200);
        }

        TextView tv = new TextView(PerkSDKActivity.this);
        tv.setText("You must complete the survey\nto earn Perk Points.");
        tv.setTextSize(18);
        tv.setPadding(20, 20, 20, 10);
        tv.setWidth(Integer.parseInt(width[0]) - 200);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.GRAY);

        // SignUp Button
        Button btn = new Button(PerkSDKActivity.this);
        btn.setText("Take The Survey");
        btn.setTextColor(Color.parseColor("#0099e0"));
        btn.setTextSize(15);
        btn.setGravity(Gravity.CENTER);

        // LogIn Button
        Button quitBtn = new Button(PerkSDKActivity.this);
        quitBtn.setText("Back");
        quitBtn.setTextColor(Color.parseColor("#0099e0"));
        quitBtn.setTextSize(15);
        quitBtn.setGravity(Gravity.CENTER);

        LinearLayout btnParentLayout = new LinearLayout(PerkSDKActivity.this);
        btnParentLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams btnParam = new LinearLayout.LayoutParams(
                Functions.getPixels(150),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btn.setLayoutParams(btnParam);
        quitBtn.setLayoutParams(btnParam);

        RelativeLayout.LayoutParams buttonParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParam.topMargin = 20;
        btnParentLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        btnParentLayout.setLayoutParams(buttonParam);
        btnParentLayout.addView(btn);
        btnParentLayout.addView(quitBtn);
        parentLayout.addView(tv);
        parentLayout.addView(btnParentLayout);


        final Dialog dialog = new Dialog(PerkSDKActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(parentLayout, rlp);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.x = 0;
        wlp.y = 200;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        quitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNativeRequest) {
                    finish();
                }
                else {
                    webview.loadUrl(Constants.perk_sdk_start + mCommonParams, extraHeaders);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem item = menu.add("My Account");
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                loadMyAccount();
                return true;
            }
        });

        MenuItem itemClose = menu.add("Close");
        itemClose.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });
        return true;
    }

    private void loadMyAccount() {
        if (PerkManager.getConfig().isUserLoggedIn(PerkSDKActivity.this)) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(Constants.account,
                    "token=" + PerkManager.getUserAccessToken(PerkSDKActivity.this)
                            + "; expires='" + Functions.getGMT() + " GMT'");
            cookieManager.setCookie(Constants.account, "id="
                    + PerkManager.getUserLoginId(PerkSDKActivity.this)
                    + "; expires='" + Functions.getGMT()
                    + " GMT'");

            webview.loadUrl(Constants.account + mCommonParams + "&for=appsaholic", extraHeaders);
        }
        else {
            webview.loadUrl(Constants.perk_account + mCommonParams, extraHeaders);
        }
    }

    private void loadHomePage() {

        CookieManager cookieManager = CookieManager
                .getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(Constants.perk_sdk_start,
                "token=" + PerkManager.getUserAccessToken(PerkSDKActivity.this)
                        + "; expires='" + Functions.getGMT() + " GMT'");

        webview.loadUrl(Constants.perk_sdk_start + mCommonParams, extraHeaders);
    }

    private void logoutUser() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        PerkManager.logoutUser(getApplicationContext());
        webview.clearHistory();
        loadHomePage();
    }
}
