package org.jeff.beepboop;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class ProfileFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            transactions.add(new Transaction(new Date(), 10, 100, Transaction.BOUGHT));
        }
        TransactionAdapter adapter = new TransactionAdapter(context, R.layout.transaction_item, transactions);
        ListView lv = v.findViewById(R.id.transaction_history);
        lv.setAdapter(adapter);
        return v;
    }
}
