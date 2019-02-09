package org.jeff.beepboop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class BuyFragment extends Fragment {

    private Context context;
    private ArrayList<Transaction> transactions;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_buy, container, false);
        transactions = new ArrayList<>();

        final BuyAdapter adapter = new BuyAdapter(context, R.layout.transaction_item, transactions);
        final ListView lv = v.findViewById(R.id.buylist);
        lv.setAdapter(adapter);

        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        final String userid = prefs.getString(getString(R.string.pref_user), "fail");

        String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
        JsonArrayRequest jsonObjectRequest0 = new JsonArrayRequest
                (Request.Method.GET, salesListingIdURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i += 1) {
                                JSONObject obj = response.getJSONObject(i);
                                Log.d("asdf", obj.toString());
                                if (obj.getString("state").equals("FOR_SALE") && !obj.getString("sellerAccount").endsWith(userid)) {
                                    final int numCredits = (int) obj.getDouble("numCredits");
                                    final int price = (int) obj.getDouble("price");
                                    final String listingId = obj.getString("listingId");
                                    final String status = obj.getString("state");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            transactions.add(new Transaction(numCredits, price, listingId, status));
                                            lv.requestLayout();
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("asdf", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("asdf", error.getLocalizedMessage());
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest0);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Transaction transaction = transactions.get(position);
                int dollars = transaction.cash;
                int credits = transaction.credits;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.buy_dialog, credits, dollars))
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/Buy";
                                JSONObject objRequest = new JSONObject();
                                try {
                                    objRequest.put("$class", "org.acme.vehicle.auction.Buy");
                                    objRequest.put("buyerAccount", userid);
                                    objRequest.put("listing", transaction.listingId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest
                                        (Request.Method.POST, salesListingIdURL, objRequest, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("asdf", "Buy completed");
                                            }
                                        }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO: Handle error
                                                Log.e("asdf", error.getLocalizedMessage());
                                            }
                                        });

                                // Access the RequestQueue through your singleton class.
                                MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest0);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return v;
    }
}
