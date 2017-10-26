package com.mukuru.currencyexchange.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.mukuru.currencyexchange.adapters.CurrencySpinnerAdapter;
import com.mukuru.currencyexchange.models.CurrencyDetail;
import com.mukuru.currencyexchange.models.CurrencyObject;
import com.mukuru.currencyexchange.network.ApiHelper;
import com.mukuru.currencyexchange.network.URLConstants;
import com.mukuru.currencyexchange.utility.VolleySingleton;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddCurrencyFragment extends Fragment {

    private OnCurrencySaveClickListener currencySaveListener;
    private OnCancelClickListener cancelListener;

    private Spinner currencySpinner;
    private EditText warningRateEditText;
    private TextView cancelButton;
    private TextView saveButton;

    private DatabaseHelper databaseHelper;
    private ConnectionSource connection;
    private Dao<CurrencyObject, Integer> currencyObjectDao;

    private List<CurrencyObject> workingList;
    private CurrencySpinnerAdapter currencySpinnerAdapter;

    private CurrencyObject selectedCurrency;

    public AddCurrencyFragment() {
        // Required empty public constructor
    }


    public static AddCurrencyFragment newInstance() {
        AddCurrencyFragment fragment = new AddCurrencyFragment();

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

        currencySpinner = (Spinner) getActivity().findViewById(R.id.currency_spinner);
        warningRateEditText = (EditText) getActivity().findViewById(R.id.warning_rate);
        cancelButton = (TextView) getActivity().findViewById(R.id.cancel);
        saveButton = (TextView) getActivity().findViewById(R.id.save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewCurrency();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelListener.onCancelClick();
            }
        });

        workingList = new ArrayList<>();

        workingList = getCurrenciesFromDB();

        currencySpinnerAdapter = new CurrencySpinnerAdapter(this.getContext(), R.layout.currency_spinner_choice, workingList);

        currencySpinner.setAdapter(currencySpinnerAdapter);

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    currencySpinnerAdapter.setSelectedCurrency(currencySpinnerAdapter.getItem(position));
                    selectedCurrency = currencySpinnerAdapter.getItem(position);
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currencySpinnerAdapter.setSelectedCurrency(null);
                selectedCurrency = null;
            }
        });
    }

    private void saveNewCurrency(){
        if (selectedCurrency != null) {
            double warningRate = -1;
            if (!TextUtils.isEmpty(warningRateEditText.getText().toString())) {
                try {
                    warningRate = Double.valueOf(warningRateEditText.getText().toString());
                } catch (NumberFormatException nfe) {
                    warningRate = -1;
                }
            }

            selectedCurrency.setWarningRate(warningRate);
            selectedCurrency.setTracked(true);

            try {
                currencyObjectDao.createOrUpdate(selectedCurrency);
                currencySaveListener.onCurrencySaveClick();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.no_currency_error), Toast.LENGTH_LONG).show();
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
        return inflater.inflate(R.layout.fragment_add_currency, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCurrencySaveClickListener) {
            currencySaveListener = (OnCurrencySaveClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCurrencySaveClickListener");
        }
        if (context instanceof OnCancelClickListener) {
            cancelListener = (OnCancelClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCancelClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        currencySaveListener = null;
    }

    private List<CurrencyObject> getCurrenciesFromDB() {
        List<CurrencyObject> dbResponse = new ArrayList<>();

        try {
            QueryBuilder<CurrencyObject, Integer> queryBuilder = currencyObjectDao.queryBuilder();
            Where<CurrencyObject, Integer> where = queryBuilder.where();

            where.eq("tracked", false);

            PreparedQuery<CurrencyObject> preparedQuery = queryBuilder.prepare();

            dbResponse = currencyObjectDao.query(preparedQuery);

            return dbResponse;
        } catch (SQLException e) {
            e.printStackTrace();
            return dbResponse;
        }
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public interface OnCurrencySaveClickListener {
        void onCurrencySaveClick();
    }

    public interface OnCancelClickListener {
        void onCancelClick();
    }
}
