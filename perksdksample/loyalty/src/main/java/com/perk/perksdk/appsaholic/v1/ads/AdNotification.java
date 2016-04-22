package com.perk.perksdk.appsaholic.v1.ads;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.common.Currency;

/**
 * child of {@link AdData}
 *
 * @see Ad
 * @see AdData
 * @see Currency
 */
public class AdNotification implements Parcelable {

    public static final Creator<AdNotification> CREATOR = new Creator<AdNotification>() {
        @Override
        public AdNotification createFromParcel(Parcel in) {
            return new AdNotification(in);
        }

        @Override
        public AdNotification[] newArray(int size) {
            return new AdNotification[size];
        }
    };

    public final String text;
    public final String stub;

    protected AdNotification(Parcel in) {
        text = in.readString();
        stub = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(stub);
    }

    public String getText() {
        return text;
    }

    public String getStub() {
        return stub;
    }
}
