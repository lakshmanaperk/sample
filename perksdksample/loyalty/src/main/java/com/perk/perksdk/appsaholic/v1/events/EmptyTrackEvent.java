package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1></h1>
 * todo:complete documentation
 */
public class EmptyTrackEvent extends TrackEvent implements Parcelable {
    public EmptyTrackEvent(Context context, String id) {
        super(context, id);
    }

    protected EmptyTrackEvent(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EmptyTrackEvent> CREATOR = new Creator<EmptyTrackEvent>() {
        @Override
        public EmptyTrackEvent createFromParcel(Parcel in) {
            return new EmptyTrackEvent(in);
        }

        @Override
        public EmptyTrackEvent[] newArray(int size) {
            return new EmptyTrackEvent[size];
        }
    };
}
