package org.jeff.beepboop;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.jeff.beepboop.Toolbox.RandomStringGenerator;
import org.json.JSONException;
import org.json.JSONObject;

public class QRCodeFragment extends MyFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_qrcode, container, false);

        final EditText gallonsBought = (EditText) v.findViewById(R.id.qrCodeFuel);
        final Button buyFuel = (Button) v.findViewById(R.id.buy_fuel_button);

        buyFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Toast toast = Toast.makeText(getActivity(), "Processing...", Toast.LENGTH_SHORT);
                toast.show();
                String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
                final String beepBoopAccountPrefix = "org.acme.vehicle.auction.BeepBoopAccount#";
                final String buyListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/Buy";
                final String creditListingPrefix = "org.acme.vehicle.auction.CreditListing#";
                final String listingId = (RandomStringGenerator.randomString(50));

                int fuelBought = Integer.parseInt(gallonsBought.getText().toString());
                int creditAmount = 15 * fuelBought;

                JSONObject objRequestSell = new JSONObject();
                try {
                    objRequestSell.put("$class", "org.acme.vehicle.auction.CreditListing");
                    objRequestSell.put("listingId", listingId);
                    objRequestSell.put("sellerAccount", beepBoopAccountPrefix + userid);
                    objRequestSell.put("price", Double.toString(0.0));
                    objRequestSell.put("numCredits", Integer.toString(creditAmount));
                    objRequestSell.put("state", "FOR_SALE");

                    final JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest
                            (Request.Method.POST, salesListingIdURL, objRequestSell, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("QR", "Petrol Bought");
                                    JSONObject objRequest = new JSONObject();
                                    try {
                                        objRequest.put("$class", "org.acme.vehicle.auction.Buy");
                                        objRequest.put("buyerAccount", beepBoopAccountPrefix + "petrolPump");
                                        objRequest.put("listing", creditListingPrefix + listingId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    final JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest
                                            (Request.Method.POST, buyListingIdURL, objRequest, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Log.d("QR", "Credits consumed");
                                                    if (attached) {
                                                        toast.cancel();
                                                        Toast.makeText(getActivity(), "Availed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                }
                                            });
                                    jsonObjectRequest0.setRetryPolicy(new DefaultRetryPolicy(0,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest0);
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    jsonObjectRequest0.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }
}
