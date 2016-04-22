package com.perk.perksdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.end.End;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEvent;
import com.perk.perksdk.appsaholic.v1.events.AppsaholicEventManager;
import com.perk.perksdk.appsaholic.v1.events.ClaimEvent;
import com.perk.perksdk.appsaholic.v1.points.Points;

import java.util.ArrayList;

public class FacebookAdNetworkActivity extends Activity implements InterstitialAdListener {

    public static String ARG_SHOW_RETURN = "com.perk.perksdk.FacebookAdNetworkActivity.ARG_SHOW_RETURN";
    private InterstitialAd interstitialAd;
    private AppsaholicEvent mEvent;
    private boolean mTrack = false;
    private boolean mShowReturnNotification = false;
    private String mPlacementId;
    ArrayList<Provider> waterfall = null;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResources().getIdentifier("activity_fan_ad", "layout", getPackageName()));

        String event = "";
        try {
            Bundle args = getIntent().getExtras();
            event = args.getString(AdsActivity.ARG_EVENT_ID);
            mEvent = args.getParcelable(AdsActivity.ARG_EVENT);
            mTrack = args.getBoolean(AdsActivity.ARG_TRACK);
            mShowReturnNotification = args.getBoolean(AdsActivity.ARG_SHOW_RETURN_NOTIFICATION);
            mPlacementId = args.getString(AdsActivity.ARG_PLACEMENT_ID);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (mEvent == null) {
            if (mTrack) {
                mEvent = AppsaholicEventManager.getTrackEvent(FacebookAdNetworkActivity.this, event);
            }
            else if (AppsaholicEvent.eventHasValidId(event)) {
                mEvent = AppsaholicEventManager.getClaimEvent(FacebookAdNetworkActivity.this, event);
            }
            else {
                mEvent = AppsaholicEventManager.getEmptyClaimEvent(FacebookAdNetworkActivity.this);
            }
        }

        Utils.m_strEventPoints = 0;
        Utils.m_strEventExtra = "0";
        Utils.m_strNotificationText = "";

        interstitialAd = new InterstitialAd(FacebookAdNetworkActivity.this, mPlacementId);
        interstitialAd.loadAd();
        PerkManager.notifyApp("AdServerWaterFallStarted");
        interstitialAd.setAdListener(this);

        mEvent.setOnEventCompleteListener(new ClaimEvent.OnEventCompleteListener() {
            @Override
            public void onEndEventSuccess(End end) {
                PerkManager.getUnreadNotificationsCount(FacebookAdNetworkActivity.this);
                FacebookAdNetworkActivity.this.finish();
                Points.legacyUpdateUserPoints(FacebookAdNetworkActivity.this);

                if (mShowReturnNotification) {
                    Functions.showReturnNotfication(FacebookAdNetworkActivity.this, end, 0, false, "", "");
                }
            }

            @Override
            public void onEndEventNotModified() {
                //this was a tracked event for a standalone ad
                finish();
            }

            @Override
            public void onEndEventFailure(String message) {

                finish();
            }
        });
    }

    @Override
    public void onAdClicked(Ad arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAdLoaded(Ad arg0) {
        // Ad is loaded and ready to be displayed
        // You can now display the full screen ad using this code:
        PerkManager.notifyApp("AdStartedSuccessfully");
        interstitialAd.show();
        PerkManager.notifyApp("AdCompletedSuccessfully");
    }

    @Override
    public void onError(Ad arg0, AdError arg1) {
        if(mTrack == true) {
            PerkManager.notifyApp("AdServerWaterFallFail");
        }
        else {
            PerkManager.notifyApp("AdvertWaterfallFail");
        }
        finish();//todo:RESULT_NOT_OK (or some made up code)
    }

    @Override
    public void onInterstitialDismissed(com.facebook.ads.Ad arg0) {
        //todo:nope. AsyncTask doesn't belong here
//                new adEndPOST().execute();
        mEvent.end();
    }

    @Override
    public void onInterstitialDisplayed(Ad arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * nope.
     *
     * @deprecated removed in v3.1
     */
    public static void loadFANInterstitial() {
        throw new UnsupportedOperationException("Included for backwards compatibility");
    }

}
