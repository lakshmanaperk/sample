package com.perk.perksdk.appsaholic.v1.init;

import com.perk.perksdk.appsaholic.v1.common.Provider;

/**
 * Created by victorude on 2/3/16.
 */
public interface InitInterface {

    boolean isSdkEnabled();

    String getAppsaholicUserID();

    boolean isPassivePlayEnabled();

    int getPassiveCountdownSeconds();

    String getAppsaholicId();

    String getAdTag();

    Provider[] getVideoAdTags();

    Provider[] getNotificationAdTags();

    Provider[] getSurveyAdTags();

    Provider[] getDisplayAdTags();

    void setSdkStatus(boolean sdkStatus);
}
