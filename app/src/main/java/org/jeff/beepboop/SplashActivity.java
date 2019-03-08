package org.jeff.beepboop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.jeff.beepboop.Walkthrough.IntroActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView splashIcon;
    private Bitmap splashBitmap;
    private Handler uiHandler;
    private Runnable redirectUserRunnable;
    private final long ANIMATION_WAIT_TIME = 2500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupSplashIcon();
        uiHandler = new Handler(Looper.getMainLooper());
        redirectUserRunnable = new Runnable() {
            @Override
            public void run() {
                redirectUser();
            }
        };
    }

    public void redirectUser() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
                Intent intent = new Intent(this, NoConnectionActivity.class);
                startActivity(intent);
            } else if (!prefs.contains(getString(R.string.pref_install))) {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
            } else if (prefs.contains(getString(R.string.pref_user))) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class); // replace with login activity
                startActivity(intent);
            }
        }
    }

    public void startSplashIcon() {
        Animation pulse = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.pulse);
        if (splashIcon != null && pulse != null) {
            splashIcon.startAnimation(pulse);
        }
    }

    public void endSplashIcon() {
        if (splashIcon != null) {
            splashIcon.clearAnimation();
        }
    }

    private void setupSplashIcon() {
        splashIcon = (ImageView) findViewById(R.id.splashIcon);
        splashBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.beep_boop_logo);
        splashIcon.setImageBitmap(Bitmap.createScaledBitmap(splashBitmap, 800, 800, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSplashIcon();
        if (uiHandler != null && redirectUserRunnable != null) {
            uiHandler.postDelayed(redirectUserRunnable, ANIMATION_WAIT_TIME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        endSplashIcon();
        if (uiHandler != null) {
            uiHandler.removeCallbacks(redirectUserRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashBitmap != null) {
            splashBitmap.recycle();
            splashBitmap = null;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
