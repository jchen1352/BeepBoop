package org.jeff.beepboop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

public class MyFragment extends Fragment {

    protected Context context;
    protected boolean attached = false;
    protected String userid;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        attached = true;
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        userid = prefs.getString(getString(R.string.pref_user), "fail");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        attached = false;
    }
}
