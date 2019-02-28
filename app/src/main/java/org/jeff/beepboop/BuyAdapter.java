package org.jeff.beepboop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BuyAdapter extends ArrayAdapter<Transaction> {

    public BuyAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Transaction transaction = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
        }
        TextView creditsView = convertView.findViewById(R.id.credits);
        TextView moneyView = convertView.findViewById(R.id.money);
        creditsView.setText(Integer.toString(transaction.credits));
        moneyView.setText(String.format("$%d", transaction.cash));
        return convertView;
    }
}
