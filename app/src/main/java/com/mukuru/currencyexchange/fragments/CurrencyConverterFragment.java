package com.mukuru.currencyexchange.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class CurrencyConverterFragment extends Fragment {
    private Spinner convertCurrencySpinner;
    private EditText amountConvert;
    private TextView convertedAmount;
    private TextView convertButton;

    private DatabaseHelper databaseHelper;
    private ConnectionSource connection;
    private Dao<CurrencyObject, Integer> currencyObjectDao;
    private Dao<CurrencyDetail, Integer> currencyDetailDao;

    private List<CurrencyObject> workingList;
    private CurrencySpinnerAdapter currencySpinnerAdapter;

    private CurrencyObject selectedCurrency;

    public CurrencyConverterFragment() {
        // Required empty public constructor
    }


    public static CurrencyConverterFragment newInstance() {
        CurrencyConverterFragment fragment = new CurrencyConverterFragment();

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseHelper = getHelper();
        connection = databaseHelper.getConnectionSource();

        try {
            currencyObjectDao = databaseHelper.getDao(CurrencyObject.class);
            currencyDetailDao = databaseHelper.getDao(CurrencyDetail.class);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        convertCurrencySpinner = (Spinner) getActivity().findViewById(R.id.convert_currency_spinner);
        amountConvert = (EditText) getActivity().findViewById(R.id.amount_convert);
        convertedAmount = (TextView) getActivity().findViewById(R.id.converted_amount);
        convertButton = (TextView) getActivity().findViewById(R.id.convert_button);

        workingList = new ArrayList<>();

        workingList = getCurrenciesFromDB();

        currencySpinnerAdapter = new CurrencySpinnerAdapter(this.getContext(), R.layout.currency_spinner_choice, workingList);

        convertCurrencySpinner.setAdapter(currencySpinnerAdapter);

        convertCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConversion();
            }
        });
    }

    private void startConversion() {
        if (selectedCurrency != null) {
            try {
                QueryBuilder<CurrencyDetail, Integer> queryBuilder = currencyDetailDao.queryBuilder();
                Where<CurrencyDetail, Integer> where = queryBuilder.where();

                where.eq("currencyId", selectedCurrency.getId());
                queryBuilder.orderBy("timestamp", false);

                PreparedQuery<CurrencyDetail> preparedQuery = queryBuilder.prepare();

                List<CurrencyDetail> details = currencyDetailDao.query(preparedQuery);

                if (!details.isEmpty()) {
                    convertCurrency(details.get(0));
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_rate_error), Toast.LENGTH_LONG).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getResources().getString(R.string.no_rate_error), Toast.LENGTH_LONG).show();
            }
        } else {
            convertedAmount.setText("0");
        }
    }

    private void convertCurrency(CurrencyDetail convertCurrencyDetails){
        if (selectedCurrency != null) {
            double amountToConvert = -1;
            if (!TextUtils.isEmpty(amountConvert.getText().toString())) {
                try {
                    amountToConvert = Double.valueOf(amountConvert.getText().toString());
                } catch (NumberFormatException nfe) {
                    amountToConvert = -1;
                }
            }

            if (amountToConvert > 0) {
                if (amountToConvert > 199.99) {
                    double convertedAmountPreMarkup = amountToConvert * convertCurrencyDetails.getRate();
                    double finalAmount = (convertedAmountPreMarkup * (4 / 100.0f)) + convertedAmountPreMarkup;
                    convertedAmount.setText(finalAmount + " " + selectedCurrency.getCode());
                } else {
                    double convertedAmountPreMarkup = amountToConvert * convertCurrencyDetails.getRate();
                    double finalAmount = (convertedAmountPreMarkup * (7 / 100.0f)) + convertedAmountPreMarkup;
                    convertedAmount.setText(finalAmount + " " + selectedCurrency.getCode());
                }
            }
        } else {
            convertedAmount.setText("0");
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
        return inflater.inflate(R.layout.fragment_currency_converter, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private List<CurrencyObject> getCurrenciesFromDB() {
        List<CurrencyObject> dbResponse = new ArrayList<>();

        try {
            dbResponse = currencyObjectDao.queryForAll();

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
}
