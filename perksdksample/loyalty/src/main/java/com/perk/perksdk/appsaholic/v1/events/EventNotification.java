package com.perk.perksdk.appsaholic.v1.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>EventNotification</h1>
 */
public class EventNotification implements Parcelable {

    public final String text;
    public final String stub;
    public final int type;

    protected EventNotification(Parcel in) {
        text = in.readString();
        stub = in.readString();
        type = in.readInt();
    }

    public static final Creator<EventNotification> CREATOR = new Creator<EventNotification>() {
        @Override
        public EventNotification createFromParcel(Parcel in) {
            return new EventNotification(in);
        }

        @Override
        public EventNotification[] newArray(int size) {
            return new EventNotification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(stub);
        dest.writeInt(type);
    }
}
