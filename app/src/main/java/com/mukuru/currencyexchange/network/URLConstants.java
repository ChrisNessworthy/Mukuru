package com.mukuru.currencyexchange.network;

public class URLConstants {

    public static final String CALL_CURRENCY = "currencies";
    public static final String CALL_LATEST_EXCHANGE = "latest_exchange";

    public static final String APP_ID = "ff9fee62da924e608b55f261d6320e68";

    public static class URLS {
        public static final String URL = "https://openexchangerates.org/api";
        public static final String CURRENCY_EXTENSION = "/currencies.json";
        public static final String LATEST_EXCHANGE_EXTENSION = "/latest.json";

        public static String CURRENCY_URL(){return URL + CURRENCY_EXTENSION;}
        public static String LATEST_EXCHANGE_URL(){return URL + LATEST_EXCHANGE_EXTENSION;}

    }
}
