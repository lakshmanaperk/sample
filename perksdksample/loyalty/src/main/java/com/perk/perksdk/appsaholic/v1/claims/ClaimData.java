package com.perk.perksdk.appsaholic.v1.claims;

import android.os.Parcel;
import android.os.Parcelable;

import com.perk.perksdk.appsaholic.v1.common.Provider;

import java.util.Arrays;

/**
 * child of {@link Claim}
 *
 * @see Claim
 * @see Provider
 */
public class ClaimData implements Parcelable {

    public static final Creator<ClaimData> CREATOR = new Creator<ClaimData>() {
        @Override
        public ClaimData createFromParcel(Parcel in) {
            return new ClaimData(in);
        }

        @Override
        public ClaimData[] newArray(int size) {
            return new ClaimData[size];
        }
    };
    public final String status;
    public final Provider[] waterfall;
    public final boolean ad_supported;

    protected ClaimData(Parcel in) {
        status = in.readString();
        Parcelable[] providers = in.readParcelableArray(Provider.class.getClassLoader());
        waterfall = (providers != null)
                ? Arrays.copyOf(providers, providers.length, Provider[].class)
                : Provider.CREATOR.newArray(0);
        ad_supported = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeParcelableArray(waterfall, 0);
        dest.writeByte((byte) (ad_supported ? 1 : 0));
    }
}
