package com.perk.perksdk.appsaholic.v1.publisher;

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

/**
 * Created by lakshmana on 04/03/16.
 */
public class Publisher extends AppsaholicRequest implements Parcelable {

    public static final Creator<Publisher> CREATOR = new Creator<Publisher>() {
        @Override
        public Publisher createFromParcel(Parcel in) {
            return new Publisher(in);
        }

        @Override
        public Publisher[] newArray(int size) {
            return new Publisher[size];
        }
    };
    /**
     * {
     * "status": "success",
     * "message": null,
     * "data": {
     * "available_perks": 676
     * }
     * }
     */

    final String status;
    final String message;
    final PublisherData data;

    protected Publisher(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(PublisherData.class.getClassLoader());
    }

    public static void get(final Context context, final RequestListener<Publisher> listener) {
        Runnable request = new Runnable() {
            @Override
            public void run() {
                Ion.with(context)
                        .load(Constants.pubisher.replace(Constants.API_KEY,
                                PerkManager.getConfig().getApiKey()))
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
                                    listener.success(gson.fromJson(result, Publisher.class));
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Publisher.class,false);
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

    public int getPoints() {
        if (data != null) {
            return data.balance;
        }
        return 0;
    }
}
