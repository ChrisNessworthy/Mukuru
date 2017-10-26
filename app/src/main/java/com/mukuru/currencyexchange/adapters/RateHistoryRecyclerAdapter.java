package com.mukuru.currencyexchange.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mukuru.currencyexchange.R;
import com.mukuru.currencyexchange.models.CurrencyDetail;
import com.mukuru.currencyexchange.models.CurrencyObject;
import com.mukuru.currencyexchange.utility.HelperMethods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class RateHistoryRecyclerAdapter extends RecyclerView.Adapter<RateHistoryRecyclerAdapter.CurrencyViewHolder> {
    private List<CurrencyDetail> currencyDetails;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CurrencyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView rateTitleTextView;
        public TextView rateTextView;
        public TextView timeTitleTextView;
        public TextView timeTextView;
        public CurrencyViewHolder(View view) {
            super(view);
            rateTitleTextView = (TextView) view.findViewById(R.id.rate_text);
            rateTextView = (TextView) view.findViewById(R.id.rate);
            timeTitleTextView = (TextView) view.findViewById(R.id.time_text);
            timeTextView = (TextView) view.findViewById(R.id.time);
        }
    }

    public RateHistoryRecyclerAdapter(List<CurrencyDetail> currencyDetails) {
        this.currencyDetails = currencyDetails;
    }

    @Override
    public RateHistoryRecyclerAdapter.CurrencyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                            int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rate_list_item, parent, false);

        CurrencyViewHolder vh = new CurrencyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CurrencyViewHolder holder, int position) {
        final CurrencyDetail obj = currencyDetails.get(position);

        holder.rateTextView.setText(obj.getRate() + "");
        holder.timeTextView.setText(formatDate(obj.getTimestamp() * 1000L));
    }

    private String formatDate(long milliseconds) {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        return sdf.format(calendar.getTime());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currencyDetails.size();
    }
}
