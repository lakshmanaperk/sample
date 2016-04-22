package com.perk.perksdk.appsaholic.app;

/**
 * Created by lakshmana on 11/03/16.
 */
public interface PerkCBInterface {

    void adStartedWithSuccess();

    void adCompletedWithSuccess();

    void adClosedWithoutCompletion();
    
    void adServerWaterFallStarted();

    void adServerWaterFallFail();

    void adServerCompleted(int maxads);

}
