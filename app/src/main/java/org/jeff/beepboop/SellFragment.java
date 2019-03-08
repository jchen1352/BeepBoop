package org.jeff.beepboop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

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
                        Toast.makeText(getActivity(), R.string.sell_error_credits, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), getString(R.string.sell_error_empty, "Credits"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (money.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.sell_error_empty, "Money"), Toast.LENGTH_SHORT).show();
                    return;
                }
                final double moneyAmount = Integer.parseInt(money);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        if (getActivity() != null && getActivity().getResources() != null) {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                                    ResourcesCompat.getColor(getActivity().getResources(), R.color.IndianRed, null));
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                                    ResourcesCompat.getColor(getActivity().getResources(), R.color.Green, null));
                        }
                    }
                });
                dialog.show();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.box1);
                }
            }
        });
        return v;
    }

    private void makeSellRequest(double moneyAmount, final double creditAmount) {
        final ToastCreator toast = new ToastCreator(getActivity(), "Processing...", Toast.LENGTH_SHORT);
        toast.show();

        String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
        String beepBoopAccountPrefix = "org.acme.vehicle.auction.BeepBoopAccount#";
        JSONObject objRequest = new JSONObject();
        try {
            objRequest.put("$class", "org.acme.vehicle.auction.CreditListing");
            objRequest.put("listingId", (new Random().nextDouble() + new Date().toString() + userid));
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
                        toast.cancel();
                        new ToastCreator(getActivity(), "Credit listing posted!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        parseVolleyError(error);
                        toast.cancel();
                        new ToastCreator(getActivity(), "Credit Listing posted!", Toast.LENGTH_LONG).show();
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
                                new ToastCreator(getActivity(), "Not enough credits!", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(creditRequest);
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONObject errors = data.getJSONObject("error");
            Log.d("SELL_FRAG", "parseVolleyError: " + errors.toString());
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }
}
