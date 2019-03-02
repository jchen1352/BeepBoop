package org.jeff.beepboop;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import javax.security.auth.login.LoginException;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private Context context;
    private boolean attached = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        attached = true;
    }

    private void logout() {



        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // remove shared preference userId from
                if (getActivity() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
                    prefs.edit().remove(getActivity().getString(R.string.pref_user)).commit();

                    // open splash activity and clear activity stack
                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);
        TextView name = v.findViewById(R.id.profile_name);
        TextView profileIcon = v.findViewById(R.id.profile_icon);
        profileIcon.setTypeface(FontManager.getTypeface(v.getContext(), FontManager.FONTAWESOME));

        final ListView lv = v.findViewById(R.id.transaction_history);

        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        final String id = prefs.getString(getString(R.string.pref_user), "hmm");
        name.setText(id);

        TextView logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        logout.setTypeface(FontManager.getTypeface(v.getContext(), FontManager.FONTAWESOME));

        final ArrayList<Transaction> transactions = new ArrayList<>(); // ALL LOGS
        final TransactionAdapter adapter = new TransactionAdapter(context, R.layout.transaction_item, transactions);

        // GET ALL SALES FIRST
        String salesListingIdURL = "http://beepboop.eastus.cloudapp.azure.com:3000/api/CreditListing";
        JsonArrayRequest jsonObjectRequest0 = new JsonArrayRequest
                (Request.Method.GET, salesListingIdURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (attached) {
                            try {
                                for (int i = 0; i < response.length(); i += 1) {
                                    JSONObject obj = response.getJSONObject(i);
                                    Log.d("asdf", obj.toString());
                                    if (obj.getString("sellerAccount").endsWith(id)) {
                                        final int numCredits = (int) obj.getDouble("numCredits");
                                        final int price = (int) obj.getDouble("price");
                                        final String listingId = obj.getString("listingId");
                                        final String status = obj.getString("state");
                                        transactions.add(new Transaction(numCredits, price, listingId, status));
                                        lv.requestLayout();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
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

        lv.setAdapter(adapter);

        // Set balance
        final TextView balance = v.findViewById(R.id.balance);
        final TextView credits = v.findViewById(R.id.credits);
        String url ="http://beepboop.eastus.cloudapp.azure.com:3000/api/BeepBoopAccount/" + id;

        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (attached) {
                                double b = response.getDouble("cashBalance");
                                balance.setText(getString(R.string.balance, (int) b));
                                double c = response.getDouble("creditBalance");
                                credits.setText(getString(R.string.credits, (int) c));
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

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest2);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        attached = false;
    }
}
