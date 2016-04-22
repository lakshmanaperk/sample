package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.perk.perksdk.appsaholic.Constants;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.AppsaholicRequest;
import com.perk.perksdk.appsaholic.v1.common.NotificationRequest;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

/**
 * <h1>Event</h1>
 */
public class Event extends AppsaholicRequest implements Parcelable, NotificationRequest {

    public final String status;
    public final String message;
    public final EventData data;

    protected Event(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(EventData.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
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

    public static void get(final Context context, @NonNull final String eventId,
                           final RequestListener<Event> listener) {

        Runnable request = new Runnable() {
            @Override
            public void run() {
                JsonObject json = new JsonObject();
                json.addProperty(Constants.APPSAHOLIC_ID, PerkManager.getConfig().getAppsaholicId());
                json.addProperty(Constants.EVENT_ID, eventId);
                json.addProperty(Constants.API_KEY, PerkManager.getAppKey());

                //todo:receive response future and handle response 304
                Ion.with(context)
                        .load(Constants.events)
                        .setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
                        .setHeader(Constants.DEVICE_INFO, PerkManager.getDeviceInfo())
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null) {
                                    listener.failure(e.getMessage());
                                }
                                else {
                                    Gson gson = new Gson();
                                    listener.success(gson.fromJson(result, Event.class));
                                }
                            }
                        });
            }
        };

        executeRequest(request, Event.class,true);
    }
}
