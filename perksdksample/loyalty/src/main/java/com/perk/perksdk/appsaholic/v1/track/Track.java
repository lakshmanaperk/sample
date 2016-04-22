package com.perk.perksdk.appsaholic.v1.track;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
 * <h1>Track</h1>
 */
public class Track extends AppsaholicRequest implements Parcelable {

    public final String status;
    public final String message;
    public final TrackData data;
    private ArrayList<Provider> surveyWaterfall;

    protected Track(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(TrackData.class.getClassLoader());
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
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

    public static void get(final @NonNull Context context, final String placementId,
                           @Nullable final RequestListener<Track> listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                JsonObject json = new JsonObject();
                json.addProperty(Constants.API_KEY, PerkManager.getConfig().getApiKey());
                json.addProperty(Constants.APPSAHOLIC_ID, PerkManager.getConfig().getAppsaholicId());
                json.addProperty(Constants.PLACEMENT_ID, placementId);

                Ion.with(context)
                        .load(Constants.track)
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
                                        listener.success(gson.fromJson(result.getResult(), Track.class));
                                    }
                                    break;
                                    default: {
                                        listener.failure(e.getMessage());
                                    }
                                    break;
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Track.class,false);
    }

    public ArrayList<Provider> getVideoWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(data.video_ad_tags));
        return waterfall;
    }

    public ArrayList<Provider> getDisplayWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(data.display_ad_tags));
        return waterfall;
    }

    public ArrayList<Provider> getNotificationWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(data.notification_ad_tags));
        return waterfall;
    }

    public ArrayList<Provider> getLoadAdWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(data.load_ad_tags));
        return waterfall;
    }

    public ArrayList<Provider> getSurveyWaterfall() {
        ArrayList<Provider> waterfall = new ArrayList<>();
        waterfall.addAll(Arrays.asList(data.survey_ad_tags));
        return waterfall;
    }
}
