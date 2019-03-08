package org.jeff.beepboop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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

        if (getActivity() != null) {
            transactions = new ArrayList<>();
            adapter = new BuyAdapter(getActivity(), R.layout.transaction_item, transactions);
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
                    AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
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
                            if (getActivity() != null && getActivity().getResources() != null) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                                        ResourcesCompat.getColor(getActivity().getResources(), R.color.IndianRed, null));
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                        ResourcesCompat.getColor(getActivity().getResources(), R.color.Green, null));
                            }
                        }
                    });
                    dialog.show();
                }
            });
        }


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
                                Collections.sort(transactions, new Comparator<Transaction>() {
                                    @Override
                                    public int compare(Transaction transaction, Transaction t1) {
                                        return Integer.compare(transaction.cash, t1.cash);
                                    }
                                });
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest0);
    }

    private void tryBuy(final double moneyAmount, Transaction transaction) {
        final ToastCreator toast = new ToastCreator(getActivity(), "Processing...", Toast.LENGTH_SHORT);
        toast.show();

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
                        toast.cancel();
                        new ToastCreator(getActivity(), "Purchase Complete!", Toast.LENGTH_SHORT).show();
                        getTransactions();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parseVolleyError(error);
                        error.printStackTrace();
                        toast.cancel();
                        new ToastCreator(getActivity(), "Purchase Complete!", Toast.LENGTH_LONG).show();
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
                                new ToastCreator(getActivity(), "Not enough balance!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Access the RequestQueue through your singleton class.
                                MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest0);
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(moneyRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONObject errors = data.getJSONObject("error");
            Log.d("BUY_FRAG", "parseVolleyError: " + errors.toString());
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
