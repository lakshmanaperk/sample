package com.perk.perksdk;

public class WebServiceResponse {
    public String responseString;
    public int responseStatusCode;

    public WebServiceResponse(String responseString, int responseStatusCode) {
        this.responseString = responseString;
        this.responseStatusCode = responseStatusCode;
    }
}
