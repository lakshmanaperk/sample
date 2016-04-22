package com.perk.perksdk.appsaholic.v1.ads;

import com.perk.perksdk.appsaholic.v1.common.RequestListener;

/**
 * <h1></h1>
 * todo:complete documentation
 */
public interface AdEndInterface extends RequestListener<Ad> {

    /**
     * Event fired on a successful request with no response
     */
    void notModified();
}
