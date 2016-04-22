package com.perk.perksdk.appsaholic.v1.common;

/**
 * <h1>Request Listener</h1>
 * A generic Appsaholic API request listener guaranteed to return an initialized endpoint
 * result object or an error
 */
public interface RequestListener<T> {

    /**
     * Event fired on a successful request from the typed endpoint request method
     *
     * @param result a typed Appsaholic endpoint object class fully initialized
     */
    void success(T result);

    /**
     * Event fired on a failed request to the Appsaholic API
     *
     * @param message the error object
     */
    void failure(String message);
}
