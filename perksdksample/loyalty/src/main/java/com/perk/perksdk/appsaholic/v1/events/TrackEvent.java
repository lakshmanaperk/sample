package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.os.Parcel;

import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.common.RequestListener;
import com.perk.perksdk.appsaholic.v1.end.End;
import com.perk.perksdk.appsaholic.v1.track.Track;

import java.util.ArrayList;

/**
 * <h1></h1>
 * todo:complete documentation
 */
public class TrackEvent extends AppsaholicEvent {

    private Track mTrack;

    public TrackEvent(Context context, String eventId) {
        setContext(context);
        setId(eventId);
    }

    public TrackEvent(Parcel in) {
        setId(in.readString());
        setAdSource(in.readString());
    }

    public void track() {
        Track.get(getContext(), getId(), new RequestListener<Track>() {
            @Override
            public void success(Track track) {
                TrackEvent.this.mTrack = track;
                if (successAction != null) {
                    successAction.run();
                }
            }

            @Override
            public void failure(String message) {
                PerkManager.notifyApp("Failed to track event with id: \"" + getId() + "\"");
                if (failureAction != null) {
                    failureAction.run();
                }
            }
        });
    }

    @Override
    public void start() {
        track();
    }

    /**
     * Call to end a track event on successful completion of an ad
     */
    @Override
    public void end() {
        End.get(getContext(), getId(), getAdSource(), getId(), new RequestListener<End>() {

            @Override
            public void success(End end) {
                if (eventCompleteListener != null) {
                    eventCompleteListener.onEndEventSuccess(end);
                }
            }

            @Override
            public void failure(String message) {
                PerkManager.notifyApp("Event failed: " + getId());
                if (eventCompleteListener != null) {
                    eventCompleteListener.onEndEventFailure(message);
                }
            }
        });
    }

    public ArrayList<Provider> getVideoWaterfall() {
        return mTrack.getVideoWaterfall();
    }

    public ArrayList<Provider> getDisplayWaterfall() {
        return mTrack.getDisplayWaterfall();
    }

    public ArrayList<Provider> getNotificationWaterfall() {
        return mTrack.getNotificationWaterfall();
    }

    public ArrayList<Provider> getLoadAdWaterfall() {
        return mTrack.getLoadAdWaterfall();
    }

    public ArrayList<Provider> getSurveyWaterfall() {
        return mTrack.getSurveyWaterfall();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getAdSource());
    }

    public static final Creator<TrackEvent> CREATOR = new Creator<TrackEvent>() {
        @Override
        public TrackEvent createFromParcel(Parcel in) {
            return new TrackEvent(in);
        }

        @Override
        public TrackEvent[] newArray(int size) {
            return new TrackEvent[size];
        }
    };
}
