package com.perk.perksdk.appsaholic.v1.Country;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.perk.perksdk.Utils;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.AppsaholicRequest;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

/**
 * Created by lakshmana on 04/03/16.
 */
public class Country extends AppsaholicRequest implements Parcelable {

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
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
    final CountryData data;

    protected Country(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(CountryData.class.getClassLoader());
    }

    public static void get(final Context context, final RequestListener<Country> listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                Ion.with(context)
                        .load(Constants.whitelisted + Constants.API_KEY + "=" + PerkManager.getConfig().getApiKey())
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
                                    listener.success(gson.fromJson(result, Country.class));
                                }
                            }
                        });
            }
        };

        AppsaholicRequest.executeRequest(request, Country.class,false);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(message);
        dest.writeParcelable(data, 0);
    }

    public String getCountries() {
        if (data != null) {
            Utils.m_Countries =  new String[data.countries.length];
            String info = "";
            for (int i = 0; i < data.countries.length; i++) {
                info +=  data.countries[i] + ",";
                Utils.m_Countries[i] = data.countries[i];
            }
            return info;
        }
        return "";
    }
}
