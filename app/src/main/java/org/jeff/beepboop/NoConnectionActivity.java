package org.jeff.beepboop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NoConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
