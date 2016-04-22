package com.perk.perksdk.appsaholic.v1.Country;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by lakshmana on 09/03/16.
 */
public class CountryData implements Parcelable {
    final String[]  countries;

    protected CountryData(Parcel in) {
        countries = in.createStringArray();
    }

    public static final Parcelable.Creator<CountryData> CREATOR = new Parcelable.Creator<CountryData>() {
        @Override
        public CountryData createFromParcel(Parcel in) {
            return new CountryData(in);
        }

        @Override
        public CountryData[] newArray(int size) {
            return new CountryData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(countries);
    }
}
