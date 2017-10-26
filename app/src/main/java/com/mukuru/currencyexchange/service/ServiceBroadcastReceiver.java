package com.mukuru.currencyexchange.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.mukuru.currencyexchange.ORMlite.DatabaseHelper;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, CurrencyExchangeMonitorService.class);
        context.startService(serviceIntent);
    }

}