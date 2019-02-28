package org.jeff.beepboop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
        uiHandler = new Handler(Looper.getMainLooper());
        redirectUserRunnable = new Runnable() {
            @Override
            public void run() {
                redirectUser();
            }
        };
    }

    public void redirectUser() {
        Intent intent = new Intent(this, MainActivity.class); // replace with login activity
        startActivity(intent);
        finish();
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
                R.drawable.beepBoopLogo);
        splashIcon.setImageBitmap(Bitmap.createScaledBitmap(splashBitmap, 360, 360, false));
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
}
