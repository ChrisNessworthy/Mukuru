package com.mukuru.currencyexchange.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.mukuru.currencyexchange.ORMlite.DatabaseHelper;
import com.mukuru.currencyexchange.R;
import com.mukuru.currencyexchange.adapters.CurrencyRecyclerAdapter;
import com.mukuru.currencyexchange.models.CurrencyObject;
import com.mukuru.currencyexchange.network.ApiHelper;
import com.mukuru.currencyexchange.network.URLConstants;
import com.mukuru.currencyexchange.utility.VolleySingleton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartupFragment extends Fragment {

    private OnCurrencyAddClickListener currencyAddListener;
    private OnCurrencyClickListener currencyClickListener;
    private OnCurrencyConvertClickListener currencyConvertClickListener;

    private RecyclerView currencyRecyclerView;
    private CurrencyRecyclerAdapter currencyAdapter;

    private FloatingActionButton addCurrency;
    private FloatingActionButton convertCurrency;

    private DatabaseHelper databaseHelper;
    private ConnectionSource connection;
    private Dao<CurrencyObject, Integer> currencyObjectDao;

    private List<CurrencyObject> workingList;

    public StartupFragment() {
        // Required empty public constructor
    }


    public static StartupFragment newInstance() {
        StartupFragment fragment = new StartupFragment();

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseHelper = getHelper();
        connection = databaseHelper.getConnectionSource();

        try {
            currencyObjectDao = databaseHelper.getDao(CurrencyObject.class);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        currencyRecyclerView = (RecyclerView) getActivity().findViewById(R.id.currency_list);
        addCurrency = (FloatingActionButton) getActivity().findViewById(R.id.fab_add);
        convertCurrency = (FloatingActionButton) getActivity().findViewById(R.id.fab_convert);

        addCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currencyAddListener.onCurrencyAddClick();
            }
        });

        convertCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currencyConvertClickListener.onCurrencyConvertClick();
            }
        });

        workingList = new ArrayList<>();

        loadCurrencies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_startup, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCurrencyAddClickListener) {
            currencyAddListener = (OnCurrencyAddClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCurrencyAddClickListener");
        }
        if (context instanceof OnCurrencyClickListener) {
            currencyClickListener = (OnCurrencyClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCurrencyClickListener");
        }
        if (context instanceof OnCurrencyConvertClickListener) {
            currencyConvertClickListener = (OnCurrencyConvertClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCurrencyConvertClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        currencyAddListener = null;
        currencyClickListener = null;
    }

    CurrencyRecyclerAdapter.OnCurrencyDeleteListener currencyDeleteListener = new CurrencyRecyclerAdapter.OnCurrencyDeleteListener() {
        @Override
        public void onCurrencyDelete(CurrencyObject item) {
            untrackCurrency(item);
        }
    };

    CurrencyRecyclerAdapter.OnCurrencySelectListener currencySelectListener = new CurrencyRecyclerAdapter.OnCurrencySelectListener() {
        @Override
        public void onCurrencySelect(CurrencyObject item) {
            currencyClickListener.onCurrencyClick(item.getId());
        }
    };

    private void loadViewWithCurrencies() {

        currencyAdapter = new CurrencyRecyclerAdapter(workingList, currencyDeleteListener, currencySelectListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        currencyRecyclerView.setLayoutManager(mLayoutManager);
        currencyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        currencyRecyclerView.setAdapter(currencyAdapter);

    }

    public void loadCurrencies(){
        workingList = getCurrenciesFromDB();

        if (workingList.isEmpty()) {
            String url = URLConstants.URLS.CURRENCY_URL();

            StringRequest getCurrencies = new ApiHelper().buildApiStringCall(Request.Method.GET, url, null,
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
            getCurrencies.setTag("getCurrencies");
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(getCurrencies, "getCurrencies");
        } else {
            loadViewWithCurrencies();
        }
    }

    private List<CurrencyObject> getCurrenciesFromDB() {
        List<CurrencyObject> dbResponse = new ArrayList<>();

        try {
            QueryBuilder<CurrencyObject, Integer> queryBuilder = currencyObjectDao.queryBuilder();
            Where<CurrencyObject, Integer> where = queryBuilder.where();

            where.eq("tracked", true);

            PreparedQuery<CurrencyObject> preparedQuery = queryBuilder.prepare();

            dbResponse = currencyObjectDao.query(preparedQuery);

            return dbResponse;
        } catch (SQLException e) {
            e.printStackTrace();
            return dbResponse;
        }
    }

    private void handleCurrencyCallSuccess(String currencies) {
        Gson gson = new Gson();

        Map<String, String> currencyMap = gson.fromJson(currencies, new TypeToken<Map<String, String>>(){}.getType());

        for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
            CurrencyObject currencyResponse = new CurrencyObject(entry.getKey(), entry.getValue(), false, null);
            try {
                currencyObjectDao.createIfNotExists(currencyResponse);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        workingList = getCurrenciesFromDB();

        loadViewWithCurrencies();
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void untrackCurrency(CurrencyObject item) {
        CurrencyObject untrackItem = item;
        untrackItem.setTracked(false);
        untrackItem.setWarningRate(-1);
        try {
            currencyObjectDao.createOrUpdate(untrackItem);
            workingList.remove(untrackItem);
            currencyAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public interface OnCurrencyClickListener {
        void onCurrencyClick(int id);
    }

    public interface OnCurrencyConvertClickListener {
        void onCurrencyConvertClick();
    }

    public interface OnCurrencyAddClickListener {
        void onCurrencyAddClick();
    }
}
