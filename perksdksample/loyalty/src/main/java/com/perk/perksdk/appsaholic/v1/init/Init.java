package com.perk.perksdk.appsaholic.v1.init;

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
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;

/**
 * <h1></h1>
 */
public class Init implements Parcelable, InitInterface {

    /**
     * We should never see this value being used unless someone attempted to call
     * {@link #getPassiveCountdownSeconds()} without properly connecting to the API.
     */
    static final int DEFAULT_PASSIVE_PLAY_SECONDS = 17;

    final String status;
    final String message;
    final InitData data;

    protected Init(Parcel in) {
        status = in.readString();
        message = in.readString();
        data = in.readParcelable(InitData.class.getClassLoader());
    }

    public static final Creator<Init> CREATOR = new Creator<Init>() {
        @Override
        public Init createFromParcel(Parcel in) {
            return new Init(in);
        }

        @Override
        public Init[] newArray(int size) {
            return new Init[size];
        }
    };

    public static void get(Context context, @Nullable final RequestListener<Init> listener) {

        JsonObject json = new JsonObject();
        json.addProperty(Constants.API_KEY, PerkManager.getAppKey());

        Ion.with(context)
                .load(Constants.init)
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
                            listener.success(gson.fromJson(result, Init.class));
                        }
                    }
                });
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

    @Override
    public boolean isSdkEnabled() {
        return (data != null) && data.sdk_status;
    }

    @Override
    public String getAppsaholicUserID() {
        return (data != null) ? data.user_id : "";
    }

    @Override
    public boolean isPassivePlayEnabled() {
        return (data != null) && data.passive;
    }

    @Override
    public int getPassiveCountdownSeconds() {
        return (data != null) ? data.passive_countdown : DEFAULT_PASSIVE_PLAY_SECONDS;
    }

    @Override
    public String getAppsaholicId() {
        return (data != null) ? data.appsaholic_id : "";
    }

    @Override
    public String getAdTag() {
        return (data != null) ? data.ad_tags.AER : "";
    }

    @Override
    public Provider[] getVideoAdTags() {
        return (data != null) ? data.video_ad_tags : new Provider[0];
    }

    @Override
    public Provider[] getNotificationAdTags() {
        return (data != null) ? data.notification_ad_tags : new Provider[0];
    }

    @Override
    public Provider[] getSurveyAdTags() {
        return (data != null) ? data.survey_ad_tags : new Provider[0];
    }

    @Override
    public Provider[] getDisplayAdTags() {
        return (data != null) ? data.display_ad_tags : new Provider[0];
    }

    @Override
    public void setSdkStatus(boolean sdkStatus) {
        if (data != null) {
            data.sdk_status = sdkStatus;
        }
    }
}
