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

public class SellFragment extends Fragment {

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
        View v = inflater.inflate(R.layout.activity_sell, container, false);
        Button b = v.findViewById(R.id.sell_button);
        final EditText creditEdit = v.findViewById(R.id.credit_edit);
        final EditText moneyEdit = v.findViewById(R.id.money_edit);
        TextView creditIcon = v.findViewById(R.id.sell_credit_icon);
        creditIcon.setTypeface(FontManager.getTypeface(v.getContext(), FontManager.FONTAWESOME));
        //moneyEdit.addTextChangedListener(new MoneyTextWatcher(moneyEdit));
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        final String userid = prefs.getString(getString(R.string.pref_user), "fail");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.sell_dialog, creditEdit.getText().toString(),
                        moneyEdit.getText().toString()))
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ToastCreator toastCreator = new ToastCreator(SellFragment.this.getContext(), "Processing...", Toast.LENGTH_SHORT);
                                toastCreator.show();

                                String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
                                String beepBoopAccountPrefix = "org.acme.vehicle.auction.BeepBoopAccount#";
                                JSONObject objRequest = new JSONObject();
                                try {
                                    objRequest.put("$class", "org.acme.vehicle.auction.CreditListing");
                                    objRequest.put("listingId", (new Date()).toString() + userid);
                                    objRequest.put("sellerAccount", beepBoopAccountPrefix + userid);
                                    objRequest.put("price", moneyEdit.getText().toString());
                                    objRequest.put("numCredits", creditEdit.getText().toString());
                                    objRequest.put("state", "FOR_SALE");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest
                                        (Request.Method.POST, salesListingIdURL, objRequest, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("asdf", "Credit Listing posted");
                                                toastCreator.cancel();
                                                new ToastCreator(context, "Credit Listing posted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO: Handle error
                                                toastCreator.cancel();
                                                new ToastCreator(context, "An error occurred. Please try again later", Toast.LENGTH_SHORT).show();
                                                error.printStackTrace();
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
}
