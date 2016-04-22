package com.perk.perksdk.appsaholic.v1.events;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.common.Currency;
import com.perk.perksdk.appsaholic.v1.common.Provider;

/**
 * <h1>EventData</h1>
 */
public class EventData implements Parcelable {

    /**
     * "data": {
     * "notification": {
     * "text": "You just earned 1 point for tapping once.",
     * "stub": "for tapping once.",
     * "type": 1
     * },
     * "waterfall": [{
     * "partner": "BRL",
     * "tag": "3856549"
     * }, {
     * "partner": "TRMR",
     * "tag": "test"
     * }, {
     * "partner": "TRMR",
     * "tag": "test"
     * }],
     * "ad_supported": true,
     * "points": 1,
     * "event_id": 8511635,
     * "currency": {
     * "currency_name": "coins",
     * "currency_awarded": 100
     * }
     * }
     */

    public final EventNotification notification;
    public final Provider[] waterfall;
    public final boolean ad_supported;
    public final int points;
    public final String event_id;
    public final Currency currency;

    protected EventData(Parcel in) {
        notification = in.readParcelable(EventNotification.class.getClassLoader());
        waterfall = Provider.getProviders(in);
        ad_supported = in.readByte() != 0;
        points = in.readInt();
        event_id = in.readString();
        currency = in.readParcelable(Currency.class.getClassLoader());
    }

    public static final Creator<EventData> CREATOR = new Creator<EventData>() {
        @Override
        public EventData createFromParcel(Parcel in) {
            return new EventData(in);
        }

        @Override
        public EventData[] newArray(int size) {
            return new EventData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(notification, 0);
        dest.writeParcelableArray(waterfall, 0);
        dest.writeByte((byte) (ad_supported ? 1 : 0));
        dest.writeInt(points);
        dest.writeString(event_id);
        dest.writeParcelable(currency, 0);
    }
}
