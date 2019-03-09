package org.jeff.beepboop.Walkthrough;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.jeff.beepboop.LoginActivity;
import org.jeff.beepboop.R;
import org.jeff.beepboop.SplashActivity;
import org.jeff.beepboop.Toolbox.BitmapScaler;

public class WalkthroughSlideFive extends Fragment {

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;


    public WalkthroughSlideFive() {
        // Required empty public constructor
    }

    public static WalkthroughSlideFive newInstance() {
        WalkthroughSlideFive fragment = new WalkthroughSlideFive();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_walkthrough_slide_five, container, false);
        if (getActivity() != null) {
            ImageView imageView1 = v.findViewById(R.id.slide_five_image_one);
            bitmap1 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.slide_four_one);
//            imageView1.setImageBitmap(Bitmap.createScaledBitmap(bitmap1, 600, 400, false));
            imageView1.setImageBitmap(BitmapScaler.scaleToFitHeight(bitmap1, 600));

            ImageView imageView2 = v.findViewById(R.id.slide_five_image_two);
            bitmap2 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.slide_four_two);
//            imageView2.setImageBitmap(Bitmap.createScaledBitmap(bitmap2, 600, 600, false));
            imageView2.setImageBitmap(BitmapScaler.scaleToFitHeight(bitmap2, 600));

            ImageView imageView3 = v.findViewById(R.id.slide_five_image_three);
            bitmap3 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.slide_four_three);
//            imageView3.setImageBitmap(Bitmap.createScaledBitmap(bitmap3, 600, 600, false));
            imageView3.setImageBitmap(BitmapScaler.scaleToFitHeight(bitmap3, 600));

            Button button = v.findViewById(R.id.slide_five_get_started);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() != null) {
                        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
                        prefs.edit().putBoolean(getString(R.string.pref_install), true).apply();
                        Intent intent = new Intent(getActivity().getBaseContext(), SplashActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (bitmap1 != null) {
            bitmap1.recycle();
            bitmap1 = null;
        }

        if (bitmap2 != null) {
            bitmap2.recycle();
            bitmap2 = null;
        }

        if (bitmap3 != null) {
            bitmap3.recycle();
            bitmap3 = null;
        }
        super.onDetach();
    }
}
