package com.perk.perksdk;

/**
 * <h1>Aerserv Ad Activity</h1>
 * Activity for showing aerserv ads.
 *
 * @author Perk.com
 * @version 1.0
 * @since 2014-12-01
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aerserv.sdk.dao.VideoFileCache;
import com.appsaholic.CommercialBreakSDK;
import com.appsaholic.adsdks.CommercialBreakSDKVideoCallback;
import com.perk.perksdk.adblocker.ApplyUtils;
import com.perk.perksdk.adblocker.Shell;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.ads.Ad;
import com.perk.perksdk.appsaholic.v1.ads.AdData;
import com.perk.perksdk.appsaholic.v1.ads.AdEndInterface;
import com.perk.perksdk.appsaholic.v1.end.End;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEvent;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEventManager;
import com.perk.perksdk.appsaholic.v1.events.ClaimEvent;
import com.perk.perksdk.appsaholic.v1.points.Points;
import com.perk.perksdk.utils.DelayedClickHandler;
import com.perk.perksdk.widget.CountdownTimer;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdsActivity extends Activity implements CommercialBreakSDKVideoCallback {

    public static final String ARG_EVENT = "com.perk.perksdk.AdsActivity.ARG_EVENT";
    public static final String ARG_EVENT_ID = "com.perk.perksdk.AdsActivity.ARG_EVENT_ID";
    public static final String ARG_PLACEMENT_ID = "com.perk.perksdk.AdsActivity.ARG_PLACEMENT_ID";
    public static final String ARG_CUSTOM_NOTIFICATION = "com.perk.perksdk.AdsActivity.ARG_CUSTOM_NOTIFICATION";
    public static final String ARG_SHOW_RETURN_NOTIFICATION = "com.perk.perksdk.AdsActivity.ARG_SHOW_RETURN_NOTIFICATION";
    public static final String ARG_TRACK = "com.perk.perksdk.AdsActivity.ARG_TRACK";
    public static final String ARG_FROM_PORTAL = "com.perk.perksdk.AdsActivity.ARG_FROM_PORTAL";
    public static final String ARG_FROM_WATCHMORE = "com.perk.perksdk.AdsActivity.ARG_FROM_WATCHMORE";
    public static final String ARG_AD_SOURCE = "com.perk.perksdk.AdsActivity.ARG_AD_SOURCE";

    //todo:this is actually terrible. m_bFromWatch should be an argument for initAerserv()
    private RelativeLayout mainLayout;
    private String total_perks = "0";
    private TextView tvPointsBalance;
    private ReplayListener replayListener = new ReplayListener();
    private View mRootView;
    private ViewGroup mPointsToaster;
    private CountdownTimer mTimer;
    private ImageView perkstar,toasterperkStar,info_icon,anotherlogo,learn_more;
    private boolean mCustomNotification = true;
    private boolean mFromWatchMore = false;
    private boolean mShowReturnNotification = false;
    private boolean mFromPortal = false;
    private boolean complete = false;
    private AppsaholicEvent mEvent;
    public Ad mAd = null;
    public End mEnd = null;
    private String mEventId;
    private int mPointsEarned = 0;
    private String mCurrencyAwarded = "0";
    private String mCurrencyType = "";
    private String mAdSource;
    private boolean mTrack = false;
    ImageView ivCollectPoints;
    TextView nextVideoText = null;
    public static boolean ispassivestarted = false;
    private View.OnClickListener learnMore = new DelayedClickHandler() {

        @Override
        public void onClick(View v) {
            super.onClick(v);
            leanMorePerk();
        }
    };

    private BroadcastReceiver internetReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (!PerkManager.isNetworkAvailable(AdsActivity.this)) {
                Log.w(Utils.TAG, "13");
                finish();
            }
        }
    };

    public void playCommercialBreak() {
        //todo:make commercial break use the proper Android Activity lifecycle tools
        Map<String, String> map = new HashMap<>();
        map.put("max_ads", String.valueOf(PerkManager.DEFAULT_MAX_ADS));
        map.put("close_button", "false");
        map.put("show_countdown_screen", "true");
        CommercialBreakSDK.showVideoAd(AdsActivity.this, map, AdsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(CountdownTimer.getCancelIntent());

        try {
            unregisterReceiver(replayListener);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(luanchLearnMore == true && PerkManager.getConfig().isPassivePlayEnabled())
        {
            luanchLearnMore = false;
            mTimer.start(true);
        }
        registerReceiver(replayListener, new IntentFilter(CountdownTimer.COUNTDOWN_FINISHED));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Bundle args = getIntent().getExtras();
            mEventId = args.getString(AdsActivity.ARG_EVENT_ID);
            mEvent = args.getParcelable(ARG_EVENT);
            mTrack = args.getBoolean(AdsActivity.ARG_TRACK);
            mCustomNotification = args.getBoolean(AdsActivity.ARG_CUSTOM_NOTIFICATION);
            mShowReturnNotification = args.getBoolean(AdsActivity.ARG_SHOW_RETURN_NOTIFICATION);
            mFromPortal = args.getBoolean(AdsActivity.ARG_FROM_PORTAL, false);
            mAdSource = args.getString(AdsActivity.ARG_AD_SOURCE);
            mFromWatchMore = args.getBoolean(AdsActivity.ARG_FROM_WATCHMORE, false);
            ispassivestarted = false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        mPointsEarned = 0;
        if (mEvent == null) {
            if (mTrack) {
                mEvent = AppsaholicEventManager.getTrackEvent(AdsActivity.this, mEventId);
            }
            else if (AppsaholicEvent.eventHasValidId(mEventId)) {
                mEvent = AppsaholicEventManager.getClaimEvent(AdsActivity.this, mEventId);
            }
            else {
                mEvent = AppsaholicEventManager.getEmptyClaimEvent(AdsActivity.this);
            }
        }

        Utils.m_strEventPoints = 0;
        Utils.m_strEventExtra = "0";
        Utils.m_strNotificationText = "";

        mEvent.setOnEventCompleteListener(new ClaimEvent.OnEventCompleteListener() {
            @Override
            public void onEndEventSuccess(End end) {
                AdsActivity.this.mEnd = end;
                AdsActivity.this.ivCollectPoints.setEnabled(true);
                if (mShowReturnNotification) {
                    adsEndpointSuccess(end.data);
                    PerkManager.setPerkAvailablePoints(AdsActivity.this, end.getPoints());

                    showPointsToaster();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPointsToaster.setVisibility(View.GONE);
                        }
                    }, DateUtils.SECOND_IN_MILLIS * 3);

                    mPointsEarned += end.getPoints();
                    complete = true;
                    finish();
                }
            }

            @Override
            public void onEndEventNotModified() {
                AdsActivity.this.ivCollectPoints.setEnabled(true);
                if (mEvent instanceof ClaimEvent) {
                    adsAdpointSuccess(((ClaimEvent) mEvent).adpoints);
                }
            }

            @Override
            public void onEndEventFailure(String message) {
                PerkManager.notifyApp("[onEndEventFailure]: " + message);
                AdsActivity.this.ivCollectPoints.setEnabled(true);
                //finish();
            }
        });

        mainLayout = new RelativeLayout(this);
        mainLayout.setBackgroundColor(Color.parseColor("#000000"));

        setContentView(getResources().getIdentifier("activity_ads", "layout", getPackageName()));
        Utils.setContext(AdsActivity.this);

        // find the views after setting the content view
        mTimer = (CountdownTimer) findViewById(getResources().getIdentifier("next_ad_timer", "id", getPackageName()));
        tvPointsBalance = (TextView) findViewById(getResources().getIdentifier("value_points", "id", getPackageName()));
        ImageView ivInfo = (ImageView) findViewById(getResources().getIdentifier("info_icon", "id", getPackageName()));
        ImageView ivWatchMore = (ImageView) findViewById(getResources().getIdentifier("watch_more", "id", getPackageName()));
        ivCollectPoints = (ImageView) findViewById(getResources().getIdentifier("collect_points", "id", getPackageName()));
        ImageView ivLearnMore = (ImageView) findViewById(getResources().getIdentifier("learn_more", "id", getPackageName()));
        ivCollectPoints.setEnabled(false);
        // set click handlers as needed
        Drawable watchMoreDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.earn_more_banner));
        ivWatchMore.setImageDrawable(watchMoreDrawable);

        Drawable collectPointsDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.collect_points));

        ivCollectPoints.setImageDrawable(collectPointsDrawable);

        ivInfo.setOnClickListener(learnMore);
        ivLearnMore.setOnClickListener(learnMore);
        ivWatchMore.setOnClickListener(new DelayedClickHandler() {

            @Override
            public void onClick(View v) {
                super.onClick(v);
                mTimer.reset();
                mFromWatchMore = true;
                initAd();
            }

        });

        ivCollectPoints.setOnClickListener(new DelayedClickHandler() {

            @Override
            public void onClick(View v) {
                super.onClick(v);

                if (mShowReturnNotification) {
                    if (mAd != null) {
                        if (mEvent instanceof ClaimEvent) {
                        }
                        else {
                            Functions.showReturnNotfication(AdsActivity.this, mAd, mPointsEarned, true, mCurrencyAwarded, mCurrencyType);
                        }
                    }

                }

                finish();
            }

        });


        if (!PerkManager.getConfig().isUserLoggedIn(AdsActivity.this)) {
            ImageView imgRedInfo = new ImageView(AdsActivity.this);
            imgRedInfo.setImageBitmap(Functions.decodeBase64(Base64Images.imgRedInfo));
        }

        this.registerReceiver(internetReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        if (PerkManager.getConfig().isPassivePlayEnabled()) {
            findViewById(getResources().getIdentifier("passive_play_disabled_text", "id", getPackageName())).setVisibility(View.GONE);
        }
        else {
            findViewById(getResources().getIdentifier("passive_play_enabled_text", "id", getPackageName())).setVisibility(View.GONE);
            findViewById(getResources().getIdentifier("info_icon", "id", getPackageName())).setOnClickListener(learnMore);
        }

        mRootView = findViewById(getResources().getIdentifier("root", "id", getPackageName()));
        nextVideoText = (TextView) findViewById(getResources().getIdentifier("flavor_text_next", "id", getPackageName()));
        mPointsToaster = (ViewGroup) findViewById(getResources().getIdentifier("points_toaster", "id", getPackageName()));
        mPointsToaster.setVisibility(View.GONE);
        toasterperkStar = (ImageView)findViewById(getResources().getIdentifier("toaster_perk_star", "id", getPackageName()));
        learn_more = (ImageView)findViewById(getResources().getIdentifier("learn_more", "id", getPackageName()));
        perkstar = (ImageView)findViewById(getResources().getIdentifier("perk_star", "id", getPackageName()));
        info_icon = (ImageView)findViewById(getResources().getIdentifier("info_icon", "id", getPackageName()));
        anotherlogo = (ImageView)findViewById(getResources().getIdentifier("logo", "id", getPackageName()));

        Drawable perkStarDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.perkStar));
        Drawable perkInfoDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.info_img));
        Drawable perkAnotherLogo = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.another_perk_img));
        Drawable learMoreDrawable = new BitmapDrawable(getResources(),
                Base64Images.decodeBase64(Base64Images.learn_more_img));
        learn_more.setImageDrawable(learMoreDrawable);
        anotherlogo.setImageDrawable(perkAnotherLogo);
        perkstar.setImageDrawable(perkStarDrawable);
        toasterperkStar.setImageDrawable(perkStarDrawable);
        info_icon.setImageDrawable(perkInfoDrawable);

        mRootView.setVisibility(View.GONE);

        if (Utils.detectAdBlockers(AdsActivity.this.getApplicationContext()) && Utils.m_strAdBlockStatus.equals("ON")) {
            adBlockDetected();
        }
        else {
            if (PerkManager.getConfig().isUserLoggedIn(AdsActivity.this)) {
                total_perks = String.valueOf(PerkManager.getPerkAvailablePoints(AdsActivity.this));
                this.tvPointsBalance.setText(total_perks);
            }

            initAd(mEvent.getId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adBlockDetected() {

        ContextThemeWrapper themedContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            themedContext = new ContextThemeWrapper(AdsActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        }
        else {
            themedContext = new ContextThemeWrapper(AdsActivity.this,
                    android.R.style.Theme_Light_NoTitleBar);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                themedContext);

        TextView msg = new TextView(AdsActivity.this);
        msg.setText("Ad Block Detected. Perk SDK doesn't support adblock apps. Please press FIX IT! for disabling adblockers.");
        msg.setPadding(20, 20, 20, 20);
        msg.setGravity(Gravity.CENTER);
        msg.setTextColor(Color.BLACK);
        msg.setTextSize(17);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder
                .setView(msg)
                .setCancelable(false)
                .setNegativeButton("FIX IT!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AdUnblockingTask().execute();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    /**
     * Reverts to default hosts file
     *
     * @return Status codes REVERT_SUCCESS or REVERT_FAIL
     */
    private boolean revert(Shell shell) {

        // build standard hosts file
        try {
            FileOutputStream fos = AdsActivity.this.getApplicationContext()
                    .openFileOutput(com.perk.perksdk.adblocker.Constants.HOSTS_FILENAME,
                            Context.MODE_PRIVATE);

            // default localhost
            String localhost = com.perk.perksdk.adblocker.Constants.LOCALHOST_IPv4 + " "
                    + com.perk.perksdk.adblocker.Constants.LOCALHOST_HOSTNAME + com.perk.perksdk.adblocker.Constants.LINE_SEPERATOR
                    + com.perk.perksdk.adblocker.Constants.LOCALHOST_IPv6 + " "
                    + com.perk.perksdk.adblocker.Constants.LOCALHOST_HOSTNAME;
            fos.write(localhost.getBytes());
            fos.close();

            // copy build hosts file with RootTools
            ApplyUtils.copyHostsFile(AdsActivity.this.getApplicationContext(),
                    com.perk.perksdk.adblocker.Constants.ANDROID_SYSTEM_ETC_HOSTS, shell);

            // delete generated hosts file after applying it
            AdsActivity.this.getApplicationContext().deleteFile(
                    com.perk.perksdk.adblocker.Constants.HOSTS_FILENAME);

            return true;
        }
        catch (Exception e) {

            return false;
        }
    }

    private void setPoints(Integer points, Integer eventpoints) {
        if (PerkManager.getConfig().isUserLoggedIn(AdsActivity.this)) {
            int total = PerkManager.getPerkAvailablePoints(AdsActivity.this);
            tvPointsBalance.setText(String.valueOf(total + points));
            PerkManager.setPerkAvailablePoints(AdsActivity.this, total + points);
        }
        else {
            PerkManager.updatePerkPendingPoints(AdsActivity.this, eventpoints);
            tvPointsBalance.setText("" + eventpoints);
            Utils.m_strEventExtra = eventpoints + " Perk Points";
        }
    }

    boolean luanchLearnMore = false;
    private void leanMorePerk() {
        mTimer.reset(PerkManager.getConfig().getPassiveCountdownSeconds(), false);
        Functions.showAlertInfo(AdsActivity.this);
        luanchLearnMore = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoFileCache.clearCache(AdsActivity.this);
        sendBroadcast(CountdownTimer.getCancelIntent());

        try {
            unregisterReceiver(internetReceiver);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (mShowReturnNotification && complete) {
            Functions.showReturnNotfication(AdsActivity.this, null, mPointsEarned, false, mCurrencyAwarded, mCurrencyType);
        }
    }

    private void adsAdpointSuccess(int points) {

        mPointsEarned += points;
        popToaster(points);

        if (mEvent.getId() == null || mEvent.getId().isEmpty()) {
            Utils.m_strEventPoints = points;

            Utils.m_strEventExtra = Utils.m_strEventPoints + " Perk Points";
        }
        else {
            Utils.m_strEventPoints += points;
        }

        setPoints(points,mPointsEarned);

        PerkManager.getUserInformation(AdsActivity.this, false);
    }

    private void adsEndpointSuccess(AdData adData) {
        if(adData != null) {
            int points = adData.getPoints();
            mPointsEarned += adData.getPoints();
            int val = Integer.parseInt(mCurrencyAwarded);
            if (adData.getCurrencyValue() != null && adData.getCurrencyValue().length() > 0) {
                val += Integer.parseInt(adData.getCurrencyValue());
            }
            mCurrencyAwarded = Integer.toString(val);
            Utils.m_strNotificationText = adData.getNotification().getText();
            popToaster(points);

            if (mEvent.getId() == null || mEvent.getId().isEmpty()) {
                Utils.m_strEventPoints = points;

                //Little housekeeping here - update this for the return notification.
                Utils.m_strEventExtra = Utils.m_strEventPoints + " Perk Points";
            }
            else {
                Utils.m_strEventPoints += points;
            }

            setPoints(points,mPointsEarned);

            //Update the user point values.
            PerkManager.getUserInformation(AdsActivity.this,false);
        }

    }

    private void showPointsToaster() {
        mPointsToaster.setVisibility(View.VISIBLE);
    }

    private void initAd() {
        playCommercialBreak();
    }

    private void initAd(String eventId) {
        playCommercialBreak();
    }

    private void popToaster(int points) {
        mPointsToaster.setVisibility(View.VISIBLE);
        TextView tvPoints = (TextView) mPointsToaster.findViewById(getResources().getIdentifier("toaster_text", "id", getPackageName()));
        tvPoints.setText(String.format(Locale.getDefault(), "+%d Perk Points!", points));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPointsToaster.setVisibility(View.GONE);
            }
        }, DateUtils.SECOND_IN_MILLIS * 3);
    }

    @Override
    public void onCommercialBreakStart() {
        if(mTrack == true) {
            PerkManager.notifyApp("AdServerWaterFallStarted");
        }
        else {
            PerkManager.notifyApp("onCommercialBreakStart");
        }
        hideUi();
    }

    @Override
    public void onCommercialBreakAdStart() {
        if(mTrack == true) {
            PerkManager.notifyApp("AdStartedSuccessfully");
        }
        else {
            PerkManager.notifyApp("onCommercialBreakAdStart");
        }
    }

    @Override
    public void onCommercialBreakAdComplete() {
        if(mTrack == true) {
            PerkManager.notifyApp("AdCompletedSuccessfully");
        }
        else {
            PerkManager.notifyApp("onCommercialBreakAdComplete");
        }
    }

    @Override
    public void onCommercialBreakComplete(int maxAds, int completed) {

        if (completed < maxAds) {
            if(mTrack == true) {
                PerkManager.notifyApp("AdServerWaterFallFail");
            }
            else {
                PerkManager.notifyApp("onCommercialBreakFail");
            }

            if (PerkManager.getConfig().isPassivePlayEnabled()) {
                showUi(true);
            }
            else {
                Functions.showAlertFail(AdsActivity.this, mCustomNotification, mFromPortal, mFromWatchMore);
            }

            return;
        }

        if (mTrack == true) {
            mEvent.end();
            //showUi(false);
            finish();
        }
        else {

            if (PerkManager.getConfig().isPassivePlayEnabled()) {
                if (mEvent != null && mEvent instanceof ClaimEvent && !mFromPortal && !mFromWatchMore) {
                    mEvent.end();
                    showUi(false);
                }
                else {
                    Ad.get(AdsActivity.this, mEventId, mAdSource, false, mFromWatchMore, new AdEndInterface() {
                        @Override
                        public void notModified() {

                        }

                        @Override
                        public void success(Ad ad) {
                            PerkManager.notifyApp("Rewarded for watching portal video");
                            Points.legacyUpdateUserPoints(AdsActivity.this);
                            mAd = ad;
                            AdsActivity.this.ivCollectPoints.setEnabled(true);
                            adsEndpointSuccess(ad.data);
                            if (mShowReturnNotification == true) {
                                if (mEvent != null && mEvent instanceof ClaimEvent) {
                                    String preveventcurrency = ((ClaimEvent) mEvent).mCurrencyEarned;
                                    preveventcurrency = Integer.toString(Integer.parseInt(preveventcurrency)) + mCurrencyAwarded;
                                    String preveventcurrencytype = ((ClaimEvent) mEvent).mCurrencyType;
                                    Functions.showReturnNotfication(getApplicationContext(), ad, mPointsEarned, true, preveventcurrency, preveventcurrencytype);
                                }

                            }
                        }

                        @Override
                        public void failure(String message) {
                            PerkManager.notifyApp("Failed to reward user for portal video");
                        }
                    });
                    showUi(false);
                }
            }
            else {
                if (mEvent != null && mEvent instanceof ClaimEvent && !mFromPortal && !mFromWatchMore) {
                    mEvent.end();
                    showUi(false);
                }
                else {
                    Ad.get(AdsActivity.this, mEventId, mAdSource, false, mFromWatchMore, new AdEndInterface() {
                        @Override
                        public void notModified() {
                        }

                        @Override
                        public void success(Ad ad) {
                            PerkManager.notifyApp("Rewarded for watching portal video");
                            Points.legacyUpdateUserPoints(AdsActivity.this);
                            mAd = ad;
                            AdsActivity.this.ivCollectPoints.setEnabled(true);
                            adsEndpointSuccess(ad.data);
                            showUi(false);
                            if (mShowReturnNotification == true) {
                                if (mEvent != null && mEvent instanceof ClaimEvent) {
                                    String preveventcurrency = ((ClaimEvent) mEvent).mCurrencyEarned;
                                    if (Integer.parseInt(mCurrencyAwarded) > 0) {
                                        preveventcurrency = Integer.toString(Integer.parseInt(preveventcurrency)) + mCurrencyAwarded;
                                    }
                                    String preveventcurrencytype = ((ClaimEvent) mEvent).mCurrencyType;
                                    Functions.showReturnNotfication(getApplicationContext(), ad, mPointsEarned, true, preveventcurrency, preveventcurrencytype);
                                }

                            }
                        }

                        @Override
                        public void failure(String message) {
                            PerkManager.notifyApp("Failed to reward user for portal video");
                        }
                    });
                }
            }
        }

    }


    private void hideUi() {
        mRootView.setVisibility(View.INVISIBLE);
    }

    private void showUi(boolean isfail) {
        mRootView.setVisibility(View.VISIBLE);
        if (PerkManager.getConfig().isPassivePlayEnabled()) {
            mTimer.reset(PerkManager.getConfig().getPassiveCountdownSeconds() + 1,true);
            mTimer.start(false);
        }
        if(isfail == true) {
            nextVideoText.setText(getResources().getIdentifier("ad_next_text_retry", "string", getPackageName()));
            AdsActivity.this.ivCollectPoints.setEnabled(true);
        }
        else {
            nextVideoText.setText(getResources().getIdentifier("ad_next_text", "string", getPackageName()));
        }
        if(PerkManager.getUserAccessToken(AdsActivity.this).length() > 0) {
            int total = PerkManager.getPerkAvailablePoints(AdsActivity.this);
            tvPointsBalance.setText(String.valueOf(total));
        }
    }

    private class ReplayListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case CountdownTimer.COUNTDOWN_FINISHED: {
                    sendBroadcast(CountdownTimer.getCancelIntent());
                    initAd();
                }
                break;
            }
        }
    }

    private class AdUnblockingTask extends AsyncTask<String, String, Boolean> {

        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean revertResult = false;
            try {

                Shell rootShell = Shell.startRootShell();
                revertResult = revert(rootShell);
                rootShell.close();

            }
            catch (Exception e) {

                e.printStackTrace();
                return revertResult;
            }

            return revertResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {

                Toast.makeText(getApplicationContext(),
                        "Ad blocker service disabled successfully",
                        Toast.LENGTH_SHORT).show();
                Log.w(Utils.TAG, "12");
                finish();

            }
            else {
                Toast.makeText(
                        getApplicationContext(),
                        "Error while disabling ad blocker service, Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
