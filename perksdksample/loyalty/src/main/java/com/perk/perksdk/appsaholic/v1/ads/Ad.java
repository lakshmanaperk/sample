package com.perk.perksdk.appsaholic.v1.ads;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.perk.perksdk.AdsActivity;
import com.perk.perksdk.PerkConfig;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.AppsaholicRequest;
import com.perk.perksdk.appsaholic.v1.common.Currency;
import com.perk.perksdk.appsaholic.v1.common.NotificationRequest;

import java.net.HttpURLConnection;

/**
 * <h1>Ad</h1>
 * native representation of ad data we expect to receive from the ads endpoint.
 * <a href="http://docs.appsaholicsdk.apiary.io/#reference/ads/ads/ad-end">
 * (Documentation)
 * </a>
 *
 * @see AdData
 * @see AdNotification
 * @see Currency
 */
public class Ad extends AppsaholicRequest implements Parcelable, NotificationRequest {

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    public final String status;
    public final String message;
    public final AdData data;

    protected Ad(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(AdData.class.getClassLoader());
    }

    /**
     * Request ad information from the api
     *
     * @param context  context requesting the ad info
     * @param eventId  (optional) tracked event ID for this request
     * @param adSource
     */
    public static void get(final Context context, @NonNull final String eventId,
                           final @NonNull String adSource, final boolean adSupport, final boolean fromWatchMore, final AdEndInterface listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                PerkConfig config = PerkManager.getConfig();
                JsonObject json = new JsonObject();

                json.addProperty(Constants.API_KEY, config.getApiKey());
                json.addProperty(Constants.APPSAHOLIC_ID, config.getAppsaholicId());
                if (adSource != null) {
                    json.addProperty(Constants.AD_SOURCE, adSource);
                }
                if (eventId != null && !eventId.isEmpty()) {
                    if (!fromWatchMore) {
                        if (!AdsActivity.ispassivestarted) {
                            json.addProperty(Constants.EVENT_ID, eventId);
                        }
                        else if (!adSupport) {
                            json.addProperty(Constants.EVENT_ID, eventId);
                        }
                    }
                }

                Ion.with(context)
                        .load(Constants.ads)
                        .setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                        .setHeader(Constants.DEVICE_INFO, PerkManager.getDeviceInfo())
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                if (listener == null) {
                                    return;
                                }
                                switch (result.getHeaders().code()) {
                                    case HttpURLConnection.HTTP_OK: {
                                        // this should never happen
                                        Gson gson = new Gson();
                                        listener.success(gson.fromJson(result.getResult(), Ad.class));
                                        if(adSupport == false) {
                                            PerkManager.sendNotification(PerkManager.PERK_MANAGER_SDK_POINTS_AWARDED_WITHOUT_AD);
                                            PerkManager.notifyApp(PerkManager.PERK_MANAGER_SDK_POINTS_AWARDED_WITHOUT_AD);
                                        }
                                        if (PerkManager.getConfig().isPassivePlayEnabled()) {
                                            AdsActivity.ispassivestarted = true;
                                        }
                                    }
                                    break;
                                    case HttpURLConnection.HTTP_NOT_MODIFIED: {
                                        String message = String.format(
                                                "Event ended successfully without response:\n" +
                                                        "placementId: \"%s\"", eventId);
                                        PerkManager.notifyApp(message);
                                        listener.notModified();
                                    }
                                    break;
                                    default: {
                                        listener.failure(result.getResult().get("message").getAsString());
                                    }
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Ad.class,false);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
        dest.writeParcelable(data, flags);
    }

    @Override
    public int getPoints() {
        return data.points;
    }

    @Override
    public String getStub() {
        return data.notification.stub;
    }

    @Override
    public String getText() {
        return data.notification.text;
    }
}
