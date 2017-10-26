package com.mukuru.currencyexchange.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mukuru.currencyexchange.R;
import com.mukuru.currencyexchange.models.CurrencyObject;

import java.util.List;

public class CurrencySpinnerAdapter extends ArrayAdapter<CurrencyObject> {

    private CurrencyObject selectedCurrency;

    private class ViewHolder{
        private View parent;
        private TextView typeText;

        public ViewHolder(View parent) {
            this.parent = parent;
            this.typeText = (TextView) this.parent.findViewById(R.id.currency_text);
        }
    }

    public CurrencySpinnerAdapter(Context context, int resource, List<CurrencyObject> objects) {
        super(context, resource, objects);
    }

    public CurrencyObject getSelectedCurrency(){
        return selectedCurrency;
    }

    public void setSelectedCurrency(CurrencyObject selectedCurrency){
        this.selectedCurrency = selectedCurrency;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.currency_spinner_choice, null, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        holder.typeText.setText(getItem(position).getName().toString());

        return holder.parent;
    }
}
