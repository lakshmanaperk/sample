package com.perk.perksdk.appsaholic.app;

import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.PerkUserInfo;

/**
 * <h1>PerkAppInterface</h1>
 * Appsaholic app interface required for initialization of {@link PerkManager}
 *
 * @see PerkManager
 */
public interface PerkAppInterface {

    /**
     * When PerkManager.startSession() called from App, SDK initialization status will be given through InitializeSDK callback
     * @param statusCode  SDK status
     * @param status     SDK init with Reason
     */
    void onInit(boolean statusCode, String status);

    /**
     * When PerkManager.getUnreadNotificationCount called from App, count will be given through FetchNotificationSuccess callback
     * @param stausCode  UnreadNotification API response code
     * @param unreadNotification     number of notitifications pending
     */

    void onNotifiationsCount(boolean stausCode, int unreadNotification);

    /**
     * When PerkManager.toggleSDKStatus called from App, status will be given back through SDKStatusChange callback
     * @param stausCode  SDK status API response code
     * @param sdkStatus     SDK status
     */
    void onSdkStatus(boolean stausCode, boolean sdkStatus);

    /**
     * When PerkManager.getUserInfo called from App, status will be given back through  UserInformation callback
     * @param stausCode  SDK status API response code
     * @param userInfo     PerkUserInfo object that gives userinfo
     */

    void onUserInformation(boolean stausCode,  PerkUserInfo userInfo);

    /**
     * When PerkManager.getCountriesList called from App, list will be given back through CountryListInfo callback
     * @param countryList  available countries as a string
     *
     */
    void onCountryList(boolean statusCode,String countryList);

    /**
     * When PerkManager.getPublisherAvailablePoints called from App, count will be given through FetchNotificationSuccess callback
     * @param stausCode  publisher balance API response code
     * @param prepaidPoints     publisher balance
     */
    void onPublisherBalance(boolean stausCode, int prepaidPoints);

    /**
     * All generic Perk Events given in this call back
     * @param reason Generic Perk Event
     */

    void onPerkEvent(String reason);

}
