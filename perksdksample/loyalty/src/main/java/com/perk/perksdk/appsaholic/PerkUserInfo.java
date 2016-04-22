package com.perk.perksdk.appsaholic;

/**
 * Created by lakshmana on 14/03/16.
 */
public class PerkUserInfo {
    String status;
    String message;
    PerkUserData data;

    /**
     * gets the user Id of current user on on UserInformation Callback
     * @return user Id
     */
    public String getUserId() {
        return data.user.id;
    }
    /**
     * gets the user Email of current user on UserInformation Callback
     * @return user Email
     */
    public String getUserEmail() {
        return data.user.email;
    }
    /**
     * gets the user First Name  of current user on UserInformation Callback
     * @return user First Name
     */
    public String getUserFirstName() {
        return data.user.first_name;
    }
    /**
     * gets the user Last Name  of current user on UserInformation Callback
     * @return user Last Name
     */
    public String getUserLastName() {
        return data.user.last_name;
    }

    /**
     * gets the user Profile Image Url of current user on UserInformation Callback
     * @return user Profile Image Url
     */

    public String getUserProfileImageUrl() {
        return data.user.profile_image;
    }
    /**
     * gets the user Available Points of current user on UserInformation Callback
     * @return user Available Points Url
     */
    public int getUserAvailablePoints() {
        return  data.user.available_points;
    }
    /**
     * gets the user Pending Points of current user on UserInformation Callback
     * @return user Pending Points Url
     */
    public int getUserPendingPoints() {
        return  data.user.pending_points;
    }

    public class PerkUserData {
        UserInfo user;
    }
    public class UserInfo {
        String id;
        String email;
        String first_name;
        String last_name;
        String profile_image;
        int available_points;
        int pending_points;
    }
}