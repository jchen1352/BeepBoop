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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SellFragment extends MyFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_sell, container, false);
        Button b = v.findViewById(R.id.sell_button);
        final EditText creditEdit = v.findViewById(R.id.credit_edit);
        final EditText moneyEdit = v.findViewById(R.id.money_edit);
        TextView creditIcon = v.findViewById(R.id.sell_credit_icon);
        creditIcon.setTypeface(FontManager.getTypeface(v.getContext(), FontManager.FONTAWESOME));
        //moneyEdit.addTextChangedListener(new MoneyTextWatcher(moneyEdit));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String money = moneyEdit.getText().toString();
                final String credits = creditEdit.getText().toString();
                final double creditAmount;
                try {
                    creditAmount = Integer.parseInt(credits);
                    if (creditAmount <= 0) {
                        Toast.makeText(context, R.string.sell_error_credits, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, getString(R.string.sell_error_empty, "Credits"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (money.equals("")) {
                    Toast.makeText(context, getString(R.string.sell_error_empty, "Money"), Toast.LENGTH_SHORT).show();
                    return;
                }
                final double moneyAmount = Integer.parseInt(money);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.sell_dialog, creditEdit.getText().toString(),
                        moneyEdit.getText().toString()))
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeSellRequest(moneyAmount, creditAmount);
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

    private void makeSellRequest(double moneyAmount, final double creditAmount) {
        String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
        String beepBoopAccountPrefix = "org.acme.vehicle.auction.BeepBoopAccount#";
        JSONObject objRequest = new JSONObject();
        try {
            objRequest.put("$class", "org.acme.vehicle.auction.CreditListing");
            objRequest.put("listingId", (new Date()).toString() + userid);
            objRequest.put("sellerAccount", beepBoopAccountPrefix + userid);
            objRequest.put("price", Double.toString(moneyAmount));
            objRequest.put("numCredits", Double.toString(creditAmount));
            objRequest.put("state", "FOR_SALE");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest
                (Request.Method.POST, salesListingIdURL, objRequest, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("asdf", "Credit Listing posted");
                        Toast.makeText(context, "Credit listing posted!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });
        String url ="http://beepboop.eastus.cloudapp.azure.com:3000/api/BeepBoopAccount/" + userid;
        JsonObjectRequest creditRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double c = response.getDouble("creditBalance");
                            if (attached && c < creditAmount) {
                                Toast.makeText(context, R.string.sell_error_credits2, Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(context).addToRequestQueue(creditRequest);
    }
}
