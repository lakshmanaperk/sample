package com.perk.perksdk;

import android.content.Context;
import android.support.annotation.NonNull;

import com.perk.perksdk.appsaholic.v1.claims.Claim;
import com.perk.perksdk.appsaholic.v1.common.Provider;

import java.util.ArrayList;

/**
 * <h1>PerkConfig</h1>
 */
public interface PerkConfig {
    boolean isUserLoggedIn(Context context);

    boolean isSdkInitialized();

    boolean isPassivePlayEnabled();

    int getPassiveCountdownSeconds();

    String getAppsaholicId();

    String getApiKey();

    String getAdTag();

    ArrayList<Provider> getClaimWaterfall(@NonNull Claim claim);

    ArrayList<Provider> getVideoWaterfall();

    ArrayList<Provider> getNotificationWaterfall();

    ArrayList<Provider> getSurveyWaterfall();

    boolean getSdkStatus();

    String getDeviceIp(Context context);

    ArrayList<Provider> getDisplayAdWaterfall();
}
