package com.mukuru.currencyexchange.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mukuru.currencyexchange.adapters.RateHistoryRecyclerAdapter;
import com.mukuru.currencyexchange.models.CurrencyDetail;
import com.mukuru.currencyexchange.models.CurrencyObject;
import com.mukuru.currencyexchange.network.ApiHelper;
import com.mukuru.currencyexchange.network.URLConstants;
import com.mukuru.currencyexchange.utility.VolleySingleton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyDetailFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private ConnectionSource connection;
    private Dao<CurrencyObject, Integer> currencyObjectDao;
    private Dao<CurrencyDetail, Integer> currencyDetailDao;

    private RecyclerView rateRecyclerView;
    private TextView currentRateText;
    private TextView currentRate;

    public CurrencyDetailFragment() {
        // Required empty public constructor
    }


    public static CurrencyDetailFragment newInstance() {
        CurrencyDetailFragment fragment = new CurrencyDetailFragment();

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();

        int id = bundle.getInt("id");

        databaseHelper = getHelper();
        connection = databaseHelper.getConnectionSource();

        try {
            currencyObjectDao = databaseHelper.getDao(CurrencyObject.class);
            currencyDetailDao = databaseHelper.getDao(CurrencyDetail.class);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        rateRecyclerView = (RecyclerView) getActivity().findViewById(R.id.rate_history);
        currentRateText = (TextView) getActivity().findViewById(R.id.current_rate_text);
        currentRate = (TextView) getActivity().findViewById(R.id.current_rate);

        loadViews(id);
    }

    private void loadViews(int id) {
        CurrencyObject currency = getCurrencyById(id);
        if (currency != null) {
            List<CurrencyDetail> currencyDetails = getCurrencyDetailsWithTimestamp(id);

            currentRateText.setText(currency.getCode() + getResources().getString(R.string.currency_rate_text));
            if (!currencyDetails.isEmpty()) {
                currentRate.setText(currencyDetails.get(0).getRate() + "");

                RateHistoryRecyclerAdapter rateHistoryAdapter = new RateHistoryRecyclerAdapter(currencyDetails);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                rateRecyclerView.setLayoutManager(mLayoutManager);
                rateRecyclerView.setItemAnimator(new DefaultItemAnimator());
                rateRecyclerView.setAdapter(rateHistoryAdapter);
            }

        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_for_provider_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_currency_detail, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private CurrencyObject getCurrencyById(int id) {
        try {
            return currencyObjectDao.queryForId(id);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    private List<CurrencyDetail> getCurrencyDetailsWithTimestamp(int currencyId) {
        try {
            QueryBuilder<CurrencyDetail, Integer> queryBuilder = currencyDetailDao.queryBuilder();
            Where<CurrencyDetail, Integer> where = queryBuilder.where();

            where.eq("currencyId", currencyId);

            queryBuilder.orderBy("timestamp", false);

            PreparedQuery<CurrencyDetail> preparedQuery = queryBuilder.prepare();

            List<CurrencyDetail> details = currencyDetailDao.query(preparedQuery);

            if (!details.isEmpty()) {
                return details;
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.no_rate_error), Toast.LENGTH_LONG).show();
                return new ArrayList<>();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
