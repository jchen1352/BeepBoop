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

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    public TransactionAdapter(@NonNull Context context, int resource, @NonNull List<Transaction> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Transaction transaction = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
        }
        TextView dateView = convertView.findViewById(R.id.date);
        TextView creditsView = convertView.findViewById(R.id.credits);
        TextView moneyView = convertView.findViewById(R.id.money);
        TextView statusView = convertView.findViewById(R.id.status);
        dateView.setText(transaction.date.toString());
        creditsView.setText(Integer.toString(transaction.credits));
        moneyView.setText(String.format("$%d.%02d", transaction.cents / 100, transaction.cents % 100));
        statusView.setText(transaction.status);
        return convertView;
    }
}
