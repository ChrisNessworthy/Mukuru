package com.mukuru.currencyexchange.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mukuru.currencyexchange.StartupActivity;

public class ServiceCompleteBroadcastReceiver extends BroadcastReceiver {

    private StartupActivity activity;

    public ServiceCompleteBroadcastReceiver(StartupActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long timestamp = intent.getExtras().getLong("timestamp");
        activity.notifyIntentComplete(timestamp);
    }

}