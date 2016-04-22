package com.perk.perksdk.appsaholic.v1.init;

import com.perk.perksdk.appsaholic.v1.common.Provider;

/**
 * <h1>DummyInit</h1>
 * <p/>
 * Passthrough for Init object related functions
 */
//todo:notify app every time they touch this class that they need to initialize
public class DummyInit implements InitInterface {

    @Override
    public boolean isSdkEnabled() {
        return false;
    }

    @Override
    public String getAppsaholicUserID() {
        return "";
    }

    @Override
    public boolean isPassivePlayEnabled() {
        return false;
    }

    @Override
    public int getPassiveCountdownSeconds() {
        return 0;
    }

    @Override
    public String getAppsaholicId() {
        return "";
    }

    @Override
    public String getAdTag() {
        return "";
    }

    @Override
    public Provider[] getVideoAdTags() {
        return new Provider[0];
    }

    @Override
    public Provider[] getNotificationAdTags() {
        return new Provider[0];
    }

    @Override
    public Provider[] getSurveyAdTags() {
        return new Provider[0];
    }

    @Override
    public Provider[] getDisplayAdTags() {
        return new Provider[0];
    }

    @Override
    public void setSdkStatus(boolean sdkStatus) {
        return;
    }
}
