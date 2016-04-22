package com.perk.perksdk.appsaholic.v1.unclaimed;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.AppsaholicRequest;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

import org.apache.http.client.methods.HttpGet;

/**
 * <h1>Unclaimed</h1>
 */
public class Unclaimed extends AppsaholicRequest implements Parcelable {

    public static final Creator<Unclaimed> CREATOR = new Creator<Unclaimed>() {
        @Override
        public Unclaimed createFromParcel(Parcel in) {
            return new Unclaimed(in);
        }

        @Override
        public Unclaimed[] newArray(int size) {
            return new Unclaimed[size];
        }
    };
    /**
     * {
     * "status": "success",
     * "message": null,
     * "data": {
     * "unclaimed_events": 16
     * }
     * }
     */

    public final String status;
    public final String message;
    public final UnclaimedData data;

    protected Unclaimed(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(UnclaimedData.class.getClassLoader());
    }

    public static void get(final Context context, final RequestListener<Unclaimed> listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                String url = Constants.unclaimed
                        + Constants.API_KEY + "=" + PerkManager.getConfig().getApiKey()
                        + "&" + Constants.APPSAHOLIC_ID + "=" + PerkManager.getConfig().getAppsaholicId();

                Ion.with(context)
                        .load(HttpGet.METHOD_NAME, url)
                        .setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                        .setHeader(Constants.DEVICE_INFO, PerkManager.getDeviceInfo())
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null) {
                                    listener.failure(e.getMessage());
                                }
                                else {
                                    Gson gson = new Gson();
                                    listener.success(gson.fromJson(result, Unclaimed.class));
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Unclaimed.class,false);
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
}
