package com.perk.perksdk.appsaholic.v1;

public class Domains {
    //domamin constants
    public static final boolean isprod = true;
    public final static String domainName = isprod ? "http://api.perk.com" : "http://api-v1-dev.perk.com";
    public final static String search_domainName = isprod ? "https://perk.com" : "http://search-dev.perk.com";
    public final static String appsaholic_domainName = isprod ? "https://appsaholic.com" : "http://staging.appsaholic.com";
    public final static String raw_appsaholic_domainName = isprod ? "appsaholic.com" : "staging.appsaholic.com";
    public final static String api_domainName = isprod ? "https://api.appsaholic.com" : "http://api-dev.appsaholic.com";
}
