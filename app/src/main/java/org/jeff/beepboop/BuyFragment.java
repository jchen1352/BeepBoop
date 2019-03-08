package org.jeff.beepboop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class BuyFragment extends MyFragment {

    private ArrayList<Transaction> transactions;
    private static final String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
    private BuyAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_buy, container, false);
        transactions = new ArrayList<>();

        adapter = new BuyAdapter(context, R.layout.transaction_item, transactions);
        final ListView lv = v.findViewById(R.id.buylist);
        lv.setAdapter(adapter);

        getTransactions();
        swipeRefresh = v.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactions();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Transaction transaction = transactions.get(position);
                final int dollars = transaction.cash;
                int credits = transaction.credits;
                AlertDialog.Builder builder =  new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.buy_dialog, credits, dollars))
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tryBuy(dollars, transaction);
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                                ResourcesCompat.getColor(context.getResources(), R.color.IndianRed, null));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                ResourcesCompat.getColor(context.getResources(), R.color.Green, null));
                    }
                });
                dialog.show();
            }
        });
        return v;
    }

    private void getTransactions() {
        Log.d("BuyFragment", "Getting Transactions");
        JsonArrayRequest jsonObjectRequest0 = new JsonArrayRequest
                (Request.Method.GET, salesListingIdURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("BuyFragment", "Got response");
                        if (attached) {
                            try {
                                transactions.clear();
                                for (int i = 0; i < response.length(); i += 1) {
                                    JSONObject obj = response.getJSONObject(i);
                                    Log.d("BuyFragment", obj.toString());
                                    if (obj.getString("state").equals("FOR_SALE") && !obj.getString("sellerAccount").endsWith(userid)) {
                                        final int numCredits = (int) obj.getDouble("numCredits");
                                        final int price = (int) obj.getDouble("price");
                                        final String listingId = obj.getString("listingId");
                                        final String status = obj.getString("state");
                                        transactions.add(new Transaction(numCredits, price, listingId, status));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest0);
    }

    private void tryBuy(final double moneyAmount, Transaction transaction) {
        String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/Buy";
        String creditListingPrefix = "org.acme.vehicle.auction.CreditListing#";
        String beepBoopAccountPrefix = "org.acme.vehicle.auction.BeepBoopAccount#";

        JSONObject objRequest = new JSONObject();
        try {
            Log.d("DEBUG_THIS", "onClick: buyerAccount = " + userid + " listing = " + transaction.listingId);
            objRequest.put("$class", "org.acme.vehicle.auction.Buy");
            objRequest.put("buyerAccount", beepBoopAccountPrefix + userid);
            objRequest.put("listing", creditListingPrefix + transaction.listingId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest
                (Request.Method.POST, salesListingIdURL, objRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("asdf", "Buy completed");
                        Toast.makeText(BuyFragment.this.getContext(),
                                "Purchase complete!", Toast.LENGTH_SHORT).show();
                        getTransactions();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                        Toast.makeText(context, R.string.buy_error_invalid, Toast.LENGTH_LONG).show();
                        getTransactions();
                    }
                });
        String url ="http://beepboop.eastus.cloudapp.azure.com:3000/api/BeepBoopAccount/" + userid;
        JsonObjectRequest moneyRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double m = response.getDouble("cashBalance");
                            if (attached && m < moneyAmount) {
                                Toast.makeText(context, R.string.buy_error_money, Toast.LENGTH_SHORT).show();
                            } else {
                                // Access the RequestQueue through your singleton class.
                                MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });
        MySingleton.getInstance(context).addToRequestQueue(moneyRequest);
    }
}
