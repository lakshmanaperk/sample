package com.perk.perksdk.appsaholic.v1.end;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.perk.perksdk.PerkConfig;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.ads.AdData;
import com.perk.perksdk.appsaholic.v1.common.AppsaholicRequest;
import com.perk.perksdk.appsaholic.v1.common.NotificationRequest;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

import java.net.HttpURLConnection;

/**
 * <h1>End</h1>
 */
public class End extends AppsaholicRequest implements Parcelable, NotificationRequest {

    public final String status;
    public final String message;
    public final AdData data;

    protected End(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(AdData.class.getClassLoader());
    }

    public static final Creator<End> CREATOR = new Creator<End>() {
        @Override
        public End createFromParcel(Parcel in) {
            return new End(in);
        }

        @Override
        public End[] newArray(int size) {
            return new End[size];
        }
    };

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

    public static void get(final Context context, final String eventId, final String adSource, final String placementId,
                           final RequestListener<End> listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                PerkConfig config = PerkManager.getConfig();
                JsonObject json = new JsonObject();

                json.addProperty(Constants.API_KEY, config.getApiKey());
                json.addProperty(Constants.APPSAHOLIC_ID, config.getAppsaholicId());
                json.addProperty(Constants.AD_SOURCE, adSource);
                if (placementId != null && !placementId.isEmpty()) {
                    json.addProperty(Constants.PLACEMENT_ID, placementId);
                }
                else if (eventId != null && !eventId.isEmpty()) {
                    json.addProperty(Constants.EVENT_ID, eventId);
                }

                Ion.with(context)
                        .load(Constants.end)
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
                                        Gson gson = new Gson();
                                        listener.success(gson.fromJson(result.getResult(), End.class));
                                    }
                                    break;
                                    case HttpURLConnection.HTTP_NOT_MODIFIED: {
                                        String message = String.format(
                                                "Event ended successfully without response:\n" +
                                                        "placementId: \"%s\"", placementId);
                                        PerkManager.notifyApp(message);
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

        AppsaholicRequest.executeRequest(request, End.class,false);
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
