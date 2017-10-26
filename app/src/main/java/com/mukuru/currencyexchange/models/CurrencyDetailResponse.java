package com.mukuru.currencyexchange.models;

import java.util.Map;

/**
 * Created by Chris on 2017/10/24.
 */

public class CurrencyDetailResponse {

    private long timestamp;
    private String base;
    private Map<String, Double> rates;

    public CurrencyDetailResponse() {
    }

    public CurrencyDetailResponse(long timestamp, String base, Map<String, Double> rates) {
        this.timestamp = timestamp;
        this.base = base;
        this.rates = rates;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
