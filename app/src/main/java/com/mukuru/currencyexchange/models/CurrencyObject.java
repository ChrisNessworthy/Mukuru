package com.mukuru.currencyexchange.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chris on 2017/10/24.
 */

@DatabaseTable(tableName = "currencies")
public class CurrencyObject {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String code;
    @DatabaseField
    private String name;
    @DatabaseField
    private boolean tracked;
    @DatabaseField
    private double warningRate;

    public CurrencyObject() {

    }

    public CurrencyObject(String code, String name, boolean tracked, Double warningRate) {
        this.code = code;
        this.name = name;
        this.tracked = tracked;
        if (warningRate == null) {
            this.warningRate = -1;
        } else {
            this.warningRate = warningRate;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTracked() {
        return tracked;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    public double getWarningRate() {
        return warningRate;
    }

    public void setWarningRate(double warningRate) {
        this.warningRate = warningRate;
    }
}
