package com.perk.perksdk.appsaholic.v1.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.ads.Ad;
import com.perk.perksdk.appsaholic.v1.ads.AdData;
import com.perk.perksdk.appsaholic.v1.ads.AdNotification;

/**
 * child of {@link AdData}
 *
 * @see Ad
 * @see AdData
 * @see AdNotification
 */
public class Currency implements Parcelable {

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    public final String currency_name;
    public final String currency_awarded;

    protected Currency(Parcel in) {
        currency_name = in.readString();
        currency_awarded = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currency_name);
        dest.writeString(currency_awarded);
    }
}
