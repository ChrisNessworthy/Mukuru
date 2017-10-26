package com.mukuru.currencyexchange;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.support.ConnectionSource;
import com.mukuru.currencyexchange.ORMlite.DatabaseHelper;
import com.mukuru.currencyexchange.fragments.AddCurrencyFragment;
import com.mukuru.currencyexchange.fragments.CurrencyConverterFragment;
import com.mukuru.currencyexchange.fragments.CurrencyDetailFragment;
import com.mukuru.currencyexchange.fragments.StartupFragment;
import com.mukuru.currencyexchange.models.CurrencyDetail;
import com.mukuru.currencyexchange.models.CurrencyObject;
import com.mukuru.currencyexchange.service.ServiceBroadcastReceiver;
import com.mukuru.currencyexchange.service.ServiceCompleteBroadcastReceiver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StartupActivity extends AppCompatActivity implements StartupFragment.OnCurrencyAddClickListener,
        StartupFragment.OnCurrencyClickListener,
        StartupFragment.OnCurrencyConvertClickListener,
        AddCurrencyFragment.OnCancelClickListener,
        AddCurrencyFragment.OnCurrencySaveClickListener{

    public static final String BROADCAST_ACTION = "BROADCAST_COMPLETE";
    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";

    private DatabaseHelper databaseHelper;
    private ConnectionSource connection;

    private ServiceCompleteBroadcastReceiver serviceCompleteBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        Intent notificationIntent = getIntent();

        setupResultReceiver();
        setBackgroundTaskAlarm();

        if (notificationIntent.hasExtra("id")) {
            onCurrencyClick(notificationIntent.getIntExtra("id", -1));
        } else {
            if (findViewById(R.id.fragment_layout) != null) {
                if (savedInstanceState != null) {
                    return;
                }

                StartupFragment startupFragment = new StartupFragment();

                startupFragment.setArguments(getIntent().getExtras());

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_layout, startupFragment).commit();
            }
        }
    }

    private void setBackgroundTaskAlarm(){
        Intent intent = new Intent(getApplicationContext(), ServiceBroadcastReceiver.class);

        final PendingIntent pIntent = PendingIntent.getBroadcast(this, ServiceBroadcastReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                (2*AlarmManager.INTERVAL_HOUR), pIntent);
    }

    private void setupResultReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(BROADCAST_ACTION);

        serviceCompleteBroadcastReceiver = new ServiceCompleteBroadcastReceiver(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceCompleteBroadcastReceiver, statusIntentFilter);
    }

    public void notifyIntentComplete(long timestamp) {
        try {
            List<CurrencyObject> trackedCurrencies = getTrackedCurrencies();
            if (!trackedCurrencies.isEmpty()) {
                List<CurrencyDetail> latestDetails = getCurrencyDetailsWithTimestamp(timestamp);
                if (!latestDetails.isEmpty()) {
                    for (CurrencyObject obj : trackedCurrencies) {
                        if (obj.getWarningRate() > 0) {
                            for (CurrencyDetail detail : latestDetails) {
                                if (obj.getCode().equals(detail.getCode())) {
                                    if (detail.getRate() < obj.getWarningRate()) {
                                        createNotification(obj, detail);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotification(CurrencyObject obj, CurrencyDetail detail) {
        Intent resultIntent = new Intent(this, StartupActivity.class);
        resultIntent.setAction(NOTIFICATION_ACTION);
        resultIntent.putExtra("id", obj.getId());
        resultIntent.putExtra("code", obj.getCode());
        resultIntent.putExtra("detailId", detail.getId());
        resultIntent.putExtra("timestamp", detail.getTimestamp());

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(obj.getCode() + " over Warning Rate")
                        .setContentText(obj.getCode() + " has gone over set Warning Rate");

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, mBuilder.build());
    }

    private List<CurrencyObject> getTrackedCurrencies() {
        try {
            databaseHelper = getHelper();

            Dao<CurrencyObject, Integer> currencyObjectDao = databaseHelper.getDao(CurrencyObject.class);

            QueryBuilder<CurrencyObject, Integer> queryBuilder = currencyObjectDao.queryBuilder();
            Where<CurrencyObject, Integer> where = queryBuilder.where();

            where.eq("tracked", true);

            PreparedQuery<CurrencyObject> preparedQuery = queryBuilder.prepare();

            return currencyObjectDao.query(preparedQuery);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<CurrencyDetail> getCurrencyDetailsWithTimestamp(long timestamp) {
        try {
            databaseHelper = getHelper();

            Dao<CurrencyDetail, Integer> currencyDetailDao = databaseHelper.getDao(CurrencyDetail.class);

            QueryBuilder<CurrencyDetail, Integer> queryBuilder = currencyDetailDao.queryBuilder();
            Where<CurrencyDetail, Integer> where = queryBuilder.where();

            where.eq("timestamp", timestamp);

            PreparedQuery<CurrencyDetail> preparedQuery = queryBuilder.prepare();

            return currencyDetailDao.query(preparedQuery);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceCompleteBroadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this,
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onCurrencyClick(int id) {
        if (findViewById(R.id.fragment_layout) != null) {
            CurrencyDetailFragment currencyDetailFragment = new CurrencyDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("id", id);

            currencyDetailFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_layout, currencyDetailFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    @Override
    public void onCurrencyAddClick() {
        if (findViewById(R.id.fragment_layout) != null) {
            AddCurrencyFragment addCurrencyFragment = new AddCurrencyFragment();

            addCurrencyFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_layout, addCurrencyFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    @Override
    public void onCurrencySaveClick() {
        if (findViewById(R.id.fragment_layout) != null) {
            StartupFragment startupFragment = new StartupFragment();

            startupFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_layout, startupFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    @Override
    public void onCancelClick() {
        if (findViewById(R.id.fragment_layout) != null) {
            StartupFragment startupFragment = new StartupFragment();

            startupFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_layout, startupFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    @Override
    public void onCurrencyConvertClick() {
        if (findViewById(R.id.fragment_layout) != null) {
            CurrencyConverterFragment currencyConverterFragment = new CurrencyConverterFragment();

            currencyConverterFragment.setArguments(getIntent().getExtras());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_layout, currencyConverterFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }
}
