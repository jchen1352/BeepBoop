package org.jeff.beepboop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
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
        TextView icon = convertView.findViewById(R.id.transaction_icon);
        icon.setTypeface(FontManager.getTypeface(convertView.getContext(), FontManager.FONTAWESOME));
        icon.setText(convertView.getContext().getResources().getText(R.string.transaction_arrow));

        TextView creditsView = convertView.findViewById(R.id.credits);
        TextView moneyView = convertView.findViewById(R.id.money);
        TextView statusView = convertView.findViewById(R.id.status);
        creditsView.setText(Integer.toString(transaction.credits));
        moneyView.setText(String.format("$ %d", transaction.cash));
        statusView.setText(transaction.status);

        switch (transaction.status) {
            case "FOR_SALE":
                statusView.setText("For sale");
                statusView.setTextColor(ResourcesCompat.getColor(convertView.getContext().getResources(), R.color.IndianRed, null));
            break;
            case "SOLD":
                statusView.setText("Sold");
                statusView.setTextColor(ResourcesCompat.getColor(convertView.getContext().getResources(), R.color.Green, null));
            break;
            default:
                statusView.setText("");
        }
        return convertView;
    }
}
