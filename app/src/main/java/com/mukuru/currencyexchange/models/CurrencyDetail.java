package com.mukuru.currencyexchange.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Chris on 2017/10/24.
 */

@DatabaseTable(tableName = "currency_detail")
public class CurrencyDetail {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private int currencyId;
    @DatabaseField
    private long timestamp;
    @DatabaseField
    private String base;
    @DatabaseField
    private String code;
    @DatabaseField
    private double rate;

    public CurrencyDetail() {
    }

    public CurrencyDetail(long timestamp, String base, String code, double rate) {
        this.timestamp = timestamp;
        this.base = base;
        this.code = code;
        this.rate = rate;
    }

    public CurrencyDetail(int currencyId, long timestamp, String base, String code, double rate) {
        this.currencyId = currencyId;
        this.timestamp = timestamp;
        this.base = base;
        this.code = code;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
