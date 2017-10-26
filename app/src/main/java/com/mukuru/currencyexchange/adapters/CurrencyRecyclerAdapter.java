package com.mukuru.currencyexchange.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mukuru.currencyexchange.R;
import com.mukuru.currencyexchange.models.CurrencyObject;

import java.util.List;

public class CurrencyRecyclerAdapter extends RecyclerView.Adapter<CurrencyRecyclerAdapter.CurrencyViewHolder> {
    private List<CurrencyObject> currencyObjects;
    private OnCurrencyDeleteListener currencyDeleteListener;
    private OnCurrencySelectListener currencySelectListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CurrencyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView codeTextView;
        public TextView nameTextView;
        public ImageView deleteImageView;
        public CurrencyViewHolder(View view) {
            super(view);
            codeTextView = (TextView) view.findViewById(R.id.code);
            nameTextView = (TextView) view.findViewById(R.id.name);
            deleteImageView = (ImageView) view.findViewById(R.id.delete);
        }
    }

    public CurrencyRecyclerAdapter(List<CurrencyObject> currencyObjects, OnCurrencyDeleteListener deleteListener, OnCurrencySelectListener selectListener) {
        this.currencyObjects = currencyObjects;
        currencyDeleteListener = deleteListener;
        currencySelectListener = selectListener;
    }

    @Override
    public CurrencyRecyclerAdapter.CurrencyViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_list_item, parent, false);

        CurrencyViewHolder vh = new CurrencyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CurrencyViewHolder holder, int position) {
        final CurrencyObject obj = currencyObjects.get(position);

        holder.codeTextView.setText(obj.getCode());
        holder.nameTextView.setText(obj.getName());
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currencyDeleteListener.onCurrencyDelete(obj);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currencySelectListener.onCurrencySelect(obj);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currencyObjects.size();
    }

    public interface OnCurrencyDeleteListener {
        void onCurrencyDelete(CurrencyObject item);
    }

    public interface OnCurrencySelectListener {
        void onCurrencySelect(CurrencyObject item);
    }
}
