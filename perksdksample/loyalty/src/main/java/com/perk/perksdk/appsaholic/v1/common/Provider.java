package com.perk.perksdk.appsaholic.v1.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.claims.Claim;
import com.perk.perksdk.appsaholic.v1.claims.ClaimData;

import java.util.Arrays;

/**
 * child of {@link Claim}
 *
 * @see Claim
 * @see ClaimData
 */
public class Provider extends Object implements Parcelable {

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
    public final String partner;
    public final String tag;

    protected Provider(Parcel in) {
        partner = in.readString();
        tag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(partner);
        dest.writeString(tag);
    }

    public static Provider[] getProviders(Parcel in) {
        Parcelable[] providers = in.readParcelableArray(Provider.class.getClassLoader());
        return (providers != null)
                ? Arrays.copyOf(providers, providers.length, Provider[].class)
                : Provider.CREATOR.newArray(0);
    }
}
