package com.perk.perksdk.appsaholic.v1.claims;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.AppsaholicRequest;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <h1>Claim</h1>
 * native representation of claim data we expect to receive from the claims endpoint.
 * <a href="http://docs.appsaholicsdk.apiary.io/#reference/events/claim-events/claim-events">
 * (Documentation)
 * </a>
 *
 * @see Claim
 * @see ClaimData
 * @see Provider
 */
public class Claim extends AppsaholicRequest implements Parcelable {

    public static final Creator<Claim> CREATOR = new Creator<Claim>() {
        @Override
        public Claim createFromParcel(Parcel in) {
            return new Claim(in);
        }

        @Override
        public Claim[] newArray(int size) {
            return new Claim[size];
        }
    };

    public final String status;
    public final String message;
    public final ClaimData data;

    protected Claim(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(ClaimData.class.getClassLoader());
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

    public static void get(final Context context, @NonNull final String eventId,
                           final RequestListener<Claim> listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                JsonObject json = new JsonObject();
                json.addProperty(Constants.API_KEY, PerkManager.getConfig().getApiKey());
                json.addProperty(Constants.APPSAHOLIC_ID, PerkManager.getConfig().getAppsaholicId());
                json.addProperty(Constants.EVENT_ID, eventId);

                Ion.with(context)
                        .load(Constants.claims)
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
                                        listener.success(gson.fromJson(result.getResult(), Claim.class));
                                    }
                                    break;
                                    default: {
                                        listener.failure(result.getResult().get("message").getAsString());
                                    }
                                    break;
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Claim.class,false);
    }

    public ArrayList<Provider> getWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(data.waterfall));
        return waterfall;
    }

    public boolean isAdSpported() {
        return data.ad_supported;
    }
}
