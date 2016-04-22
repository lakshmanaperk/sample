package com.perk.perksdk.appsaholic.v1.points;

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
 * <h1>Points</h1>
 */
public class Points extends AppsaholicRequest implements Parcelable {

    public static final Creator<Points> CREATOR = new Creator<Points>() {
        @Override
        public Points createFromParcel(Parcel in) {
            return new Points(in);
        }

        @Override
        public Points[] newArray(int size) {
            return new Points[size];
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

    public final PointsData data;

    protected Points(Parcel in) {
        data = in.readParcelable(Points.class.getClassLoader());
    }

    public static void get(final Context context, final RequestListener<Points> listener) {
        Runnable request = new Runnable() {
            @Override
            public void run() {
                Ion.with(context)
                        .load(Constants.points.replace(Constants.APPSAHOLIC_ID,
                                PerkManager.getConfig().getAppsaholicId()))
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
                                    listener.success(gson.fromJson(result, Points.class));
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Points.class,false);
    }

    /**
     * @param context the application context
     * @return false if the user is not logged in OR true if the user is logged in.
     * @deprecated Do not use this function. Backwards compatibility hack.
     * Support function for updating the Utils class m_objEditor global.
     */
    public static boolean legacyUpdateUserPoints(final Context context) {
        if (!PerkManager.getConfig().isUserLoggedIn(context)) {
            return false;
        }

        Points.get(context, new RequestListener<Points>() {
            @Override
            public void success(Points points) {
                PerkManager.setPerkAvailablePoints(context, points.getPoints());
            }

            @Override
            public void failure(String message) {

            }
        });
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public int getPoints() {
        return data.available_perks;
    }
}
