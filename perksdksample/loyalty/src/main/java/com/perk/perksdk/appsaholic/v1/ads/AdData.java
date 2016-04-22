package com.perk.perksdk.appsaholic.v1.ads;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.common.Currency;

/**
 * child of {@link Ad}
 *
 * @see Ad
 * @see AdNotification
 * @see Currency
 */
public class AdData implements Parcelable {

    public static final Creator<AdData> CREATOR = new Creator<AdData>() {
        @Override
        public AdData createFromParcel(Parcel in) {
            return new AdData(in);
        }

        @Override
        public AdData[] newArray(int size) {
            return new AdData[size];
        }
    };

    public final AdNotification notification;
    public final int points;
    public final Currency currency;

    protected AdData(Parcel in) {
        notification = in.readParcelable(AdNotification.class.getClassLoader());
        points = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(notification, 0);
        dest.writeInt(points);
        dest.writeParcelable(currency, 0);
    }

    public int getPoints() {
        return points;
    }

    public String getCurrencyValue() {
        if (currency != null) {
            return currency.currency_awarded;
        }
        return "";
    }

    public String getCurrencyType() {
        if (currency != null) {
            return currency.currency_name;
        }
        return "";
    }

    public AdNotification getNotification() {
        return notification;
    }
}
