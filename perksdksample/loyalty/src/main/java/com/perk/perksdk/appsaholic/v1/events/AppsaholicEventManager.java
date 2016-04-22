package com.perk.perksdk.appsaholic.v1.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.perk.perksdk.AdsActivity;
import com.perk.perksdk.FacebookAdNetworkActivity;
import com.perk.perksdk.Functions;
import com.perk.perksdk.Utils;
import com.perk.perksdk.appsaholic.PerkManager;
import com.perk.perksdk.appsaholic.v1.common.Provider;
import com.perk.perksdk.appsaholic.v1.end.End;
import com.perk.perksdk.appsaholic.v1.points.Points;

import java.util.ArrayList;

/**
 * <h1>AppsaholicEventManager</h1>
 */
public class AppsaholicEventManager {

    /**
     * Convenience class for managing the claim event lifecycle
     *
     * @param context the application context
     * @param eventId A valid string event id or null
     * @return a managed {@link ClaimEvent} or an {@link EmptyClaimEvent} if the event id is invalid
     */
    public static ClaimEvent getClaimEvent(Context context, String eventId) {
        return (AppsaholicEvent.eventHasValidId(eventId))
                ? new ClaimEvent(context, eventId)
                : new EmptyClaimEvent(context, null);
    }

    /**
     * Convenience class for managing the track event lifecycle
     *
     * @param context     the application context
     * @param placementId A valid string placement id or null
     * @return a managed {@link TrackEvent} or an {@link EmptyTrackEvent} if the event id is invalid
     */
    public static TrackEvent getTrackEvent(Context context, String eventId) {
        return (AppsaholicEvent.eventHasValidId(eventId))
                ? new TrackEvent(context, eventId)
                : new EmptyTrackEvent(context, null);
    }

    public static ClaimEvent getEmptyClaimEvent(Context context) {
        return getClaimEvent(context, null);
    }

    public static void loadAd(Context context, AppsaholicEvent event, ArrayList<Provider> waterfall,
                              boolean mCustomNotification, boolean showReturn, boolean fromPortal, boolean track, boolean fromwatchmore) {

        String eventId = event.getId();

        if (waterfall.size() <= 0) {
            Functions.showAlertFail(context, mCustomNotification, fromPortal, fromwatchmore);
        }

        for (Provider tag : waterfall) {
            event.setAdSource(tag.partner);
            Functions.logAdStart(tag);

            switch (tag.partner) {
                case "COM": {
                    Intent intent = new Intent(Utils.m_objContext, AdsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle args = new Bundle();
                    args.putParcelable(AdsActivity.ARG_EVENT, event);
                    args.putString(AdsActivity.ARG_EVENT_ID, eventId);
                    args.putBoolean(AdsActivity.ARG_CUSTOM_NOTIFICATION, mCustomNotification);
                    args.putString(AdsActivity.ARG_AD_SOURCE, tag.partner);
                    args.putBoolean(AdsActivity.ARG_FROM_PORTAL, fromPortal);
                    args.putBoolean(AdsActivity.ARG_SHOW_RETURN_NOTIFICATION, showReturn);
                    args.putBoolean(AdsActivity.ARG_TRACK, track);
                    args.putBoolean(AdsActivity.ARG_FROM_WATCHMORE, fromwatchmore);
                    intent.putExtras(args);
                    context.startActivity(intent);
                }
                return;
                case "FAN": {
                    Intent intent = new Intent(Utils.m_objContext, FacebookAdNetworkActivity.class);
                    Bundle args = new Bundle();
                    args.putParcelable(AdsActivity.ARG_EVENT, event);
                    args.putString(AdsActivity.ARG_EVENT_ID, eventId);
                    args.putString(AdsActivity.ARG_PLACEMENT_ID, tag.tag);
                    args.putBoolean(AdsActivity.ARG_CUSTOM_NOTIFICATION, mCustomNotification);
                    args.putString(AdsActivity.ARG_AD_SOURCE, event.getAdSource());
                    args.putBoolean(AdsActivity.ARG_SHOW_RETURN_NOTIFICATION, showReturn);
                    args.putBoolean(AdsActivity.ARG_TRACK, track);
                    args.putBoolean(AdsActivity.ARG_FROM_WATCHMORE, fromwatchmore);
                    intent.putExtras(args);
                    context.startActivity(intent);
                }
                return;
//                case "TRP": {
//                    Uri uri = Uri.parse(Constants.trailpay_single_offer + "?api_key=" + PerkManager.getAppKey());
//                    Intent intent = new Intent(PerkSDKActivity.VIEW, uri, Utils.m_objContext, PerkSDKActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    Bundle args = new Bundle();
//                    args.putBoolean(PerkSDKActivity.ARG_IS_EVENT_AD, false);
//                    intent.putExtras(args);
//                    Utils.m_objContext.startActivity(intent);
//                    PerkManager.notifyApp("Leaving application");
//                }
//                return;
//                case "PNV": {
//                    Uri uri = Uri.parse(Constants.pubnative_ads + "api_key=" + PerkManager.getAppKey() + "&advertising_id=" + Utils.m_strAdvertisingId);
//                    Intent intent = new Intent(PerkSDKActivity.VIEW, uri, Utils.m_objContext, PerkSDKActivity.class);
//                    Bundle bundle = new Bundle();
//                    intent.putExtra(Browser.EXTRA_HEADERS, bundle);
//                    bundle.putString(Constants.DEVICE_INFO, PerkManager.getDeviceInfo());
//                    context.startActivity(intent);
//                    PerkManager.notifyApp("Leaving application");
//                }
//                return;
//                case "TAP": {
//                    String tapSenseLocation = Constants.tapsense_ads + "api_key=" + PerkManager.getAppKey() + "&appsaholic_id=" + PerkManager.getAppsaholicUserID();
//                    tapSenseLocation = tapSenseLocation.replace("https", "http");//why???
//                    Uri uri = Uri.parse(tapSenseLocation);
//                    Intent intent = new Intent(PerkSDKActivity.VIEW, uri, Utils.m_objContext, PerkSDKActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Constants.DEVICE_INFO, PerkManager.getDeviceInfo());
//                    Locale.getDefault();
//                    bundle.putString(Constants.ACCEPT_LANGUAGE, Locale.getDefault().toString());
//                    bundle.putString(Constants.USER_AGENT, Utils.m_sUserAgent);
//                    intent.putExtra(Browser.EXTRA_HEADERS, bundle);
//                    Utils.m_objContext.startActivity(intent);
//                    PerkManager.notifyApp("Leaving application");
//                }
//                return;
                case "PP": {
                    End.get(context, eventId, null, null, null);// just reward the points
                    Points.legacyUpdateUserPoints(context);
                }
                return;
                default: {
                    PerkManager.notifyApp(
                            String.format("Unrecognized ad source: partner = %s, tag = %s",
                                    tag.partner, tag.tag));
                }
                break;
            }
        }
    }
}
