package com.perk.perksdk.appsaholic.v1.id;

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
 * <h1>Id</h1>
 */
public class Id extends AppsaholicRequest implements Parcelable {

    public static final Creator<Id> CREATOR = new Creator<Id>() {
        @Override
        public Id createFromParcel(Parcel in) {
            return new Id(in);
        }

        @Override
        public Id[] newArray(int size) {
            return new Id[size];
        }
    };
    /**
     {
        "status": "success",
        "message": null,
        "data": {
            "user": {
                "id": "26df80db-b08c-48d2-b4ea-ec7e2e112ce0",
                "email": "victoru@fullsail.edu",
                "first_name": "Kalu",
                "last_name": "Ude",
                "available_points": 676,
                "pending_points": 0
            }
        }
    }
     */

    final String status;
    final String message;
    final IdData data;

    protected Id(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(IdData.class.getClassLoader());
    }

    public static void get(final Context context, final RequestListener<Id> listener) {
        Runnable request = new Runnable() {
            @Override
            public void run() {
                Ion.with(context)
                        .load(Constants.appsaholic_id.replace(Constants.APPSAHOLIC_ID,
                                PerkManager.getUserAccessToken(context)))
                        .setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                        .setHeader(Constants.DEVICE_INFO, PerkManager.getDeviceInfo())
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null) {
                                    listener.failure(e.getMessage());
                                } else {
                                    Gson gson = new Gson();
                                    listener.success(gson.fromJson(result, Id.class));
                                }
                            }
                        });
            }
        };

        executeRequest(request, Id.class,false);
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

    public String getFirstName() {
        return data.user.first_name;
    }

    public String getLastName() {
        return data.user.last_name;
    }

    public String getId() {
        return data.user.id;
    }

    public String getEmail() {
        return data.user.email;
    }

    public int getAvailablePoints() {
        return data.user.available_points;
    }

    public int getPendingPoints() {
        return data.user.pending_points;
    }

    public String  getProfileImage() {
        return data.user.profile_image;
    }

}
