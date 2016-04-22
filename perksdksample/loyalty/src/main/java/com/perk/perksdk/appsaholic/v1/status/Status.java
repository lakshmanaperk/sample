package com.perk.perksdk.appsaholic.v1.status;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

/**
 * <h1>Status</h1>
 */
public class Status implements Parcelable {

    public final String status;
    public final String message;
    public final StatusData data;

    protected Status(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
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

    public static void toggle(Context context, @Nullable final RequestListener<Status> listener) {

        JsonObject json = new JsonObject();
        json.addProperty(Constants.API_KEY, PerkManager.getConfig().getApiKey());
        json.addProperty(Constants.APPSAHOLIC_ID, PerkManager.getConfig().getAppsaholicId());

        Ion.with(context)
                .load(Constants.status)
                .setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .setHeader(Constants.DEVICE_INFO, PerkManager.getDeviceInfo())
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (listener == null) {
                            return;
                        }

                        if (e != null) {
                            listener.failure(e.getMessage());
                        }
                        else {
                            Gson gson = new Gson();
                            listener.success(gson.fromJson(result, Status.class));
                        }
                    }
                });
    }
}
