package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.perk.perksdk.Functions;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.ads.Ad;
import com.perk.perksdk.appsaholic.v1.ads.AdEndInterface;
import com.perk.perksdk.appsaholic.v1.claims.Claim;
import com.perk.perksdk.appsaholic.v1.common.NotificationRequest;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;
import com.perk.perksdk.appsaholic.v1.points.Points;

import java.util.ArrayList;

/**
 * <h1>ClaimEvent</h1>
 * Track an Appsaholic Event and contact the correct 'event end' endpoint as needed.
 * Use {@link AppsaholicEventManager#getClaimEvent(Context, String)} to get an ClaimEvent
 * instance.
 *
 * <pre>
 * Note: any action involving an ClaimEvent should use {@link AppsaholicEvent#setSuccessAction(Runnable)}
 * to ensure that the event is properly initialized beforehand.
 * </pre>
 */
public class ClaimEvent extends AppsaholicEvent {

    private Claim mClaim;
    private int mPointsEarned = 0;
    public   String mCurrencyEarned = "0";
    public String mCurrencyType = "";
    public int adpoints = 0;
    public ClaimEvent(Parcel in) {
        setId(in.readString());
        setAdSource(in.readString());
    }

    public boolean isAdSupported() { return mClaim.isAdSpported(); }
    public ArrayList<Provider> getWaterfall() {
        return mClaim.getWaterfall();
    }

    public void claim() {
        Claim.get(getContext(), getId(), new RequestListener<Claim>() {
            @Override
            public void success(Claim claim) {
                ClaimEvent.this.mClaim = claim;
                if (successAction != null) {
                    successAction.run();
                }
            }

            @Override
            public void failure(String message) {
                PerkManager.notifyApp("Failed to claim event with id: \"" + getId() + "\"");
                if (failureAction != null) {
                    failureAction.run();
                }
            }
        });
    }

    public static Context getContext()
    {
        return  AppsaholicEvent.getContext();
    }

    @Override
    public void start() {
        claim();
    }

    /**
     * Call to end a claim event on successful completion of an ad
     */
    @Override
    public void end() {
        boolean isAdSupport = true;
        if(mClaim != null)
            isAdSupport = mClaim.isAdSpported();
        Ad.get(getContext(), getId(), getAdSource(), isAdSupport,false,new AdEndInterface() {
            @Override
            public void notModified() {

            }

            @Override
            public void success(Ad ad) {
                PerkManager.notifyApp("Event sucess: " + getId());
                Points.legacyUpdateUserPoints(getContext());
                mPointsEarned += ad.getPoints();
                adpoints = ad.getPoints();
                if(ad.data != null && ad.data.getCurrencyValue() != null && ad.data.getCurrencyValue().length() > 0)
                 mCurrencyEarned = Integer.toString(Integer.parseInt(mCurrencyEarned) + Integer.parseInt(ad.data.getCurrencyValue()));
                mCurrencyType = ad.data.getCurrencyType();
                if(mCurrencyType != null && mCurrencyType.length() > 0 && mCurrencyEarned != null && mCurrencyEarned.length() > 0)
                    Functions.showReturnNotfication(getContext(), (NotificationRequest) ad, mPointsEarned,false,mCurrencyEarned,mCurrencyType);
                else
                    Functions.showReturnNotfication(getContext(), (NotificationRequest) ad, mPointsEarned,false,"","");
                if (eventCompleteListener != null) {
                    eventCompleteListener.onEndEventNotModified();
                }
            }


            @Override
            public void failure(String message) {
                PerkManager.notifyApp("Event failed: " + getId());
                if (eventCompleteListener != null) {
                    eventCompleteListener.onEndEventFailure(message);
                }
            }
        });
    }

    //can't touch this
    ClaimEvent(@NonNull final Context context, String eventId) {
        setContext(context);
        setId(eventId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getAdSource());
    }

    public static final Creator<ClaimEvent> CREATOR = new Creator<ClaimEvent>() {
        @Override
        public ClaimEvent createFromParcel(Parcel in) {
            return new ClaimEvent(in);
        }

        @Override
        public ClaimEvent[] newArray(int size) {
            return new ClaimEvent[size];
        }
    };
}
