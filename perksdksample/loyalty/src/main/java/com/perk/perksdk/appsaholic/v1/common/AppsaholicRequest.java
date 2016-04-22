package com.perk.perksdk.appsaholic.v1.common;

import com.perk.perksdk.appsaholic.PerkManager;

/**
 * <h1>AppsaholicRequest</h1>
 * <p/>
 * Helper class used to safely execute requests to the Appsaholic API
 */
public abstract class AppsaholicRequest {

    /**
     * Request wrapper class used to check network status prior to request execution.
     *
     * @param request a Runnable containing network logic to be executed if the SDK is enabled.
     * @param caller  the Class of the AppsaholicRequest subclass (used for logging only)
     */
    protected static void executeRequest(Runnable request, Class caller,boolean isrequired) {
        if(isrequired == true)
        {
            if (!PerkManager.getConfig().isSdkInitialized() && ! PerkManager.GetSDKStatus()) {
                PerkManager.notifyApp(caller.getSimpleName()
                        + " attempted to call Appsaholic API but the SDK is disabled");
                return;
            }
        }
        request.run();
    }
}
