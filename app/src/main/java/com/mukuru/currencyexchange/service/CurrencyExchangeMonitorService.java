package com.mukuru.currencyexchange.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mukuru.currencyexchange.ORMlite.DatabaseHelper;
import com.mukuru.currencyexchange.models.CurrencyDetail;
import com.mukuru.currencyexchange.models.CurrencyDetailResponse;
import com.mukuru.currencyexchange.models.CurrencyObject;
import com.mukuru.currencyexchange.network.ApiHelper;
import com.mukuru.currencyexchange.network.URLConstants;
import com.mukuru.currencyexchange.utility.VolleySingleton;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CurrencyExchangeMonitorService extends IntentService {

    public static final String BROADCAST_ACTION = "BROADCAST_COMPLETE";

    public CurrencyExchangeMonitorService() {
        super("CurrencyExchangeMonitorService");
    }

    private static DatabaseHelper databaseHelper;
    private static Dao<CurrencyObject, Integer> currencyObjectDao;
    private static Dao<CurrencyDetail, Integer> currencyDetailsDao;

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this,
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = URLConstants.URLS.LATEST_EXCHANGE_URL();

        Map<String, String> params = new HashMap<>();
        params.put("app_id", URLConstants.APP_ID);

        StringRequest getCurrencies = new ApiHelper().buildApiStringCall(Request.Method.GET, url,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleCurrencyCallSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        getCurrencies.setTag("getCurrencyDetails");
        VolleySingleton.getInstance(this).addToRequestQueue(getCurrencies, "getCurrencyDetails");
    }

    private void handleCurrencyCallSuccess(String currencyDetails) {
        databaseHelper = getHelper();

        try {
            currencyObjectDao = databaseHelper.getDao(CurrencyObject.class);
            currencyDetailsDao = databaseHelper.getDao(CurrencyDetail.class);

            List<CurrencyObject> currencies = currencyObjectDao.queryForAll();

            Gson gson = new Gson();

            CurrencyDetailResponse response = gson.fromJson(currencyDetails, CurrencyDetailResponse.class);

            for (Map.Entry<String, Double> rate : response.getRates().entrySet()) {
                for (CurrencyObject currencyObject : currencies) {
                    if (rate.getKey().equals(currencyObject.getCode())) {
                        CurrencyDetail currencyDetail = new CurrencyDetail(currencyObject.getId(), response.getTimestamp(), response.getBase(), rate.getKey(), rate.getValue());

                        currencyDetailsDao.createOrUpdate(currencyDetail);
                        break;
                    }
                }
            }

            Bundle bundle = new Bundle();
            bundle.putLong("timestamp", response.getTimestamp());
            Intent localIntent = new Intent(BROADCAST_ACTION);
            localIntent.putExtra("timestamp", response.getTimestamp());
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
